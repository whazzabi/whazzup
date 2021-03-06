package io.github.whazzabi.whazzup.business.datadog;

import io.github.whazzabi.whazzup.TestTeam;
import io.github.whazzabi.whazzup.business.check.checkresult.CheckResult;
import io.github.whazzabi.whazzup.business.customization.Team;
import io.github.whazzabi.whazzup.presentation.State;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class DataDogCheckExecutorTest {

    private RestTemplate restTemplate = mock(RestTemplate.class);

    @InjectMocks
    private DataDogCheckExecutor dataDogCheckExecutor = new DataDogCheckExecutor(restTemplate);

    @Before
    public void beforeMethod() throws Exception {
        lenient().doReturn(new ResponseEntity<>(HttpStatus.CONFLICT)).when(restTemplate).getForEntity(anyString(), anyObject());
    }

    @Test
    public void executeCheck_AllResultsOk() throws Exception {

        // simulate successful backend call
        mockDatadogApiCallAndReturn(dataDogMonitor("Monitor OK", DataDogMonitor.STATE_OK), dataDogMonitor("Monitor in Error", "some_error_state"));

        // execute check
        final List<CheckResult> checkResults = dataDogCheckExecutor.executeCheck(new DataDogCheck("myCheck", null, null, null, null));

        assertEquals(2, checkResults.size());

        final List<CheckResult> red = checkResults.stream().filter((checkResult) -> State.RED == checkResult.getState()).collect(Collectors.toList());
        assertEquals(1, red.size());
        assertEquals("Monitor in Error@DataDog", red.get(0).getName());
        assertEquals("some_error_state (query: null)", red.get(0).getInfo());

        final List<CheckResult> green = checkResults.stream().filter((checkResult) -> State.GREEN == checkResult.getState()).collect(Collectors.toList());
        assertEquals(1, green.size());
        assertEquals("Monitor OK@DataDog", green.get(0).getName());
        assertEquals("OK (query: null)", green.get(0).getInfo());
    }

    @Test
    public void executeCheck_noDataMonitor() throws Exception {

        // simulate successful backend call
        mockDatadogApiCallAndReturn(dataDogMonitor("Monitor OK", DataDogMonitor.STATE_NO_DATA));

        // execute check
        final List<CheckResult> checkResults = dataDogCheckExecutor.executeCheck(new DataDogCheck("myCheck", null, null, null, null));

        assertEquals(1, checkResults.size());
        assertEquals(State.GREEN, checkResults.get(0).getState());
        assertEquals("Monitor OK@DataDog", checkResults.get(0).getName());
        assertEquals("No Data (query: null)", checkResults.get(0).getInfo());
    }

    @Test
    public void executeCheck_noDataMonitor_withNotifyNoData() throws Exception {

        // simulate successful backend call
        mockDatadogApiCallAndReturn(enableNotifyNoData(dataDogMonitor("Monitor not OK", DataDogMonitor.STATE_NO_DATA)));

        // execute check
        final List<CheckResult> checkResults = dataDogCheckExecutor.executeCheck(new DataDogCheck("myCheck", null, null, null, null));

        assertEquals(1, checkResults.size());
        assertEquals(State.RED, checkResults.get(0).getState());
        assertEquals("Monitor not OK@DataDog", checkResults.get(0).getName());
        assertEquals("No Data (query: null)", checkResults.get(0).getInfo());
    }

    @Test
    public void executeCheck_WithHttpError() throws Exception {

        // simulate some code other than 200
        mockDatadogApiCallAndReturn(HttpStatus.BAD_GATEWAY);

        // execute check
        final List<CheckResult> checkResults = dataDogCheckExecutor.executeCheck(new DataDogCheck("myCheck", null, null, null, "[yana]").withTeamMapping(TestTeam.INSTANCE));

        assertEquals(1, checkResults.size());

        final CheckResult checkResult = checkResults.get(0);

        assertEquals(State.RED, checkResult.getState());
        assertEquals("DataDog", checkResult.getName());
        assertEquals("got http 502 BAD_GATEWAY", checkResult.getInfo());
        assertEquals(1, checkResult.getTeams().size());
        assertEquals(TestTeam.INSTANCE.getTeamName(), checkResult.getTeams().get(0));
    }

    @Test
    public void convertMonitorToCheckResult() {

        final CheckResult checkResult = dataDogCheckExecutor.convertMonitorToCheckResult(dataDogMonitor("name", DataDogMonitor.STATE_OK), dataDogCheck());
        assertEquals(State.GREEN, checkResult.getState());
        assertEquals(1, checkResult.getTestCount());
        assertEquals(0, checkResult.getFailCount());
        assertEquals("name@DataDog", checkResult.getName());
        assertEquals("OK (query: null)", checkResult.getInfo());
        assertEquals("https://app.datadoghq.com/monitors#status?id=null&group=all", checkResult.getLink());
        assertNull(checkResult.getGroup());
        assertEquals(0, checkResult.getTeams().size());
    }

    @Test
    public void convertMonitorToCheckResult_Alert() {

        final CheckResult checkResult = dataDogCheckExecutor.convertMonitorToCheckResult(dataDogMonitor("name", "alert"), dataDogCheck());
        assertEquals(State.RED, checkResult.getState());
        assertEquals(1, checkResult.getTestCount());
        assertEquals(1, checkResult.getFailCount());
        assertEquals("name@DataDog", checkResult.getName());
        assertEquals("alert (query: null)", checkResult.getInfo());
        assertEquals("https://app.datadoghq.com/monitors#status?id=null&group=all", checkResult.getLink());
        assertNull(checkResult.getGroup());
        assertEquals(0, checkResult.getTeams().size());
    }

    @Test
    public void convertMonitorToCheckResult_Silenced() {

        final CheckResult checkResult = dataDogCheckExecutor.convertMonitorToCheckResult(dataDogMonitor("name", "alert"), dataDogCheck());
        assertEquals(State.RED, checkResult.getState());
        assertEquals(1, checkResult.getTestCount());
        assertEquals(1, checkResult.getFailCount());
        assertEquals("name@DataDog", checkResult.getName());
        assertEquals("alert (query: null)", checkResult.getInfo());
        assertEquals("https://app.datadoghq.com/monitors#status?id=null&group=all", checkResult.getLink());
        assertNull(checkResult.getGroup());
        assertEquals(0, checkResult.getTeams().size());
    }

    @Test
    public void convertMonitorToCheckResult_Downtime() {

        DataDogMonitor dataDogMonitor = addActiveDowntime(dataDogMonitor("name", "alert"));

        final CheckResult checkResult = dataDogCheckExecutor.convertMonitorToCheckResult(dataDogMonitor, dataDogCheck());
        assertEquals(State.GREEN, checkResult.getState());
        assertEquals(1, checkResult.getTestCount());
        assertEquals(0, checkResult.getFailCount());
        assertEquals("name@DataDog", checkResult.getName());
        assertEquals("MAINTENANCE!", checkResult.getInfo());
        assertEquals("https://app.datadoghq.com/monitors#status?id=null&group=all", checkResult.getLink());
        assertNull(checkResult.getGroup());
        assertEquals(0, checkResult.getTeams().size());
    }

    @Test
    public void decideTeams() {

        assertNull(dataDogCheckExecutor.decideTeams("[foo]some_monitor", teamMappings(), null));
        assertNull(dataDogCheckExecutor.decideTeams("some_monitor", teamMappings(), null));
        assertEquals(Collections.singletonList(TestTeam.INSTANCE), dataDogCheckExecutor.decideTeams("[yana][cm]some_monitor", teamMappings(), null));
        assertEquals(Collections.singletonList(TestTeam.INSTANCE), dataDogCheckExecutor.decideTeams("[yana][foo][cm]some_monitor", teamMappings(), null));
        assertEquals(Collections.singletonList(TestTeam.INSTANCE), dataDogCheckExecutor.decideTeams("[cm]some_monitor", teamMappings(), null));
    }

    @Test
    public void decideTeams_withTwoTeams() {

        assertEquals(Arrays.asList(new Team[]{TestTeam.INSTANCE, TestTeam.INSTANCE}), dataDogCheckExecutor.decideTeams("[yana][cm]some_monitor", teamMultipleMappings(), null));
    }

    @Test
    public void defaultStateMapper() {
        mockDatadogApiCallAndReturn(dataDogMonitor("[YANA]Monitor", DataDogMonitor.STATE_ALERT));

        List<CheckResult> checkResults = dataDogCheckExecutor.executeCheck(dataDogCheck());
        assertEquals(1, checkResults.size());
        assertEquals(State.RED, checkResults.get(0).getState());
    }

    @Test
    public void customStateMapper() {
        DataDogCheck check = dataDogCheck();
        check.withTriggeredStateMapper(monitor -> State.YELLOW);

        mockDatadogApiCallAndReturn(dataDogMonitor("[YANA]Monitor", DataDogMonitor.STATE_ALERT));

        List<CheckResult> checkResults = dataDogCheckExecutor.executeCheck(check);
        assertEquals(1, checkResults.size());
        assertEquals(State.YELLOW, checkResults.get(0).getState());
    }

    private DataDogCheck dataDogCheck() {
        return new DataDogCheck("name", null, "apiKey", "appKey", null);
    }

    private DataDogMonitor dataDogMonitor(String name, String overallState) {
        DataDogMonitor monitor = new DataDogMonitor();
        ReflectionTestUtils.setField(monitor, "name", name);
        ReflectionTestUtils.setField(monitor, "overallState", overallState);

        return monitor;
    }

    private DataDogMonitor addActiveDowntime(DataDogMonitor monitor) {
        DataDogMonitorOptions options = new DataDogMonitorOptions();

        DataDogDowntime dataDogDowntime = new DataDogDowntime();
        dataDogDowntime.active = true;

        ReflectionTestUtils.setField(monitor, "matchingDowntimes", new DataDogDowntime[]{ dataDogDowntime });
        return monitor;
    }

    private DataDogMonitor enableNotifyNoData(DataDogMonitor monitor) {
        DataDogMonitorOptions options = new DataDogMonitorOptions();
        ReflectionTestUtils.setField(options, "notifyNoData", true);
        ReflectionTestUtils.setField(monitor, "options", options);
        return monitor;
    }

    private Map<String, List<Team>> teamMappings() {

        Map<String, List<Team>> teamMappings = new HashMap<>();
        teamMappings.put("[cm]", Arrays.asList(new Team[]{TestTeam.INSTANCE}));
        return teamMappings;
    }

    private Map<String, List<Team>> teamMultipleMappings() {

        Map<String, List<Team>> teamMappings = new HashMap<>();
        teamMappings.put("[cm]", Arrays.asList(new Team[]{TestTeam.INSTANCE, TestTeam.INSTANCE}));
        return teamMappings;
    }

    private void mockDatadogApiCallAndReturn(DataDogMonitor... monitors) {
        when(restTemplate.getForEntity(any(), eq(DataDogMonitor[].class))).thenReturn(new ResponseEntity<>(monitors, HttpStatus.OK));
    }

    private void mockDatadogApiCallAndReturn(HttpStatus httpStatus) {
        when(restTemplate.getForEntity(any(), eq(DataDogMonitor[].class))).thenReturn(new ResponseEntity<>(httpStatus));
    }
}

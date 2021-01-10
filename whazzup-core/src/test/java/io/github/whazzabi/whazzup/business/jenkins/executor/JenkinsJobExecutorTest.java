package io.github.whazzabi.whazzup.business.jenkins.executor;

import io.github.whazzabi.whazzup.business.check.checkresult.CheckResult;
import io.github.whazzabi.whazzup.business.customization.Group;
import io.github.whazzabi.whazzup.business.jenkins.JenkinsCheck;
import io.github.whazzabi.whazzup.business.jenkins.JenkinsClient;
import io.github.whazzabi.whazzup.business.jenkins.JenkinsServerConfiguration;
import io.github.whazzabi.whazzup.business.jenkins.domain.BuildInfo;
import io.github.whazzabi.whazzup.business.jenkins.domain.JenkinsJobInfo;
import io.github.whazzabi.whazzup.business.jenkins.joblist.JenkinsJobNameMapper;
import io.github.whazzabi.whazzup.presentation.State;
import org.apache.http.auth.AuthenticationException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class JenkinsJobExecutorTest {

    private JenkinsClient jenkinsClient = mock(JenkinsClient.class);

    private JenkinsJobExecutor jenkinsJobExecutor = new JenkinsJobExecutor(jenkinsClient, new JenkinsJobToStateMapper());

    @Before
    public void initMocks() throws Exception {
        lenient().when(jenkinsClient.queryApi(anyString(), any(JenkinsServerConfiguration.class), eq(JenkinsJobInfo.class))).thenReturn(jenkinsJobInfo());
    }

    @Test
    public void testGetShortNameWithoutJobNameMapper() {

        JenkinsCheck jenkinsCheck = jenkinsCheck("my-name-is-kept-untouched", null);

        final String shortName = jenkinsJobExecutor.shortName(jenkinsCheck);

        assertEquals("my-name-is-kept-untouched", shortName);
    }

    @Test
    public void testGetShortNameWithJobNameMapper() {

        JenkinsCheck jenkinsCheck = jenkinsCheck("My-Name-Is-Lower-Cased", (check) -> check.getName().toLowerCase());

        final String shortName = jenkinsJobExecutor.shortName(jenkinsCheck);

        assertEquals("my-name-is-lower-cased", shortName);
    }

    @Test
    public void testBuildWithoutLastBuildResultResultsInGreenState() throws IOException, AuthenticationException {

        final List<CheckResult> checkResults = jenkinsJobExecutor.executeCheck(jenkinsJobInfo(), jenkinsCheck(), mock(BuildInfo.class));

        assertEquals(1, checkResults.size());
        final CheckResult checkResult = checkResults.get(0);
        assertEquals(State.GREEN, checkResult.getState());
    }

    private JenkinsJobInfo jenkinsJobInfo() {
        JenkinsJobInfo jenkinsJobInfo = new JenkinsJobInfo();
        ReflectionTestUtils.setField(jenkinsJobInfo, "buildable", true);
        return jenkinsJobInfo;
    }

    private JenkinsCheck jenkinsCheck() {
        return jenkinsCheck("", null);
    }

    private JenkinsCheck jenkinsCheck(String name, JenkinsJobNameMapper jenkinsJobNameMapper) {
        return new JenkinsCheck(name, "", "", "", mock(Group.class), mock(List.class), jenkinsJobNameMapper, "");
    }
}
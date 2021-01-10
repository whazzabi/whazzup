package io.github.whazzabi.whazzup.business.jenkins.executor;

import io.github.whazzabi.whazzup.business.check.checkresult.CheckResult;
import io.github.whazzabi.whazzup.business.jenkins.JenkinsCheck;
import io.github.whazzabi.whazzup.business.jenkins.JenkinsClient;
import io.github.whazzabi.whazzup.business.jenkins.domain.*;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JenkinsPipelineExecutorTest {

    private final static String LAST_BUILD_URL = "/foo/lastbuild";

    private final static String LAST_SUCCESSFUL_BUILD_URL = "/foo/lastsuccessfulbuild";

    private JenkinsClient client = mock(JenkinsClient.class);

    private JenkinsPipelineExecutor executor = new JenkinsPipelineExecutor(client);

    private final Build lastSuccessfulBuild = new Build(LAST_SUCCESSFUL_BUILD_URL);

    private final Build lastBuild = new Build(LAST_BUILD_URL);

    @Test
    public void testExecuteCheckWithValidPipelineDefinitionInLastSuccessfulExecution() throws Exception {

        initMockForUrl(LAST_BUILD_URL, buildInfo("stage one", "stage two"));
        initMockForUrl(LAST_SUCCESSFUL_BUILD_URL, buildInfo("stage one", "stage two", "stage three"));

        final List<CheckResult> checkResults = executor.executeCheck(new JenkinsJobInfo(lastSuccessfulBuild, lastBuild), mock(JenkinsCheck.class), mock(BuildInfo.class));
        assertEquals(3, checkResults.size());
        assertEquals("NOT EXECUTED", checkResults.get(2).getInfo());
    }

    @Test
    public void testExecuteCheckWithOutdatedPipelineDefinitionInLastSuccessfulExecution() throws Exception {

        initMockForUrl(LAST_BUILD_URL, buildInfo("stage one", "stage newTwo"));
        initMockForUrl(LAST_SUCCESSFUL_BUILD_URL, buildInfo("stage one", "stage two", "stage three"));

        final List<CheckResult> checkResults = executor.executeCheck(new JenkinsJobInfo(lastSuccessfulBuild, lastBuild), mock(JenkinsCheck.class), mock(BuildInfo.class));
        assertEquals(2, checkResults.size());
    }

    @Test
    public void testExecuteCheckWithNoLastSuccessfulExecution() throws Exception {

        initMockForUrl(LAST_BUILD_URL, buildInfo("stage one", "stage two"));

        final List<CheckResult> checkResults = executor.executeCheck(new JenkinsJobInfo(null, lastBuild), mock(JenkinsCheck.class), mock(BuildInfo.class));
        assertEquals(2, checkResults.size());
    }

    private void initMockForUrl(String url, JenkinsPipelineBuildInfo buildInfo) {
        when(client.queryWorkflowApi(eq(url), any(), any())).thenReturn(buildInfo);
    }

    private JenkinsPipelineBuildInfo buildInfo(String... stages) {

        final List<PipelineStage> pipelineStages = Stream.of(stages)
                .map(PipelineStage::new)
                .collect(Collectors.toList());
        return new JenkinsPipelineBuildInfo(JenkinsPipelineStageResult.SUCCESS, pipelineStages);
    }
}
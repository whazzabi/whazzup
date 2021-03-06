package io.github.whazzabi.whazzup.business.jenkins.executor;

import io.github.whazzabi.whazzup.business.jenkins.JenkinsCheck;
import io.github.whazzabi.whazzup.business.jenkins.JenkinsClient;
import io.github.whazzabi.whazzup.business.jenkins.domain.BuildInfo;
import io.github.whazzabi.whazzup.business.jenkins.domain.JenkinsJobInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JenkinsCheckExecutorTest {

    @Mock
    private JenkinsJobExecutor jobExecutor;

    @Mock
    private JenkinsPipelineExecutor pipelineExecutor;

    @Mock
    private JenkinsClient jenkinsClient;

    @InjectMocks
    private JenkinsCheckExecutor jenkinsCheckExecutor;

    @Test
    public void testExecuteCheckAgainstPipelineJobExecutesPipelineExecutor() throws Exception {

        execute(JenkinsJobInfo.PIPELINE_CLASS, true);

        verifyNoMoreInteractions(jobExecutor);
        verify(pipelineExecutor, times(1)).executeCheck(any(JenkinsJobInfo.class), any(JenkinsCheck.class), any(BuildInfo.class));
    }

    @Test
    public void testExecuteCheckAgainstPipelineJobButNonExplodedPipelinesExecutesJobExecutor() throws Exception {

        execute(JenkinsJobInfo.PIPELINE_CLASS, false);

        verifyNoMoreInteractions(pipelineExecutor);
        verify(jobExecutor, times(1)).executeCheck(any(JenkinsJobInfo.class), any(JenkinsCheck.class), any(BuildInfo.class));
    }

    @Test
    public void testExecuteCheckAgainstRegularJobExecutesJobExecutor() throws Exception {

        execute("some-other-class", true);

        verifyNoMoreInteractions(pipelineExecutor);
        verify(jobExecutor, times(1)).executeCheck(any(), any(), any());
    }

    private void execute(String withBuildClass, boolean withExplodePipeline) {
        final JenkinsJobInfo jobInfo = new JenkinsJobInfo();
        jobInfo.setBuildClass(withBuildClass);
        when(jenkinsClient.queryApi(any(), any(), any())).thenReturn(jobInfo);
        final JenkinsCheck check = mock(JenkinsCheck.class);
        when(check.isExplodePipelines()).thenReturn(withExplodePipeline);
        jenkinsCheckExecutor.executeCheck(check);
    }
}
package io.github.whazzabi.whazzup.business.jenkins.executor;

import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.check.CheckExecutor;
import io.github.whazzabi.whazzup.business.check.checkresult.CheckResult;
import io.github.whazzabi.whazzup.business.jenkins.JenkinsCheck;
import io.github.whazzabi.whazzup.business.jenkins.JenkinsClient;
import io.github.whazzabi.whazzup.business.jenkins.JenkinsServerConfiguration;
import io.github.whazzabi.whazzup.business.jenkins.domain.BuildInfo;
import io.github.whazzabi.whazzup.business.jenkins.domain.JenkinsJobInfo;
import io.github.whazzabi.whazzup.presentation.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class JenkinsCheckExecutor implements CheckExecutor<JenkinsCheck> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final JenkinsJobExecutor jobExecutor;

    private final JenkinsPipelineExecutor pipelineExecutor;

    private final JenkinsClient jenkinsClient;

    @Autowired
    public JenkinsCheckExecutor(JenkinsJobExecutor jobExecutor, JenkinsPipelineExecutor pipelineExecutor, JenkinsClient jenkinsClient) {

        this.jobExecutor = jobExecutor;
        this.pipelineExecutor = pipelineExecutor;
        this.jenkinsClient = jenkinsClient;
    }

    @Override
    public List<CheckResult> executeCheck(JenkinsCheck check) {

        final JenkinsServerConfiguration serverConfiguration = check.getServerConfiguration();
        final JenkinsJobInfo jobInfo = jenkinsClient.queryApi(check.getJobUrl(), serverConfiguration, JenkinsJobInfo.class);

        if (jobInfo == null) {
            log.error("error fetching jenkins result. Job query returned null: {}", check.getName());
            return Collections.singletonList(
                    new CheckResult(State.RED, check.getName(), "N/A", 0, 0, check.getGroup())
                            .withLink(check.getJobUrl())
                            .withTeams(check.getTeams()));
        }

        final BuildInfo buildInfo = check.isFetchBuildInfo() ? buildInfo(check.getJobUrl(), serverConfiguration) : new BuildInfo();

        if (jobInfo.isPipeline() && check.isExplodePipelines()) {
            return pipelineExecutor.executeCheck(jobInfo, check, buildInfo);
        }
        return jobExecutor.executeCheck(jobInfo, check, buildInfo);
    }

    private BuildInfo buildInfo(String jobUrl, JenkinsServerConfiguration serverConfiguration) {
        final BuildInfo buildInfo = jenkinsClient.query(
                jobUrl + "/lastSuccessfulBuild/artifact/" + BuildInfo.JENKINS_BUILD_INFO_FILE_NAME, serverConfiguration,
                BuildInfo.class);
        return buildInfo != null ? buildInfo : new BuildInfo();
    }

    @Override
    public boolean isApplicable(Check check) {
        return check instanceof JenkinsCheck;
    }
}

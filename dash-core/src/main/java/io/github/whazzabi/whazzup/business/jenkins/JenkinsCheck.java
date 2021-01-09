package io.github.whazzabi.whazzup.business.jenkins;

import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.customization.Group;
import io.github.whazzabi.whazzup.business.customization.Team;
import io.github.whazzabi.whazzup.business.jenkins.joblist.JenkinsJobNameMapper;

import java.util.List;

public class JenkinsCheck extends Check {

    private static final String ICON_SRC = "assets/jenkins-logo.png";

    private final JenkinsServerConfiguration serverConfiguration;

    private JenkinsJobNameMapper jenkinsJobNameMapper;

    private final String jobUrl;

    private boolean fetchBuildInfo = false;

    /**
     * determines if a pipeline should be represented by one checkresult or several (one per stage in pipeline)
     */
    private boolean explodePipelines = true;

    public JenkinsCheck(String name, String url, String userName, String apiToken, Group group, List<Team> teams, JenkinsJobNameMapper jenkinsJobNameMapper) {
        super(name, group, teams);
        this.jobUrl = url;
        this.serverConfiguration = new JenkinsServerConfiguration(url, userName, apiToken);
        this.jenkinsJobNameMapper = jenkinsJobNameMapper;
    }

    public JenkinsCheck(String name, String url, String userName, String apiToken, Group group, List<Team> teams, JenkinsJobNameMapper jenkinsJobNameMapper, String jobUrl) {
        super(name, group, teams);
        this.jobUrl = jobUrl;
        this.serverConfiguration = new JenkinsServerConfiguration(url, userName, apiToken);
        this.jenkinsJobNameMapper = jenkinsJobNameMapper;
    }

    public JenkinsCheck(String jobName, String url, JenkinsServerConfiguration serverConfig, Group group, List<Team> teams) {
        super(jobName, group, teams);
        this.serverConfiguration = serverConfig;
        this.jobUrl = url;
    }

    public JenkinsCheck(String jobName, JenkinsServerConfiguration serverConfig, Group group, List<Team> teams) {
        super(jobName, group, teams);
        this.serverConfiguration = serverConfig;
        this.jobUrl = serverConfig.getUrl() + "/job/" + jobName;
    }

    public JenkinsJobNameMapper getJenkinsJobNameMapper() {
        return jenkinsJobNameMapper;
    }

    public JenkinsCheck withJobNameMapper(JenkinsJobNameMapper jobNameMapper) {
        this.jenkinsJobNameMapper = jobNameMapper;
        return this;
    }

    public JenkinsCheck withFetchBuildInfo(boolean fetchBuildInfo) {
        this.fetchBuildInfo = fetchBuildInfo;
        return this;
    }

    public boolean isExplodePipelines() {
        return explodePipelines;
    }

    /**
     * @param explodePipelines {@link #explodePipelines}
     * @return this {@link JenkinsCheck} instance
     */
    public JenkinsCheck withExplodePipelines(boolean explodePipelines) {
        this.explodePipelines = explodePipelines;
        return this;
    }

    public JenkinsServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    @Override
    public String getIconSrc() {
        return ICON_SRC;
    }

    public boolean isFetchBuildInfo() {
        return fetchBuildInfo;
    }
}

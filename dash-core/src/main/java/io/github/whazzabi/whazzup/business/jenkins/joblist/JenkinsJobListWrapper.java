package io.github.whazzabi.whazzup.business.jenkins.joblist;


import io.github.whazzabi.whazzup.business.jenkins.domain.JenkinsElement;

import java.util.List;

public class JenkinsJobListWrapper {

    private List<JenkinsElement> jobs;

    public List<JenkinsElement> getJobs() {
        return jobs;
    }

    public void setJobs(List<JenkinsElement> jobs) {
        this.jobs = jobs;
    }
}

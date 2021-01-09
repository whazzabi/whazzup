package io.github.whazzabi.whazzup.business.jenkins.joblist;

import io.github.whazzabi.whazzup.business.jenkins.JenkinsCheck;

/**
 * Maps a jenkins check to a job name that will be displayed in the UI
 */
public interface JenkinsJobNameMapper {
    String map(JenkinsCheck check);
}
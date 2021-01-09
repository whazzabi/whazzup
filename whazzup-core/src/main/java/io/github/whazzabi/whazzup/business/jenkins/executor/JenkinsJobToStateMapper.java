package io.github.whazzabi.whazzup.business.jenkins.executor;

import io.github.whazzabi.whazzup.business.jenkins.domain.JenkinsBuildInfo;
import io.github.whazzabi.whazzup.business.jenkins.domain.JenkinsJobInfo;
import io.github.whazzabi.whazzup.business.jenkins.domain.JenkinsResult;
import io.github.whazzabi.whazzup.business.jenkins.domain.Property;
import io.github.whazzabi.whazzup.presentation.State;
import org.springframework.stereotype.Service;

@Service
public class JenkinsJobToStateMapper {

    /**
     * Helps mapping jenkins job information to a {@link State}
     */
    public State identifyStatus(JenkinsBuildInfo buildInfo, int failedTestCount, JenkinsJobInfo jobInfo) {

        if (buildInfo == null) {
            return State.GREEN;
        }

        if (failedTestCount > 0) {
            return isMaster(jobInfo) ? State.YELLOW : isActive(buildInfo) ? State.GREEN : State.GREY;
        }

        JenkinsResult result = buildInfo.getResult();
        if (result == null) {
            return isMaster(jobInfo) ? State.RED : isActive(buildInfo) ? State.GREY : State.YELLOW;
        }

        switch (result) {
            case ABORTED:
                return isMaster(jobInfo) ? State.GREY : isActive(buildInfo) ? State.GREEN : State.GREY;
            case UNSTABLE:
                // if there were only test failures, we never get here. therefore treat unstable as failed
            case FAILURE:
                return isMaster(jobInfo) ? State.YELLOW : isActive(buildInfo) ? State.GREEN : State.GREY;
            case SUCCESS:
                return State.GREEN;
            default:
                return State.GREY;
        }
    }

    private boolean isActive(JenkinsBuildInfo buildInfo) {
        long oneWeekAgo = System.currentTimeMillis() - (7 * 24 * 3600 * 1000);
        return buildInfo.getTimestamp() > oneWeekAgo;
    }

    private boolean isMaster(JenkinsJobInfo jobInfo) {
        boolean isMultiBranch = jobInfo.getProperties()
                .stream()
                .anyMatch(property -> Property.MULTIBRANCH_CLASS.equals(property.getPropertyClass()));
        if (!isMultiBranch) {
            // non multi-branch jobs always are the 'master'
            return true;
        }
        return "master".equalsIgnoreCase(jobInfo.getName());
    }
}

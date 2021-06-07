package io.github.whazzabi.whazzup.business.github.pullrequest;

import io.github.whazzabi.whazzup.business.check.checkresult.CheckResult;
import io.github.whazzabi.whazzup.business.github.common.api.GithubPullRequest;

/**
 * Decorates the CheckResult for the UI based on the check configuration and corresponding PullRequest alarm
 */
public interface GithubPullrequestResultDecorator {

    /**
     * @return the decorated check result or null if the check result should be excluded
     */
    default CheckResult decorate(CheckResult checkResult, GithubPullRequest pullRequest) {
        return checkResult;
    };
}

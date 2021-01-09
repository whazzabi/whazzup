package io.github.whazzabi.whazzup.business.jira.issuecheckresultdecorator;

import io.github.whazzabi.whazzup.business.check.checkresult.CheckResult;
import io.github.whazzabi.whazzup.business.jira.JiraCheck;
import io.github.whazzabi.whazzup.business.jira.rest.Issue;

/**
 * Decorates the CheckResult for the UI based on the check configuration and corresponding Jira issue
 */
public interface JiraIssueCheckResultDecorator {

    String info(Issue issue);

    String name(JiraCheck jiraCheck, Issue issue);

    default CheckResult decorate(CheckResult checkResult, JiraCheck jiraCheck, Issue issue) {
        return checkResult;
    };
}

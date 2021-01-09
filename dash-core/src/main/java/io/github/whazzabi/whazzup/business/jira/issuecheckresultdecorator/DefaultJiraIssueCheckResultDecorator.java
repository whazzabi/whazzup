package io.github.whazzabi.whazzup.business.jira.issuecheckresultdecorator;

import io.github.whazzabi.whazzup.business.jira.JiraCheck;
import io.github.whazzabi.whazzup.business.jira.rest.Issue;

public class DefaultJiraIssueCheckResultDecorator implements JiraIssueCheckResultDecorator {

    @Override
    public String info(Issue issue) {
        return issue.getKey();
    }

    @Override
    public String name(JiraCheck jiraCheck, Issue issue) {

        final String assignee = issue.getFields().getAssignee() == null ? "nobody" : issue.getFields().getAssignee().getName();
        return jiraCheck.getName() + " (" + assignee + ")";
    }
}

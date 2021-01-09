package io.github.whazzabi.whazzup.business.jira.issuestatemapper;

import io.github.whazzabi.whazzup.business.jira.rest.Issue;
import io.github.whazzabi.whazzup.presentation.State;

public interface JiraIssueStateMapper {

    /**
     * Maps an {@link Issue} to a {@link State}
     * @param issue The jira issue to map the state from
     * @return The mapped state
     */
    State mapToState(Issue issue);
}

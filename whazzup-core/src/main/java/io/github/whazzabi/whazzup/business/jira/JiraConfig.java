package io.github.whazzabi.whazzup.business.jira;

import io.github.whazzabi.whazzup.business.jira.issuecheckresultdecorator.DefaultJiraIssueCheckResultDecorator;
import io.github.whazzabi.whazzup.business.jira.issuecheckresultdecorator.JiraIssueCheckResultDecorator;
import io.github.whazzabi.whazzup.business.jira.issuestatemapper.DefaultJiraIssueStateMapper;
import io.github.whazzabi.whazzup.business.jira.issuestatemapper.JiraIssueStateMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JiraConfig {

    @ConditionalOnMissingBean
    @Bean
    public JiraIssueStateMapper issueStateMapper() {
        return new DefaultJiraIssueStateMapper();
    }

    @ConditionalOnMissingBean
    @Bean
    public JiraIssueCheckResultDecorator checkResultDecorator() {
        return new DefaultJiraIssueCheckResultDecorator();
    }
}

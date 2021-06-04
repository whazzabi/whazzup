package io.github.whazzabi.whazzup.business.github.pullrequest;

import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.customization.Group;
import io.github.whazzabi.whazzup.business.customization.Team;
import io.github.whazzabi.whazzup.business.github.GithubConfig;
import io.github.whazzabi.whazzup.business.github.common.GithubRepositoryMatcher;

import java.util.List;

public class GithubPullRequestsCheck extends Check {

    private final GithubConfig githubConfig;

    /**
     * if specified the pr results will be filtered down to prs containing this keyword
     */
    private String filterKeyword;

    private GithubRepositoryMatcher githubRepositoryMatcher = new GithubRepositoryMatcher();

    /**
     * eg. orgs/whazzup or user/waschnick
     */
    private final String githubFullyQualifiedName;

    public GithubPullRequestsCheck(String name, Group group, List<Team> teams, GithubConfig githubConfig, String githubFullyQualifiedName) {
        super(name, group, teams);
        this.githubConfig = githubConfig;
        this.githubFullyQualifiedName = githubFullyQualifiedName;
    }

    /**
     * @deprecated Use GithubRepositoryMatcher instead, will be remove with 2.4.0
     */
    @Deprecated
    public GithubPullRequestsCheck(String name, Group group, List<Team> teams, GithubConfig githubConfig, String githubFullyQualifiedName, String repoNameRegex) {
        super(name, group, teams);
        this.githubConfig = githubConfig;
        githubRepositoryMatcher.withRepoNameRegex(repoNameRegex);
        this.githubFullyQualifiedName = githubFullyQualifiedName;
    }

    public GithubPullRequestsCheck withFilterKeyword(String filterKeyword) {
        this.filterKeyword = filterKeyword;
        return this;
    }

    public GithubPullRequestsCheck withGithubRepositoryMatcher(GithubRepositoryMatcher githubRepositoryMatcher) {
        this.githubRepositoryMatcher = githubRepositoryMatcher;
        return this;
    }

    public GithubConfig githubConfig() {
        return githubConfig;
    }

    /**
     * @deprecated Use GithubRepositoryMatcher instead, will be remove with 2.4.0
     */
    @Deprecated
    public String repoNameRegex() {
        return githubRepositoryMatcher.repoNameRegex();
    }

    public GithubRepositoryMatcher githubRepositoryMatcher() {
        return githubRepositoryMatcher;
    }

    public String githubFullyQualifiedName() {
        return githubFullyQualifiedName;
    }

    @Deprecated
    public String getFilterKeyword() {
        return filterKeyword;
    }

    @Override
    public String getIconSrc() {
        return "assets/github-logo.png";
    }

    @Override
    public long runEachNthCheck() {
        return 5;
    }
}

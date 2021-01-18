package io.github.whazzabi.whazzup.business.github.pullrequest;

import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.customization.Group;
import io.github.whazzabi.whazzup.business.customization.Team;
import io.github.whazzabi.whazzup.business.github.GithubConfig;

import java.util.List;

public class GithubPullRequestsCheck extends Check {

    private final GithubConfig githubConfig;

    private String repoNameRegex = "";

    /**
     * if specified the pr results will be filtered down to prs containing this keyword
     */
    private String filterKeyword;

    /**
     * eg. orgs/whazzup or user/waschnick
     */
    private final String githubFullyQualifiedName;

    public GithubPullRequestsCheck(String name, Group group, List<Team> teams, GithubConfig githubConfig, String githubFullyQualifiedName, String repoNameRegex) {
        super(name, group, teams);
        this.githubConfig = githubConfig;
        this.repoNameRegex = repoNameRegex;
        this.githubFullyQualifiedName = githubFullyQualifiedName;
    }

    public GithubConfig githubConfig() {
        return githubConfig;
    }

    public String repoNameRegex() {
        return repoNameRegex;
    }

    public String githubFullyQualifiedName() {
        return githubFullyQualifiedName;
    }

    public GithubPullRequestsCheck withFilterKeyword(String filterKeyword) {
        this.filterKeyword = filterKeyword;
        return this;
    }

    public String getFilterKeyword() {
        return filterKeyword;
    }

    @Override
    public String getIconSrc() {
        return "assets/github-logo.png";
    }
}

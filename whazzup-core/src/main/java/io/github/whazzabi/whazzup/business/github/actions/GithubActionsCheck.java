package io.github.whazzabi.whazzup.business.github.actions;

import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.customization.Group;
import io.github.whazzabi.whazzup.business.customization.Team;
import io.github.whazzabi.whazzup.business.github.GithubConfig;
import io.github.whazzabi.whazzup.business.github.common.GithubRepositoryMatcher;

import java.util.List;

public class GithubActionsCheck extends Check {

    private final GithubConfig githubConfig;

    private GithubRepositoryMatcher githubRepositoryMatcher = new GithubRepositoryMatcher();

    private BranchesOfRepositorySupplier branchesOfRepository = new MainBranchSupplier();

    /**
     * eg. orgs/whazzup or user/waschnick
     */
    private final String githubFullyQualifiedName;

    public GithubActionsCheck(String name, Group group, List<Team> teams, GithubConfig githubConfig, String githubFullyQualifiedName) {
        super(name, group, teams);
        this.githubConfig = githubConfig;
        this.githubFullyQualifiedName = githubFullyQualifiedName;
    }

    /**
     * @deprecated Use GithubRepositoryMatcher instead, will be remove with 2.4.0
     */
    @Deprecated
    public GithubActionsCheck(String name, Group group, List<Team> teams, GithubConfig githubConfig, String githubFullyQualifiedName, String repoNameRegex) {
        super(name, group, teams);
        this.githubConfig = githubConfig;
        githubRepositoryMatcher.withRepoNameRegex(repoNameRegex);
        this.githubFullyQualifiedName = githubFullyQualifiedName;
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

    public GithubActionsCheck withGithubRepositoryMatcher(GithubRepositoryMatcher githubRepositoryMatcher) {
        this.githubRepositoryMatcher = githubRepositoryMatcher;
        return this;
    }


    public GithubActionsCheck withBranchesOfRepository(BranchesOfRepositorySupplier branchesOfRepository) {
        this.branchesOfRepository = branchesOfRepository;
        return this;
    }

    public BranchesOfRepositorySupplier branchesOfRepository() {
        return branchesOfRepository;
    }

    public String githubFullyQualifiedName() {
        return githubFullyQualifiedName;
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

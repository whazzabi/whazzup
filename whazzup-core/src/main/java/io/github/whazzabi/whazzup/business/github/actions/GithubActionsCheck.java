package io.github.whazzabi.whazzup.business.github.actions;

import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.customization.Group;
import io.github.whazzabi.whazzup.business.customization.Team;
import io.github.whazzabi.whazzup.business.github.GithubConfig;

import java.util.List;

public class GithubActionsCheck extends Check {

    private final GithubConfig githubConfig;

    private final String repoNameRegex;

    private BranchesOfRepositorySupplier branchesOfRepository = new MainBranchSupplier();

    /**
     * eg. orgs/whazzup or user/waschnick
     */
    private final String githubFullyQualifiedName;

    public GithubActionsCheck(String name, Group group, List<Team> teams, GithubConfig githubConfig, String githubFullyQualifiedName, String repoNameRegex) {
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

    public GithubActionsCheck withBranchesOfRepository(BranchesOfRepositorySupplier branchesOfRepository) {
        this.branchesOfRepository = branchesOfRepository;
        return this;
    }

    public BranchesOfRepositorySupplier branchesOfRepository() {
        return branchesOfRepository;
    };

    public String githubFullyQualifiedName() {
        return githubFullyQualifiedName;
    }

    @Override
    public String getIconSrc() {
        return "assets/github-logo.png";
    }
}

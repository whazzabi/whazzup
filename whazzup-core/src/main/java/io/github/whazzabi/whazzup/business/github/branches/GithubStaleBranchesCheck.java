package io.github.whazzabi.whazzup.business.github.branches;

import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.customization.Group;
import io.github.whazzabi.whazzup.business.customization.Team;
import io.github.whazzabi.whazzup.business.github.GithubConfig;
import io.github.whazzabi.whazzup.business.github.actions.BranchesOfRepositorySupplier;
import io.github.whazzabi.whazzup.business.github.actions.MainBranchSupplier;

import java.util.List;

public class GithubStaleBranchesCheck extends Check {

    private final GithubConfig githubConfig;

    private String repoNameRegex = "";

    private BranchesOfRepositorySupplier ignoredBranchesOfRepository = new MainBranchSupplier();

    private Integer maxAgeOfBranchInDays = 7;

    /**
     * eg. orgs/whazzup or user/waschnick
     */
    private final String githubFullyQualifiedName;

    public GithubStaleBranchesCheck(String name, Group group, List<Team> teams, GithubConfig githubConfig, String githubFullyQualifiedName, String repoNameRegex) {
        super(name, group, teams);
        this.githubConfig = githubConfig;
        this.repoNameRegex = repoNameRegex;
        this.githubFullyQualifiedName = githubFullyQualifiedName;
    }

    public GithubStaleBranchesCheck withIgnoredBranchesOfRepository(BranchesOfRepositorySupplier branchesOfRepository) {
        this.ignoredBranchesOfRepository = branchesOfRepository;
        return this;
    }

    public GithubStaleBranchesCheck withMaxAgeOfBranchInDays(Integer maxAgeOfBranchInDays) {
        this.maxAgeOfBranchInDays = maxAgeOfBranchInDays;
        return this;
    }

    public BranchesOfRepositorySupplier ignoredBranchesOfRepository() {
        return ignoredBranchesOfRepository;
    }

    public Integer maxAgeOfBranchInDays() {
        return maxAgeOfBranchInDays;
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

    @Override
    public String getIconSrc() {
        return "assets/github-logo.png";
    }
}

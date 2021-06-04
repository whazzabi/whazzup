package io.github.whazzabi.whazzup.business.github.actions;

import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.check.CheckExecutor;
import io.github.whazzabi.whazzup.business.check.checkresult.CheckResult;
import io.github.whazzabi.whazzup.business.github.GithubConfig;
import io.github.whazzabi.whazzup.business.github.common.GithubClient;
import io.github.whazzabi.whazzup.business.github.common.api.GithubCommitAuthor;
import io.github.whazzabi.whazzup.business.github.common.api.GithubRepo;
import io.github.whazzabi.whazzup.business.github.common.api.GithubWorkflow;
import io.github.whazzabi.whazzup.business.github.common.api.GithubWorkflowRun;
import io.github.whazzabi.whazzup.presentation.State;
import io.github.whazzabi.whazzup.util.ConncurrentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GithubActionsCheckExecutor implements CheckExecutor<GithubActionsCheck> {

    private static final Logger LOG = LoggerFactory.getLogger(GithubActionsCheckExecutor.class);

    private final GithubClient githubClient;

    public GithubActionsCheckExecutor(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    @Override
    public List<CheckResult> executeCheck(GithubActionsCheck check) {
        LOG.info("Executing Github-Check: {} for Github-Account {}", check.getName(), check.githubFullyQualifiedName());

        GithubConfig githubConfig = check.githubConfig();
        List<CheckResult> checkResults = new ConncurrentList<>();

        List<GithubRepo> repositories = githubClient.getRepositories(githubConfig, check.githubFullyQualifiedName(), check.githubRepositoryMatcher());

        repositories.parallelStream().forEach(repo -> {
            checkResults.addAll(checkRepository(repo, check, githubConfig));
        });

        LOG.trace("Finished {}. Results: {}", check.getName(), checkResults.size());
        checkResults.sort(Comparator.comparing(CheckResult::getName));
        return checkResults;
    }

    private List<CheckResult> checkRepository(GithubRepo repo, GithubActionsCheck check, GithubConfig githubConfig) {
        List<String> branches = check.branchesOfRepository().get(repo.name);

        List<GithubWorkflow> workflows = githubClient.getActiveWorkflowsOfRepo(githubConfig, repo);

        return branches.parallelStream()
                .flatMap(branch -> githubClient.getLastWorkflowRunsOfBranch(githubConfig, repo, workflows, branch).stream())
                .parallel()
                .map(run -> toCheckResult(run, repo, check))
                .collect(Collectors.toList());
    }

    private CheckResult toCheckResult(GithubWorkflowRun run, GithubRepo repo, GithubActionsCheck check) {
        String name = repo.name + "/" + run.head_branch;

        GithubCommitAuthor author = run.head_commit.author != null ? run.head_commit.author : run.head_commit.committer;
        String description = run.head_commit.message + " [" + author.name + "]";
        State state = stateOfRun(run);
        return new CheckResult(state, name, description, 1, 0, check.getGroup())
                .withLink(run.html_url);
    }

    private State stateOfRun(GithubWorkflowRun run) {
        // https://docs.github.com/en/free-pro-team@latest/rest/reference/checks#create-a-check-run
        // success, failure, neutral, cancelled, skipped, timed_out, or action_required.
        switch (run.conclusion) {
            case "success":
                return State.GREEN;
            case "failure":
            case "timed_out":
                return State.RED;
            default:
                return State.GREY;
        }
    }

    @Override
    public boolean isApplicable(Check check) {
        return check instanceof GithubActionsCheck;
    }
}

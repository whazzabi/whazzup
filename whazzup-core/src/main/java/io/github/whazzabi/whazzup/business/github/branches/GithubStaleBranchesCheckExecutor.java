package io.github.whazzabi.whazzup.business.github.branches;

import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.check.CheckExecutor;
import io.github.whazzabi.whazzup.business.check.checkresult.CheckResult;
import io.github.whazzabi.whazzup.business.github.GithubConfig;
import io.github.whazzabi.whazzup.business.github.common.GithubClient;
import io.github.whazzabi.whazzup.business.github.common.api.GithubBranchDetails;
import io.github.whazzabi.whazzup.business.github.common.api.GithubRepo;
import io.github.whazzabi.whazzup.presentation.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class GithubStaleBranchesCheckExecutor implements CheckExecutor<GithubStaleBranchesCheck> {

    private static final Logger LOG = LoggerFactory.getLogger(GithubStaleBranchesCheckExecutor.class);

    private final GithubClient githubClient;

    public GithubStaleBranchesCheckExecutor(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    @Override
    public List<CheckResult> executeCheck(GithubStaleBranchesCheck check) {
        LOG.info("Executing Github-Stale-Branches-Check: {} for Github-Account {}", check.getName(), check.githubFullyQualifiedName());

        GithubConfig githubConfig = check.githubConfig();
        List<CheckResult> checkResults = new ArrayList<>();

        List<GithubRepo> repositories = githubClient.getRepositories(check.githubFullyQualifiedName(), check.repoNameRegex(), githubConfig);
        for (GithubRepo repo : repositories) {
            checkResults.addAll(checkRepository(check, githubConfig, repo));
        }
        LOG.trace("Finished {}. Results: {}", check.getName(), checkResults.size());
        return checkResults;
    }

    private List<CheckResult> checkRepository(GithubStaleBranchesCheck check, GithubConfig githubConfig, GithubRepo repo) {

        return githubClient.getDetailedBranches(githubConfig, repo).stream()
                .map(branch -> {
                    String assigneeName = branch.commit.author.login;
                    State state = stateOfPullRequest(check, branch);
                    final CheckResult checkResult = new CheckResult(state, "[" + repo.name + "] " + branch.name, " [" + assigneeName + "]", 1, 1, check.getGroup());
                    checkResult.withLink(branch._links.html);
                    checkResult.withTeams(check.getTeams());
                    return checkResult;
                })
                .collect(toList());
    }

    private State stateOfPullRequest(GithubStaleBranchesCheck check, GithubBranchDetails branch) {
        return branch.getAgeInDays() > check.maxAgeOfBranchInDays() ? State.YELLOW : State.GREEN;
    }

    @Override
    public boolean isApplicable(Check check) {
        return check instanceof GithubStaleBranchesCheck;
    }
}

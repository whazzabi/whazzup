package io.github.whazzabi.whazzup.business.github.pullrequest;

import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.check.CheckExecutor;
import io.github.whazzabi.whazzup.business.check.checkresult.CheckResult;
import io.github.whazzabi.whazzup.business.github.GithubConfig;
import io.github.whazzabi.whazzup.business.github.common.GithubClient;
import io.github.whazzabi.whazzup.business.github.common.api.GithubPullRequest;
import io.github.whazzabi.whazzup.business.github.common.api.GithubRepo;
import io.github.whazzabi.whazzup.presentation.State;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class GithubPullRequestsCheckExecutor implements CheckExecutor<GithubPullRequestsCheck> {

    private static final Logger LOG = LoggerFactory.getLogger(GithubPullRequestsCheckExecutor.class);

    private final GithubClient githubClient;

    public GithubPullRequestsCheckExecutor(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    @Override
    public List<CheckResult> executeCheck(GithubPullRequestsCheck check) {
        LOG.info("Executing Github-Check: {} for Github-Account {}", check.getName(), check.githubFullyQualifiedName());

        GithubConfig githubConfig = check.githubConfig();
        List<CheckResult> checkResults = new ArrayList<>();

        List<GithubRepo> repositories = githubClient.getRepositories(check.githubFullyQualifiedName(), check.repoNameRegex(), githubConfig);
        for (GithubRepo repo : repositories) {
            checkResults.addAll(checkRepository(check, githubConfig, repo));
        }
        LOG.trace("Finished {}. Results: {}", check.getName(), checkResults.size());
        return checkResults;
    }

    private List<CheckResult> checkRepository(GithubPullRequestsCheck check, GithubConfig githubConfig, GithubRepo repo) {
        boolean hasFilterKeyword = StringUtils.isBlank(check.getFilterKeyword());

        return githubClient.getPullRequests(githubConfig, repo).stream()
                .filter(pr -> {
                    if (hasFilterKeyword) {
                        return true;
                    }
                    return StringUtils.containsIgnoreCase(pr.title, check.getFilterKeyword());
                })
                .map(pullRequest -> {
                    String assigneeName = pullRequest.assignee == null ? "?" : pullRequest.assignee.login;
                    State state = stateOfPullRequest(pullRequest);
                    final CheckResult checkResult = new CheckResult(state, "[" + repo.name + "] " + pullRequest.title, " [" + assigneeName + "]", 1, 1, check.getGroup());
                    checkResult.withLink(pullRequest.html_url);
                    checkResult.withTeams(check.getTeams());
                    return checkResult;
                })
                .collect(toList());
    }

    private State stateOfPullRequest(GithubPullRequest pullRequest) {
        return pullRequest == null ? State.RED : State.YELLOW;
    }

    @Override
    public boolean isApplicable(Check check) {
        return check instanceof GithubPullRequestsCheck;
    }
}

package io.github.whazzabi.whazzup.business.github.pullrequest;

import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.check.CheckExecutor;
import io.github.whazzabi.whazzup.business.check.checkresult.CheckResult;
import io.github.whazzabi.whazzup.business.github.GithubConfig;
import io.github.whazzabi.whazzup.business.github.common.GithubClient;
import io.github.whazzabi.whazzup.business.github.common.api.GithubPullRequest;
import io.github.whazzabi.whazzup.business.github.common.api.GithubRepo;
import io.github.whazzabi.whazzup.business.github.common.api.GithubUser;
import io.github.whazzabi.whazzup.presentation.State;
import io.github.whazzabi.whazzup.util.ConncurrentList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class GithubPullRequestsCheckExecutor implements CheckExecutor<GithubPullRequestsCheck> {

    private static final Logger LOG = LoggerFactory.getLogger(GithubPullRequestsCheckExecutor.class);

    private final GithubClient githubClient;
    private final List<GithubPullrequestResultDecorator> resultDecorators;

    public GithubPullRequestsCheckExecutor(GithubClient githubClient, List<GithubPullrequestResultDecorator> resultDecorators) {
        this.githubClient = githubClient;
        this.resultDecorators = resultDecorators;
    }

    @Override
    public List<CheckResult> executeCheck(GithubPullRequestsCheck check) {
        LOG.info("Executing Github-Check: {} for Github-Account {}", check.getName(), check.githubFullyQualifiedName());

        GithubConfig githubConfig = check.githubConfig();
        List<CheckResult> checkResults = new ConncurrentList<>();

        List<GithubRepo> repositories = githubClient.getRepositories(githubConfig, check.githubFullyQualifiedName(), check.githubRepositoryMatcher());
        repositories.parallelStream().forEach(repo -> {
            checkResults.addAll(checkRepository(check, githubConfig, repo));
        });
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
                    List<String> assignees = getAssigneeOrReviewers(pullRequest).stream().map(user -> user.login).collect(toList());
                    String assigneeName = assignees.isEmpty() ? "?" : String.join(", ", assignees);
                    State state = stateOfPullRequest(pullRequest);
                    final CheckResult checkResult = new CheckResult(state, "[" + repo.name + "] " + pullRequest.title, " [" + assigneeName + "]", 1, 1, check.getGroup());
                    checkResult.withLink(pullRequest.html_url);
                    checkResult.withTeams(check.getTeams());

                    resultDecorators.forEach(resultDecorator -> resultDecorator.decorate(checkResult, pullRequest));

                    return checkResult;
                })
                .collect(toList());
    }

    private List<GithubUser> getAssigneeOrReviewers(GithubPullRequest pullRequest) {
        List<GithubUser> assignees = pullRequest.assignee != null ? Collections.singletonList(pullRequest.assignee) : pullRequest.assignees;
        if (assignees == null || assignees.isEmpty()) {
            assignees = pullRequest.requested_reviewers;
        }

        return assignees == null ? Collections.emptyList() : assignees;
    }

    private State stateOfPullRequest(GithubPullRequest pullRequest) {
        return pullRequest == null ? State.RED : State.YELLOW;
    }

    @Override
    public boolean isApplicable(Check check) {
        return check instanceof GithubPullRequestsCheck;
    }
}

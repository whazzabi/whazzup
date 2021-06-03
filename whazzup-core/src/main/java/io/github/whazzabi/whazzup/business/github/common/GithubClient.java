package io.github.whazzabi.whazzup.business.github.common;

import io.github.whazzabi.whazzup.business.github.GithubConfig;
import io.github.whazzabi.whazzup.business.github.common.api.*;
import io.github.whazzabi.whazzup.util.CloseableHttpClientRestClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@Component
public class GithubClient {

    private static final Logger LOG = LoggerFactory.getLogger(GithubClient.class);

    // This is also the max size from github!
    public static final int DEFAULT_PAGE_SIZE = 100;

    private final CloseableHttpClient closeableHttpClient;

    public GithubClient(CloseableHttpClient closeableHttpClient) {
        this.closeableHttpClient = closeableHttpClient;
    }

    public List<GithubRepo> getRepositories(String fullyQualifiedName, String repoNameRegex, GithubConfig config) {
        List<GithubRepo> allRepos = getRepositories(fullyQualifiedName, config);
        return filterRepos(allRepos, repoNameRegex);
    }

    public List<GithubRepo> getRepositories(String fullyQualifiedName, GithubConfig config) {

        final CloseableHttpClientRestClient restClient = client(config)
                .withQueryParameter("per_page", "" + DEFAULT_PAGE_SIZE);


        List<GithubRepo> result = new ArrayList<>();

        boolean hasNextPage = true;
        int currentPage = 1;
        while (hasNextPage) {
            restClient.setQueryParameter("page", "" + currentPage);

            GithubRepo[] tempResult = restClient.get("https://api.github.com/" + fullyQualifiedName + "/repos", GithubRepo[].class);
            result.addAll(Arrays.asList(tempResult));

            hasNextPage = tempResult.length == DEFAULT_PAGE_SIZE;
            currentPage++;
        }

        return result;
    }

    public List<GithubPullRequest> getPullRequests(GithubConfig githubConfig, GithubRepo repo) {
        final CloseableHttpClientRestClient restClient = client(githubConfig);

        return Arrays.asList(restClient.get(repo.url + "/pulls", GithubPullRequest[].class));
    }

    public List<GithubBranch> getBranches(GithubConfig githubConfig, GithubRepo repo) {
        final CloseableHttpClientRestClient restClient = client(githubConfig);

        return Arrays.asList(restClient.get(repo.url + "/branches", GithubBranch[].class));
    }

    public List<GithubBranchDetails> getDetailedBranches(GithubConfig githubConfig, GithubRepo repo) {
        final CloseableHttpClientRestClient restClient = client(githubConfig);
        return getBranches(githubConfig, repo).stream().map(branch -> restClient.get(branch.getDetailsUrl(), GithubBranchDetails.class)).collect(toList());
    }

    public List<GithubWorkflow> getActiveWorkflowsOfRepo(GithubConfig githubConfig, GithubRepo repo) {
        final CloseableHttpClientRestClient restClient = client(githubConfig);

        GithubWorkflowsResponse response = restClient.get(repo.url + "/actions/workflows", GithubWorkflowsResponse.class);
        return response.workflows.stream()
                .filter(wf -> "active".equals(wf.state))
                .collect(toList());
    }

    public List<GithubWorkflowRun> getLastWorkflowRunsOfBranch(GithubConfig githubConfig, GithubRepo repo, List<GithubWorkflow> workflows, String branch) {
        final CloseableHttpClientRestClient restClient = client(githubConfig);
        restClient.setQueryParameter("branch", branch);

        GithubWorkflowRunsResponse response = restClient.get(repo.url + "/actions/runs", GithubWorkflowRunsResponse.class);

        return workflows.stream()
                .map(response::findLastRunByWorkflow)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    private CloseableHttpClientRestClient client(GithubConfig githubConfig) {
        return new CloseableHttpClientRestClient(closeableHttpClient)
                .withCredentials(githubConfig.githubUserName(), githubConfig.githubUserPassword());
    }

    private List<GithubRepo> filterRepos(List<GithubRepo> githubRepos, String repoNameRegex) {
        Pattern pattern = Pattern.compile(repoNameRegex);

        List<GithubRepo> result = githubRepos.stream()
                .filter(repo -> pattern.matcher(repo.name).matches())
                .collect(toList());

        LOG.info("Found " + result.size() + " (from " + githubRepos.size() + " repos overall) matching Repos for RegEx '" + repoNameRegex + "'");
        return result;
    }
}

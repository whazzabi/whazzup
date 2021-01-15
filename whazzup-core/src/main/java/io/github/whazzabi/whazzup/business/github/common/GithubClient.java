package io.github.whazzabi.whazzup.business.github.common;

import io.github.whazzabi.whazzup.business.github.GithubConfig;
import io.github.whazzabi.whazzup.business.github.common.api.GithubPullRequest;
import io.github.whazzabi.whazzup.business.github.common.api.GithubRepo;
import io.github.whazzabi.whazzup.business.github.common.api.GithubWorkflowRun;
import io.github.whazzabi.whazzup.business.github.common.api.GithubWorkflowRunsResponse;
import io.github.whazzabi.whazzup.util.CloseableHttpClientRestClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class GithubClient {

    private static final Logger LOG = LoggerFactory.getLogger(GithubClient.class);

    // This is also the max size from github!
    public static final int DEFAULT_PAGE_SIZE = 100;

    @Autowired
    private CloseableHttpClient closeableHttpClient;

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

    public Optional<GithubWorkflowRun> getLastWorkflowRunOfBranch(GithubConfig githubConfig, GithubRepo repo, String branch) {
        final CloseableHttpClientRestClient restClient = client(githubConfig);
        restClient.setQueryParameter("branch", branch);
        restClient.setQueryParameter("per_page", "1");

        GithubWorkflowRunsResponse response = restClient.get(repo.url + "/actions/runs", GithubWorkflowRunsResponse.class);
        if (response.workflow_runs.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(response.workflow_runs.get(0));
    }

    private CloseableHttpClientRestClient client(GithubConfig githubConfig) {
        return new CloseableHttpClientRestClient(closeableHttpClient)
                .withCredentials(githubConfig.githubUserName(), githubConfig.githubUserPassword());
    }

    private List<GithubRepo> filterRepos(List<GithubRepo> githubRepos, String repoNameRegex) {
        Pattern pattern = Pattern.compile(repoNameRegex);

        List<GithubRepo> result = githubRepos.stream()
                .filter(repo -> pattern.matcher(repo.name).matches())
                .collect(Collectors.toList());

        LOG.info("Found " + result.size() + " (from " + githubRepos.size() + " repos overall) matching Repos for RegEx '" + repoNameRegex + "'");
        return result;
    }
}

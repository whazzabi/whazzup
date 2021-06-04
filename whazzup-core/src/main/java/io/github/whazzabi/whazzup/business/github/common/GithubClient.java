package io.github.whazzabi.whazzup.business.github.common;

import io.github.whazzabi.whazzup.business.github.GithubConfig;
import io.github.whazzabi.whazzup.business.github.common.api.*;
import io.github.whazzabi.whazzup.util.CacheBuilder;
import io.github.whazzabi.whazzup.util.CloseableHttpClientRestClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Component
public class GithubClient {

    private static final Logger LOG = LoggerFactory.getLogger(GithubClient.class);

    // This is also the max size from github!
    public static final int DEFAULT_PAGE_SIZE = 100;

    private final CloseableHttpClient closeableHttpClient;

    private final Map<String, List<GithubWorkflow>> WORKFLOWS_CACHE = CacheBuilder.cache(15, ChronoUnit.MINUTES);
    private final Map<String, List<GithubRepo>> REPO_CACHE = CacheBuilder.cache(15, ChronoUnit.MINUTES);

    public GithubClient(CloseableHttpClient closeableHttpClient) {
        this.closeableHttpClient = closeableHttpClient;
    }

    public synchronized List<GithubRepo> getRepositories(GithubConfig config, String fullyQualifiedName, GithubRepositoryMatcher githubRepositoryMatcher) {
        List<GithubRepo> allRepos = getRepositories(fullyQualifiedName, config);
        return githubRepositoryMatcher.filterRepos(allRepos);
    }

    /**
     * @deprecated Use io.github.whazzabi.whazzup.business.github.common.GithubClient#getRepositories(io.github.whazzabi.whazzup.business.github.GithubConfig, java.lang.String, io.github.whazzabi.whazzup.business.github.common.GithubRepositoryMatcher)
     */
    @Deprecated
    public synchronized List<GithubRepo> getRepositories(String fullyQualifiedName, String repoNameRegex, GithubConfig config) {
        return getRepositories(config, fullyQualifiedName, new GithubRepositoryMatcher().withRepoNameRegex(repoNameRegex));
    }

    public synchronized List<GithubRepo> getRepositories(String fullyQualifiedName, GithubConfig config) {
        List<GithubRepo> cacheResult = REPO_CACHE.get(fullyQualifiedName);
        if (cacheResult != null) {
            return cacheResult;
        }

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

        REPO_CACHE.put(fullyQualifiedName, result);
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
        List<GithubWorkflow> cacheResult = WORKFLOWS_CACHE.get(repo.url);
        if (cacheResult != null) {
            return cacheResult;
        }

        final CloseableHttpClientRestClient restClient = client(githubConfig);

        GithubWorkflowsResponse response = restClient.get(repo.url + "/actions/workflows", GithubWorkflowsResponse.class);
        List<GithubWorkflow> result = response.workflows.stream()
                .filter(wf -> "active".equals(wf.state))
                .collect(toList());
        WORKFLOWS_CACHE.put(repo.url, result);
        return result;
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

    private int counter = 0;
    private long start_date = System.currentTimeMillis();

    private CloseableHttpClientRestClient client(GithubConfig githubConfig) {
        return new CloseableHttpClientRestClient(closeableHttpClient) {


            @Override
            public String get(String url) {
                doLog(url);
                return super.get(url);
            }

            @Override
            public <T> T get(String url, Class<T> clazz) {
                doLog(url);
                return super.get(url, clazz);
            }

            private void doLog(String url) {
                long rateInCallsPerMinute = (long) (counter / ((System.currentTimeMillis() - start_date) / 1000d / 60d));
                LOG.debug("Github (" + counter++ + " @ " + rateInCallsPerMinute + " rpm): " + url);

            }
        }
                .withCredentials(githubConfig.githubUserName(), githubConfig.githubUserPassword());
    }
}

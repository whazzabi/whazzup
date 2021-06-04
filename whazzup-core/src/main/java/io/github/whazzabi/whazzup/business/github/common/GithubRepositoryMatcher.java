package io.github.whazzabi.whazzup.business.github.common;

import io.github.whazzabi.whazzup.business.github.common.api.GithubRepo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class GithubRepositoryMatcher {

    private static final Logger LOG = LoggerFactory.getLogger(GithubRepositoryMatcher.class);

    public static GithubRepositoryMatcher excludedArchivedRepos() {
        return new GithubRepositoryMatcher().withIncludeArchivedRepos(false);
    }

    /**
     * if specified the pr results will be filtered down to prs containing this keyword
     */
    private String repoNameRegex = "";
    private Boolean includeArchivedRepos = true;
    private List<String> ignoredRepos = Collections.emptyList();

    public List<String> ignoredRepos() {
        return ignoredRepos;
    }

    public String repoNameRegex() {
        return repoNameRegex;
    }

    public GithubRepositoryMatcher withRepoNameRegex(String repoNameRegex) {
        this.repoNameRegex = repoNameRegex;
        return this;
    }


    public GithubRepositoryMatcher withIgnoredRepos(List<String> ignoredRepos) {
        this.ignoredRepos = ignoredRepos;
        return this;
    }

    public GithubRepositoryMatcher withIncludeArchivedRepos(Boolean includeArchivedRepos) {
        this.includeArchivedRepos = includeArchivedRepos;
        return this;
    }

    public List<GithubRepo> filterRepos(List<GithubRepo> githubRepos) {
        Pattern pattern = Pattern.compile(repoNameRegex);

        boolean hasRepoRegex = StringUtils.isNotBlank(repoNameRegex);
        boolean hasIgnoredReposList = ignoredRepos != null && !ignoredRepos.isEmpty();
        boolean isFilterArchivedRepos = !includeArchivedRepos;

        List<GithubRepo> result = githubRepos.stream()
                .filter(repo -> hasRepoRegex ? pattern.matcher(repo.name).matches() : true)
                .filter(repo -> hasIgnoredReposList ? !ignoredRepos.contains(repo.name) : true)
                .filter(repo -> isFilterArchivedRepos ? !repo.archived : true)
                .collect(toList());

        LOG.info("Found " + result.size() + " (from " + githubRepos.size() + " repos overall) matching Repos for filter: " + this.toString());
        return result;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("GithubRepositoryMatcher{");
        sb.append("repoNameRegex='").append(repoNameRegex).append('\'');
        sb.append(", includeArchivedRepos=").append(includeArchivedRepos);
        sb.append(", ignoredRepos=").append(ignoredRepos);
        sb.append('}');
        return sb.toString();
    }
}

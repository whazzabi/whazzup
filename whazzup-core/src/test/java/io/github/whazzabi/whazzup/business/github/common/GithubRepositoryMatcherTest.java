package io.github.whazzabi.whazzup.business.github.common;

import io.github.whazzabi.whazzup.business.github.common.api.GithubRepo;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GithubRepositoryMatcherTest {


    private static final GithubRepo REPO_A = repo("repo-a", false);
    private static final GithubRepo REPO_B = repo("repo-a-archived", true);
    private static final GithubRepo REPO_C = repo("repo-b-archived", true);
    private static final GithubRepo REPO_D = repo("repo-c", false);
    private static final GithubRepo REPO_E = repo("repository-d", false);
    private static final GithubRepo REPO_F = repo("something-repo", false);

    private static final List<GithubRepo> REPOS = Collections.unmodifiableList(Arrays.asList(
            REPO_A, REPO_B, REPO_C, REPO_D, REPO_E, REPO_F
    ));

    @Test
    public void shouldNotFilterReposWithDefaults() {
        GithubRepositoryMatcher matcher = new GithubRepositoryMatcher();
        assertThat(matcher.filterRepos(REPOS)).containsAll(REPOS);
    }

    @Test
    public void shouldNotFilterReposWithGenericRegex() {
        GithubRepositoryMatcher matcher = new GithubRepositoryMatcher();
        matcher.withRepoNameRegex(".+");
        assertThat(matcher.filterRepos(REPOS)).containsAll(REPOS);
    }

    @Test
    public void shouldExcludeArchivedRepos() {
        GithubRepositoryMatcher matcher = GithubRepositoryMatcher.excludedArchivedRepos();

        assertThat(matcher.filterRepos(REPOS)).contains(REPO_A, REPO_D, REPO_E, REPO_F);
    }

    @Test
    public void shouldFilterReposCorrectly() {
        GithubRepositoryMatcher matcher = new GithubRepositoryMatcher();
        matcher.withRepoNameRegex("repo.*");

        assertThat(matcher.filterRepos(REPOS)).contains(REPO_A, REPO_B, REPO_C, REPO_D, REPO_E);

        matcher.withIgnoredRepos(Arrays.asList(REPO_A.name));
        assertThat(matcher.filterRepos(REPOS)).contains(REPO_B, REPO_C, REPO_D, REPO_E);

        matcher.withIncludeArchivedRepos(false);
        assertThat(matcher.filterRepos(REPOS)).contains(REPO_D, REPO_E);

        matcher.withRepoNameRegex("");
        assertThat(matcher.filterRepos(REPOS)).contains(REPO_D, REPO_E, REPO_F);
    }

    private static GithubRepo repo(String name, boolean archived) {
        GithubRepo repo = new GithubRepo();
        repo.name = name;
        repo.archived = archived;
        return repo;
    }
}
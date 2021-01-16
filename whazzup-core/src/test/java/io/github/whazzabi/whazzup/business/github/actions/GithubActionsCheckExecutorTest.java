package io.github.whazzabi.whazzup.business.github.actions;

import io.github.whazzabi.whazzup.TestTeam;
import io.github.whazzabi.whazzup.business.check.checkresult.CheckResult;
import io.github.whazzabi.whazzup.business.customization.Group;
import io.github.whazzabi.whazzup.business.github.GithubConfig;
import io.github.whazzabi.whazzup.business.github.common.GithubClient;
import io.github.whazzabi.whazzup.business.github.common.api.GithubRepo;
import io.github.whazzabi.whazzup.business.github.common.api.GithubWorkflowRun;
import io.github.whazzabi.whazzup.presentation.State;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GithubActionsCheckExecutorTest {

    public static final String ORG = "orgs/whazzabi";
    public static final GithubConfig GITHUB_CONFIG = new GithubConfig("username", "password");

    private GithubClient client = mock(GithubClient.class);
    private GithubActionsCheckExecutor executor = new GithubActionsCheckExecutor(client);

    @Test
    public void convertsConclusionToCorrectState() {
        repositoriesExist(
                repository("repo1", successfullRunForBranch("main")),
                repository("repo2", failedRunForBranch("main")),
                repository("repo3", cancelledRunForBranch("main"))
        );

        GithubActionsCheck check = check(ORG, ".*");

        List<CheckResult> checkResults = executor.executeCheck(check);

        assertThat(checkResults.size()).isEqualTo(3);
        assertThat(checkResults.get(0).getState()).isEqualTo(State.GREEN);
        assertThat(checkResults.get(0).getName()).isEqualTo("repo1/main");
        assertThat(checkResults.get(0).getInfo()).isEqualTo("run success");
        assertThat(checkResults.get(1).getState()).isEqualTo(State.RED);
        assertThat(checkResults.get(1).getName()).isEqualTo("repo2/main");
        assertThat(checkResults.get(1).getInfo()).isEqualTo("run failure");
        assertThat(checkResults.get(2).getState()).isEqualTo(State.GREY);
        assertThat(checkResults.get(2).getName()).isEqualTo("repo3/main");
        assertThat(checkResults.get(2).getInfo()).isEqualTo("run cancelled");
    }

    @Test
    public void excludesReposWithoutRunsOfBranch() {
        repositoriesExist(
                repository("repo1", successfullRunForBranch("main")),
                repository("repo2", noRunsForBranch("main"))
        );

        GithubActionsCheck check = check(ORG, ".*");

        List<CheckResult> checkResults = executor.executeCheck(check);

        assertThat(checkResults.size()).isEqualTo(1);
        assertThat(checkResults.get(0).getName()).isEqualTo("repo1/main");
    }

    @Test
    public void differentBranchesPerRepo() {
        repositoriesExist(
                repository("repo1", successfullRunForBranch("main"), successfullRunForBranch("feature1")),
                repository("repo2", successfullRunForBranch("master"), successfullRunForBranch("feature2"))
        );

        GithubActionsCheck check = check(ORG, ".*")
                .withBranchesOfRepository(repo -> singletonList(repo.equals("repo1") ? "main" : "master"));

        List<CheckResult> checkResults = executor.executeCheck(check);

        assertThat(checkResults.size()).isEqualTo(2);
        assertThat(checkResults.get(0).getName()).isEqualTo("repo1/main");
        assertThat(checkResults.get(1).getName()).isEqualTo("repo2/master");
    }

    @Test
    public void multipleBranchesPerRepo() {
        repositoriesExist(
                repository("repo1", successfullRunForBranch("master"), successfullRunForBranch("develop"), successfullRunForBranch("feature1")),
                repository("repo2", successfullRunForBranch("main"), successfullRunForBranch("feature2"))
        );

        GithubActionsCheck check = check(ORG, ".*")
                .withBranchesOfRepository(repo -> repo.equals("repo1") ? asList("master", "develop") : singletonList("main"));

        List<CheckResult> checkResults = executor.executeCheck(check);

        assertThat(checkResults.size()).isEqualTo(3);
        assertThat(checkResults.get(0).getName()).isEqualTo("repo1/master");
        assertThat(checkResults.get(1).getName()).isEqualTo("repo1/develop");
        assertThat(checkResults.get(2).getName()).isEqualTo("repo2/main");
    }
    
    private GithubRepo repository(String name, WorkflowRunOfBranch ...workflowRunOfBranches) {
        GithubRepo repo = new GithubRepo();
        repo.name = name;
        repo.url = "https://api.github.com/orgs/Egoditor/repos/";
        for (WorkflowRunOfBranch workflowRunOfBranch: workflowRunOfBranches) {
            when(client.getLastWorkflowRunOfBranch(eq(GITHUB_CONFIG), eq(repo), eq(workflowRunOfBranch.branch)))
                    .thenReturn(workflowRunOfBranch.run);
        }

        return repo;
    }

    private void repositoriesExist(GithubRepo ...repositories) {
        when(client.getRepositories(eq(ORG), anyString(), eq(GITHUB_CONFIG))).thenReturn(asList(repositories));
    }

    private GithubActionsCheck check(String fullyQualifiedName, String repoNameRegex) {
        Group group = new Group(false, 0, "", "");
        GithubConfig githubConfig = GITHUB_CONFIG;
        return new GithubActionsCheck("name", group, singletonList(new TestTeam()), githubConfig, fullyQualifiedName, repoNameRegex);
    }

    public WorkflowRunOfBranch successfullRunForBranch(String branch) {
        return new WorkflowRunOfBranch(branch, "success");
    }

    public WorkflowRunOfBranch failedRunForBranch(String branch) {
        return new WorkflowRunOfBranch(branch, "failure");
    }

    public WorkflowRunOfBranch cancelledRunForBranch(String branch) {
        return new WorkflowRunOfBranch(branch, "cancelled");
    }

    public WorkflowRunOfBranch noRunsForBranch(String branch) {
        return new WorkflowRunOfBranch(branch, Optional.empty());
    }

    private static class WorkflowRunOfBranch {
        public String branch;
        public Optional<GithubWorkflowRun> run;

        public WorkflowRunOfBranch(String branch, String conclusion) {
            this.branch = branch;
            this.run = Optional.of(workflowRun(branch, conclusion));
        }

        public WorkflowRunOfBranch(String branch, Optional<GithubWorkflowRun> run) {
            this.branch = branch;
            this.run = run;
        }

        private GithubWorkflowRun workflowRun(String branch, String conclusion) {
            GithubWorkflowRun run = new GithubWorkflowRun();
            run.head_branch = branch;
            run.conclusion = conclusion;
            return run;
        }
    }
}
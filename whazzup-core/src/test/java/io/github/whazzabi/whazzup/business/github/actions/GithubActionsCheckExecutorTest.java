package io.github.whazzabi.whazzup.business.github.actions;

import io.github.whazzabi.whazzup.TestTeam;
import io.github.whazzabi.whazzup.business.check.checkresult.CheckResult;
import io.github.whazzabi.whazzup.business.customization.Group;
import io.github.whazzabi.whazzup.business.github.GithubConfig;
import io.github.whazzabi.whazzup.business.github.common.GithubClient;
import io.github.whazzabi.whazzup.business.github.common.api.*;
import io.github.whazzabi.whazzup.presentation.State;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class GithubActionsCheckExecutorTest {

    public static final String ORG = "orgs/whazzabi";
    public static final GithubConfig GITHUB_CONFIG = new GithubConfig("username", "password");
    public static final String WORKFLOW_NAME = "workflowName";
    public static final String COMMIT_MESSAGE = "my-message";
    public static final String COMMIT_AUTHOR = "my-author";

    private GithubClient client = spy(new GithubClient(mock(CloseableHttpClient.class)));
    private GithubActionsCheckExecutor executor = new GithubActionsCheckExecutor(client);

//    @BeforeEach
//    public void beforeEach() {
//        doReturn(mock(CloseableHttpClient.class)).when(client).getCl
//    }

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
        assertThat(checkResults.get(0).getInfo()).isEqualTo("my-message [my-author]");
        assertThat(checkResults.get(1).getState()).isEqualTo(State.RED);
        assertThat(checkResults.get(1).getName()).isEqualTo("repo2/main");
        assertThat(checkResults.get(1).getInfo()).isEqualTo("my-message [my-author]");
        assertThat(checkResults.get(2).getState()).isEqualTo(State.GREY);
        assertThat(checkResults.get(2).getName()).isEqualTo("repo3/main");
        assertThat(checkResults.get(2).getInfo()).isEqualTo("my-message [my-author]");
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
        assertThat(checkResults.get(0).getName()).isEqualTo("repo1/develop");
        assertThat(checkResults.get(1).getName()).isEqualTo("repo1/master");
        assertThat(checkResults.get(2).getName()).isEqualTo("repo2/main");
    }

    @Test
    public void multipleWorkflowsPerRepo() {
        repositoriesExist(
                repository("repo1", new WorkflowRunsOfBranch("main", asList(
                        workflowRunOfWorkflow("main", "workflow1"),
                        workflowRunOfWorkflow("main", "workflow2")
                ))),
                repository("repo2", successfullRunForBranch("main"))
        );

        GithubActionsCheck check = check(ORG, ".*");

        List<CheckResult> checkResults = executor.executeCheck(check);

        assertThat(checkResults.size()).isEqualTo(3);
        assertThat(checkResults.get(0).getName()).isEqualTo("repo1/main");
        assertThat(checkResults.get(0).getInfo()).isEqualTo("my-message [my-author]");
        assertThat(checkResults.get(1).getName()).isEqualTo("repo1/main");
        assertThat(checkResults.get(1).getInfo()).isEqualTo("my-message [my-author]");
        assertThat(checkResults.get(2).getName()).isEqualTo("repo2/main");
        assertThat(checkResults.get(2).getInfo()).isEqualTo("my-message [my-author]");
    }

    private GithubRepo repository(String name, WorkflowRunsOfBranch... workflowRunsOfBranches) {
        GithubRepo repo = new GithubRepo();
        repo.name = name;
        repo.url = "https://api.github.com/orgs/whazzabi/repos/";

        List<GithubWorkflow> workflows = singletonList(workflow());

        doReturn(workflows).when(client).getActiveWorkflowsOfRepo(eq(GITHUB_CONFIG), eq(repo));

        for (WorkflowRunsOfBranch workflowRunOfBranch : workflowRunsOfBranches) {
            doReturn(workflowRunOfBranch.runs).when(client).getLastWorkflowRunsOfBranch(eq(GITHUB_CONFIG), eq(repo), eq(workflows), eq(workflowRunOfBranch.branch));
        }

        return repo;
    }

    private void repositoriesExist(GithubRepo... repositories) {
        doReturn(asList(repositories)).when(client).getRepositories(eq(ORG), eq(GITHUB_CONFIG));
    }

    private GithubActionsCheck check(String fullyQualifiedName, String repoNameRegex) {
        Group group = new Group(false, 0, "", "");
        GithubConfig githubConfig = GITHUB_CONFIG;
        return new GithubActionsCheck("name", group, singletonList(new TestTeam()), githubConfig, fullyQualifiedName, repoNameRegex);
    }

    private GithubWorkflow workflow() {
        GithubWorkflow workflow = new GithubWorkflow();
        workflow.id = 123L;
        workflow.state = "active";
        return workflow;
    }

    private WorkflowRunsOfBranch successfullRunForBranch(String branch) {
        return workflowRunOfBranch(branch, "success");
    }

    private WorkflowRunsOfBranch failedRunForBranch(String branch) {
        return workflowRunOfBranch(branch, "failure");
    }

    private WorkflowRunsOfBranch cancelledRunForBranch(String branch) {
        return workflowRunOfBranch(branch, "cancelled");
    }

    private WorkflowRunsOfBranch noRunsForBranch(String branch) {
        return new WorkflowRunsOfBranch(branch, emptyList());
    }

    private static WorkflowRunsOfBranch workflowRunOfBranch(String branch, String conclusion) {
        return new WorkflowRunsOfBranch(branch, singletonList(workflowRun(branch, conclusion)));
    }

    private static GithubWorkflowRun workflowRun(String branch, String conclusion) {
        GithubWorkflowRun run = workflowRun();
        run.name = WORKFLOW_NAME;
        run.head_branch = branch;
        run.conclusion = conclusion;
        return run;
    }

    private static GithubWorkflowRun workflowRunOfWorkflow(String branch, String workflowName) {
        GithubWorkflowRun run = workflowRun();
        run.name = workflowName;
        run.head_branch = branch;
        run.conclusion = "success";
        return run;
    }

    private static GithubWorkflowRun workflowRun() {
        GithubWorkflowRun run = new GithubWorkflowRun();
        run.name = WORKFLOW_NAME;
        run.head_commit = new GithubWorkflowRunHeadCommit();
        run.head_commit.message = COMMIT_MESSAGE;

        run.head_commit.author = new GithubCommitAuthor();
        run.head_commit.author.name = COMMIT_AUTHOR;
        return run;
    }

    private static class WorkflowRunsOfBranch {
        public String branch;
        public List<GithubWorkflowRun> runs;

        public WorkflowRunsOfBranch(String branch, List<GithubWorkflowRun> run) {
            this.branch = branch;
            this.runs = run;
        }
    }
}
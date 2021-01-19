package io.github.whazzabi.whazzup.business.github.common.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubWorkflowRunsResponse {

    public List<GithubWorkflowRun> workflow_runs;

    public Optional<GithubWorkflowRun> findLastRunByWorkflow(GithubWorkflow workflow) {
        // workflow runs are returned by Github latest first. Let's hope that never changes ;-)
        return workflow_runs.stream()
                .filter(run -> Objects.equals(workflow.id, run.workflow_id))
                .findFirst();
    }
}

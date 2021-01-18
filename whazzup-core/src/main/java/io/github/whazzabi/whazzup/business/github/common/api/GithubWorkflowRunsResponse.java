package io.github.whazzabi.whazzup.business.github.common.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubWorkflowRunsResponse {

    public List<GithubWorkflowRun> workflow_runs;
}

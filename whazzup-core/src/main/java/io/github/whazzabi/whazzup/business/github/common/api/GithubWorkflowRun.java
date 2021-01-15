package io.github.whazzabi.whazzup.business.github.common.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubWorkflowRun {

    public Long id;
    public String name;
    public Long workflow_id;
    public String status;
    public String conclusion;
    public String url;
    public String html_url;
    public String head_branch;
}

package io.github.whazzabi.whazzup.business.github.common.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubWorkflowRunHeadCommit {

    public String id;
    public String tree_id;
    public String message;
    public String timestamp;

    public GithubCommitAuthor author;
    public GithubCommitAuthor committer;
}

package io.github.whazzabi.whazzup.business.github.common.api;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubCommitDetail {
    public GithubCommitAuthor author;
    public GithubCommitAuthor committer;
    public String message;
    public GithubCommitReference tree;
    public String url;
    public Integer comment_count;
    //    "verification": {
    //      "verified": false,
    //      "reason": "unsigned",
    //      "signature": null,
    //      "payload": null
    //    }
}

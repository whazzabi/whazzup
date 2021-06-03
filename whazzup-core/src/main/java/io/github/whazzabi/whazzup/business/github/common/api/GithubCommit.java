package io.github.whazzabi.whazzup.business.github.common.api;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubCommit {
    public String sha;
    public String node_id;
    public GithubCommitDetail commit;
    public String url;
    public String html_url;
    public String comments_url;
    public GithubUser author;
    public GithubUser committer;
    public List<GithubCommitReference> parents;
}

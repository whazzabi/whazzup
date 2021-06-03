package io.github.whazzabi.whazzup.business.github.common.api;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubCommitReference {
    public String sha;
    public String url;
    public String html_url;

}

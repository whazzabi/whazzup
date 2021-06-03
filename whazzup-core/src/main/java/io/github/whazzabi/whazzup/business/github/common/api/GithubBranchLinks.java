package io.github.whazzabi.whazzup.business.github.common.api;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubBranchLinks {
    public String self;
    public String html;
}

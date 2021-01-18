package io.github.whazzabi.whazzup.business.github.common.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubWorkflow {

    public Long id;
    public String state;
}

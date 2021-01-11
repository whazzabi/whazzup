package io.github.whazzabi.whazzup.business.github.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubRepoPermissions {
    public Boolean admin;
    public Boolean push;
    public Boolean pull;
}

package io.github.whazzabi.whazzup.business.github.common.api;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubCommitAuthor {
    public String name;
    public String email;
    public String date;
}

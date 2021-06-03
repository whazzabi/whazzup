package io.github.whazzabi.whazzup.business.github.common.api;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubBranch {

    public String name;
    public GithubCommitReference commit;
    @JsonProperty("protected")
    public boolean isProtected;
    public String protection_url;

    public String getDetailsUrl() {
        return protection_url.replace("/protection", "");
    }
}

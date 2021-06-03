package io.github.whazzabi.whazzup.business.github.common.api;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubBranchDetails {

    public String name;
    public GithubCommit commit;
    public GithubBranchLinks _links;
    @JsonProperty("protected")
    public boolean isProtected;
    public String protection_url;
    //"protection": {
    //  "enabled": false,
    //  "required_status_checks": {
    //  "enforcement_level": "off",
    //  "contexts": []
    //  }
    //},

    public Integer getAgeInDays() {
        // 2021-05-26T13:25:49Z
        String dateString = commit.commit.author.date;
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateString);
        long hours = offsetDateTime.until(now(), ChronoUnit.HOURS);
        return (int)hours / 24;
    }

    protected OffsetDateTime now() {
        return OffsetDateTime.now();
    }
}

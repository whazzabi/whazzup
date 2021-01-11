package io.github.whazzabi.whazzup.business.pingdom;

import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.customization.Group;
import io.github.whazzabi.whazzup.business.customization.Team;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class PingdomCheck extends Check {

    private final PingdomConfig pingdomConfig;

    private String regexForMatchingNames = "";

    private Pattern pattern;

    public PingdomCheck(String name, Group group, Team team, PingdomConfig pingdomConfig, String regexForMatchingNames) {
        this(name, group, Collections.singletonList(team), pingdomConfig, regexForMatchingNames);
    }

    public PingdomCheck(String name, Group group, List<Team> teams, PingdomConfig pingdomConfig, String regexForMatchingNames) {
        super(name, group, teams);
        this.pingdomConfig = pingdomConfig;
        this.regexForMatchingNames = regexForMatchingNames;
    }

    public PingdomConfig pingdomConfig() {
        return pingdomConfig;
    }

    public String regexForMatchingRepoNames() {
        return regexForMatchingNames;
    }

    public Pattern pattern() {
        if (pattern == null) {
            pattern = Pattern.compile(regexForMatchingNames);
        }
        return pattern;
    }


    @Override
    public String getIconSrc() {
        return "assets/pingdom-logo.png";
    }

}

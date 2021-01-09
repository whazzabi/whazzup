package io.github.whazzabi.whazzup.example.business;

import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.customization.Group;
import io.github.whazzabi.whazzup.business.customization.Team;

import java.util.List;

public class StateToggleCheck extends Check {

    public StateToggleCheck(String name, Group group, List<Team> teams) {
        super(name, group, teams);
    }
}

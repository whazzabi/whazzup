package io.github.whazzabi.whazzup;

import io.github.whazzabi.whazzup.business.customization.Team;

public class TestTeam implements Team {

    public final static TestTeam INSTANCE = new TestTeam();

    @Override
    public String getTeamName() {
        return "TestTeam";
    }

    @Override
    public String getJiraTeamName() {
        return "test";
    }
}

package io.github.whazzabi.whazzup.example.config;

import io.github.whazzabi.whazzup.business.customization.Team;
import io.github.whazzabi.whazzup.business.customization.TeamProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ExampleTeamProvider implements TeamProvider {

    @Override
    public List<Team> getTeams() {
        return Arrays.asList(ExampleTeam.values());
    }
}

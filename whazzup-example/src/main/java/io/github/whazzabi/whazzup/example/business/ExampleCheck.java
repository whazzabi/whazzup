package io.github.whazzabi.whazzup.example.business;

import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.customization.Group;
import io.github.whazzabi.whazzup.business.customization.Team;
import io.github.whazzabi.whazzup.presentation.State;

import java.util.List;

public class ExampleCheck extends Check {

    private static final String ICON_SRC = "https://upload.wikimedia.org/wikipedia/commons/5/51/Army-officer-icon.png";

    private String link;

    private State state = State.YELLOW;

    protected ExampleCheck(String name, Group group, List<Team> teams) {
        super(name, group, teams);
    }

    public ExampleCheck withLink(String link) {
        this.link = link;
        return this;
    }

    public String link() {
        return link;
    }

    public ExampleCheck withState(State state) {
        this.state = state;
        return this;
    }

    @Override
    public String getIconSrc() {
        return ICON_SRC;
    }

    public State state() {
        return state;
    }
}

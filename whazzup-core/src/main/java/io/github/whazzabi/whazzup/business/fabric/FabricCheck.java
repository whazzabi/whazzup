package io.github.whazzabi.whazzup.business.fabric;

import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.customization.Group;
import io.github.whazzabi.whazzup.business.customization.Team;

import java.util.List;

/**
 * Check for fabric/crashlytics
 */
public class FabricCheck extends Check {

    private static final String ICON_SRC = "assets/fabric-logo.png";
    private final String email;

    private final String password;

    public FabricCheck(String name, Group group, List<Team> teams, String email, String password) {
        super(name, group, teams);
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    @Override
    public String getIconSrc() {
        return ICON_SRC;
    }

    public String getPassword() {
        return this.password;
    }
}

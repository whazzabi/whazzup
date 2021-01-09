package io.github.whazzabi.whazzup.presentation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Arrays;
import java.util.List;

public class UiTeams {

    public static final UiTeams ALL = new UiTeams(Arrays.asList("ALL"));

    private final List<String> teams;

    @java.beans.ConstructorProperties({"teams"})
    public UiTeams(List<String> teams) {
        this.teams = teams;
    }

    public List<String> getTeams() {
        return this.teams;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

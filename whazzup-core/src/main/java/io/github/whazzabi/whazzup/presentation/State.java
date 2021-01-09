package io.github.whazzabi.whazzup.presentation;

public enum State{

    GREY,
    GREEN,
    YELLOW,
    RED;


    @Override
    public String toString() {
        return name().toLowerCase();
    }

}

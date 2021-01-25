package io.github.whazzabi.whazzup.presentation;

import java.util.List;

public class Navbar {

    private List<NavbarItem> left;

    private List<NavbarItem> right;

    public Navbar(List<NavbarItem> left, List<NavbarItem> right) {
        this.left = left;
        this.right = right;
    }

    public List<NavbarItem> getLeft() {
        return left;
    }

    public List<NavbarItem> getRight() {
        return right;
    }
}

package io.github.whazzabi.whazzup.presentation;

import java.util.List;

public class NavbarItem {

    private String label;
    private String link;
    private List<NavbarItem> subMenu;
    private boolean divider;

    public NavbarItem(String label, String link, List<NavbarItem> subMenu, boolean divider) {
        this.label = label;
        this.link = link;
        this.subMenu = subMenu;
        this.divider = divider;
    }

    public String getLabel() {
        return label;
    }

    public String getLink() {
        return link;
    }

    public List<NavbarItem> getSubMenu() {
        return subMenu;
    }

    public boolean isDivider() {
        return divider;
    }
}

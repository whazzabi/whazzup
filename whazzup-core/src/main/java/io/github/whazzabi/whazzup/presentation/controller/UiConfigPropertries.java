package io.github.whazzabi.whazzup.presentation.controller;

import io.github.whazzabi.whazzup.presentation.Navbar;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "whazzup")
public class UiConfigPropertries {

    private final String pagetitle;
    private final Navbar navbar;

    public UiConfigPropertries(String pagetitle, Navbar navbar) {
        this.pagetitle = pagetitle;
        this.navbar = navbar;
    }

    public String getPagetitle() {
        return pagetitle;
    }

    public Navbar getNavbar() {
        return navbar;
    }
}

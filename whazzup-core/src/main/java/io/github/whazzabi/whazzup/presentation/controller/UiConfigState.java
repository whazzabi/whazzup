package io.github.whazzabi.whazzup.presentation.controller;

import io.github.whazzabi.whazzup.presentation.UiConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UiConfigState {


    @Value("${whazzup.pagetitle:angel dust}")
    private String title;

    private UiConfig uiConfig;

    public UiConfig get() {
        if(uiConfig == null) {
            uiConfig = createUiConfig();
        }
        return uiConfig;
    }

    private UiConfig createUiConfig() {
        return new UiConfig(title);
    }
}

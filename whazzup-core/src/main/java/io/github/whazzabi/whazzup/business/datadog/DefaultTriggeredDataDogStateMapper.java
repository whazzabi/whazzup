package io.github.whazzabi.whazzup.business.datadog;

import io.github.whazzabi.whazzup.presentation.State;


public class DefaultTriggeredDataDogStateMapper implements TriggeredDataDogStateMapper {

    @Override
    public State map(DataDogMonitor monitor) {
        return State.RED;
    }
}

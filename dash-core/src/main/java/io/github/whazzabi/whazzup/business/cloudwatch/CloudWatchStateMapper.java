package io.github.whazzabi.whazzup.business.cloudwatch;

import io.github.whazzabi.whazzup.presentation.State;

public interface CloudWatchStateMapper {

    State mapState(final String stateValue);
}

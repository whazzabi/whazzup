package io.github.whazzabi.whazzup.business.failure;

import io.github.whazzabi.whazzup.business.customization.Group;

public class FailureGroup extends Group {

    public static final FailureGroup INSTANCE = new FailureGroup();

    public FailureGroup() {
        super(false, -1, null, "ERRORS");
    }

    @Override
    public String toString() {
        return getGroupId();
    }
}

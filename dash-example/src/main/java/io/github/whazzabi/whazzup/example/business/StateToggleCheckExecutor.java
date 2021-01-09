package io.github.whazzabi.whazzup.example.business;

import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.check.CheckExecutor;
import io.github.whazzabi.whazzup.business.check.checkresult.CheckResult;
import io.github.whazzabi.whazzup.presentation.State;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class StateToggleCheckExecutor implements CheckExecutor<StateToggleCheck> {

    private State state = State.GREEN;

    @Override
    public List<CheckResult> executeCheck(StateToggleCheck check) {
        CheckResult checkResult = new CheckResult(togglingState(), check.getName(), "foo", 1, 1, check.getGroup())
                .withCheckResultIdentifier("toggle-state-check-id");

        return Collections.singletonList(checkResult);
    }

    private State togglingState() {
        if (state == State.GREEN) {
            state = State.RED;
        } else {
            state = State.GREEN;
        }

        return state;
    }

    @Override
    public boolean isApplicable(Check check) {
        return check instanceof StateToggleCheck;
    }
}

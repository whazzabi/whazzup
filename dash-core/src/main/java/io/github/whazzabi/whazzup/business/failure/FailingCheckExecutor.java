package io.github.whazzabi.whazzup.business.failure;

import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.check.CheckExecutor;
import io.github.whazzabi.whazzup.business.check.checkresult.CheckResult;
import io.github.whazzabi.whazzup.presentation.State;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class FailingCheckExecutor implements CheckExecutor<FailingCheck> {


    @Override
    public List<CheckResult> executeCheck(FailingCheck check) {
        return Collections.singletonList(new CheckResult(State.RED, check.getName(), check.getFailureMessage(), 1, 1, check.getGroup()));
    }

    @Override
    public boolean isApplicable(Check check) {
        return check instanceof FailingCheck;
    }
}

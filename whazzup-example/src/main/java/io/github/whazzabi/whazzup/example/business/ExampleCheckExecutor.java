package io.github.whazzabi.whazzup.example.business;

import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.check.CheckExecutor;
import io.github.whazzabi.whazzup.business.check.checkresult.CheckResult;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class ExampleCheckExecutor implements CheckExecutor<ExampleCheck> {

    @Override
    public List<CheckResult> executeCheck(ExampleCheck check) {

        final CheckResult result = new CheckResult(check.state(), check.getName(), "info", 0, 1, check.getGroup());
        result.withDescription("some result description");
        result.withTeams(check.getTeams());
        result.withCheckResultIdentifier(check.getName());

        if (check.link() != null) {
            result.withLink(check.link());
        }

        if (new Random().nextInt() % 2 == 0) {
            result.markRunning();
        }

        // This Task take some time to show parallel check exection!
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return Collections.singletonList(result);
    }

    @Override
    public boolean isApplicable(Check check) {
        return check instanceof ExampleCheck;
    }
}

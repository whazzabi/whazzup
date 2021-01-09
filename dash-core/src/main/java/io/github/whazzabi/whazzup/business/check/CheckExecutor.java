package io.github.whazzabi.whazzup.business.check;

import io.github.whazzabi.whazzup.business.check.checkresult.CheckResult;

import java.util.List;

public interface CheckExecutor<T extends Check> {

    /**
     * @param check The check to execute
     * @return Never an empty collection. In case of "everything is fine" a "green" result check must be returned!
     */
    List<CheckResult> executeCheck(T check);

    boolean isApplicable(Check check);
}

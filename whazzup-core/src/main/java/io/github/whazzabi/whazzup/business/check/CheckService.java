package io.github.whazzabi.whazzup.business.check;

import io.github.whazzabi.whazzup.business.check.checkresult.CheckResult;
import io.github.whazzabi.whazzup.presentation.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;


@Service
public class CheckService {

    private static final Logger LOG = LoggerFactory.getLogger(CheckService.class);

    @Autowired
    private List<CheckExecutor> checkExecutors;

    @Value("${whazzup.iconsEnabled:true}")
    private boolean iconsEnabled;

    private Map<Check, List<CheckResult>> CACHE = new ConcurrentHashMap<>();

    private int numberOfParallelTask = 8;
    private long counterOfCheckRuns = 0;

    public List<CheckResult> check(List<Check> checks) {
        try {
            // HINT: We are creating a custom ForkJoinPool and are submitting our parallelStream to it
            // HINT2: The parallelStream normally uses ForkJoinPool.commonPool
            ForkJoinPool forkJoinPool = new ForkJoinPool(getNumberOfParallelTask());
            return forkJoinPool.submit(() ->
                    checks.parallelStream()
                            .map(this::executeCheck)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList())
            ).get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            counterOfCheckRuns++;
        }
    }

    @SuppressWarnings("unchecked")
    private List<CheckResult> executeCheck(Check check) {
        if (counterOfCheckRuns % check.runEachNthCheck() != 0) {
            LOG.debug("Returning CACHE and skipping check: " + check);
            if (CACHE.containsKey(check)) {
                return CACHE.get(check);
            } else {
                LOG.debug("CACHE empty for check: " + check);
                CACHE.forEach((key, value) -> LOG.warn(" # CACHE-ENTRY: " + key));
            }
        }

        CheckExecutor checkExecutor = executor(check);
        try {
            List<CheckResult> checkResults = checkExecutor.executeCheck(check);
            List<CheckResult> results = decorateCheckResults(check, checkResults);
            CACHE.put(check, results);
            return results;
        } catch (Exception e) {
            LOG.error("There are unhandled errors when performing check '{}' on stage '{}' for teams '{}'", check.getName(), check.getGroup(), check.getTeams());
            LOG.error(e.getMessage(), e);
            return Collections.singletonList(new CheckResult(State.RED, "unhandled check error: " + e.getMessage(), check.getName(), 0, 0, check.getGroup()).withTeams(check.getTeams()));
        }
    }

    private List<CheckResult> decorateCheckResults(Check check, List<CheckResult> results) {
        if (iconsEnabled) {
            results.forEach(checkResult -> {
                if (StringUtils.isEmpty(checkResult.getIconSrc())) {
                    checkResult.withIconSrc(check.getIconSrc());
                }
            });
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    CheckExecutor executor(Check check) {

        final ArrayList<CheckExecutor> applicableExecutors = new ArrayList<>(checkExecutors);
        applicableExecutors.removeIf(checkExecutor -> !checkExecutor.isApplicable(check));

        if (applicableExecutors.size() != 1) {
            LOG.error("{} executors found for check of type {}", applicableExecutors.size(), check.getClass().getName());
            throw new RuntimeException("executor count mismatch (no executor? too many executors?)");
        }

        return applicableExecutors.get(0);
    }

    /**
     * @return the number of parallel checks to execute
     */
    public int getNumberOfParallelTask() {
        return numberOfParallelTask;
    }


    /**
     * @param numberOfParallelTask the number of parallel checks to execute
     */
    public void setNumberOfParallelTask(int numberOfParallelTask) {
        this.numberOfParallelTask = numberOfParallelTask;
    }
}
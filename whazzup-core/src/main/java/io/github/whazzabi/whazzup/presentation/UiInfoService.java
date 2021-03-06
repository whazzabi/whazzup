package io.github.whazzabi.whazzup.presentation;

import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.check.CheckProvider;
import io.github.whazzabi.whazzup.business.check.CheckService;
import io.github.whazzabi.whazzup.business.check.checkresult.CheckResult;
import io.github.whazzabi.whazzup.business.check.checkresult.CheckResultCommentRepository;
import io.github.whazzabi.whazzup.business.customization.Group;
import io.github.whazzabi.whazzup.business.failure.FailingCheck;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UiInfoService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(UiInfoService.class);
    private final String applicationStartId = UUID.randomUUID().toString();

    // begin with an empty object. checks shall only be executed by the scheduler
    private UiInfo uiInfo = new UiInfo(applicationStartId);

    private List<Check> checkCache = new ArrayList<>();

    @Autowired
    private List<CheckProvider> checkProviders;

    @Autowired
    private CheckService checkService;

    @Autowired
    private CheckResultCommentRepository comments;

    public UiInfo infos() {
        return uiInfo;
    }

    @Scheduled(fixedDelay = 60_000)
    public void check() {

        // execute them
        List<CheckResult> checkResults = checkService.check(allChecks());

        // map checkresults to infos
        Map<Group, List<CheckResult>> checkResultsMappedToGroup = mapResultsToGroups(checkResults);

        // build uigroup-object and set field
        this.uiInfo = buildUiGroups(checkResultsMappedToGroup);

        removeCommentsForGreenChecks();
    }

    private void removeCommentsForGreenChecks() {
        this.uiInfo.getGroups().forEach(group -> {
            group.getChecks().forEach(check -> {
                if (check.getState() == State.GREEN) {
                    comments.clearCommentsForCheck(check.getCheckResultIdentifier());
                }
            });
        });
    }

    private UiInfo buildUiGroups(Map<Group, List<CheckResult>> checkResultsMappedToGroup) {

        UiInfo newUiInfo = new UiInfo(applicationStartId);

        newUiInfo.setLastUpdateTime(Long.toString(System.currentTimeMillis()));

        for (Group group : checkResultsMappedToGroup.keySet()) {

            UIGroup uiGroup = new UIGroup();
            uiGroup.setName(group.toString());
            uiGroup.setOrderScore(group.getOrderScore());

            List<CheckResult> checkResultsForGroup = checkResultsMappedToGroup.get(group);

            Integer testCount = 0;
            Integer failCount = 0;
            State state = null;
            for (CheckResult checkResult : checkResultsForGroup) {
                testCount += checkResult.getTestCount();
                failCount += checkResult.getFailCount();
                state = aggregate(state, checkResult.getState());
                uiGroup.add(checkResult);
            }
            uiGroup.setTotalCount(testCount);
            uiGroup.setFailCount(failCount);

            if (group.groupStatsEnabled()) {
                String info = failCount > 0 ? failCount + "/" + testCount : testCount.toString();
                uiGroup.setInfo(info);
            }

            uiGroup.setState(state);

            uiGroup.withMetaInfo(group.getMetaInfo());

            newUiInfo.add(uiGroup);
        }
        return newUiInfo;
    }

    State aggregate(State state1, State state2) {

        if (state1 == null) {
            return state2;
        }

        if (state2 == null) {
            return state1;
        }

        if (state1 == State.RED || state2 == State.RED) {
            return State.RED;
        }
        if (state1 == State.YELLOW || state2 == State.YELLOW) {
            return State.YELLOW;
        }
        if (state1 == State.GREY || state2 == State.GREY) {
            return State.GREY;
        }

        if (state1 == State.GREEN || state2 == State.GREEN) {
            return State.GREEN;
        }
        return State.GREY;
    }

    private Map<Group, List<CheckResult>> mapResultsToGroups(List<CheckResult> checkResults) {

        Map<Group, List<CheckResult>> checkResultsMappedToGroup = new HashMap<>();
        for (CheckResult checkResult : checkResults) {

            Group checkGroup = checkResult.getGroup();
            if (!checkResultsMappedToGroup.containsKey(checkGroup)) {
                checkResultsMappedToGroup.put(checkGroup, new ArrayList<>());
            }
            List<CheckResult> checkResultsForGroup = checkResultsMappedToGroup.get(checkGroup);
            checkResultsForGroup.add(checkResult);
        }
        return checkResultsMappedToGroup;
    }

    private List<Check> allChecks() {
        if (checkCache.isEmpty()) {
            List<Check> allChecks = new ArrayList<>();
            checkProviders.forEach(checkProvider -> {
                try {
                    allChecks.addAll(checkProvider.provideChecks());
                } catch (Exception e) {
                    log.error("exception during check provider execution with provider: " + checkProvider, e);
                    allChecks.add(new FailingCheck(checkProvider.getClass().getSimpleName(), e.getMessage()));
                }
            });
            checkCache = allChecks;
        }

        return checkCache;
    }
}

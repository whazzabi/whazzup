package io.github.whazzabi.whazzup.business.cloudwatch;

import com.amazonaws.services.cloudwatch.model.DescribeAlarmsRequest;
import com.amazonaws.services.cloudwatch.model.DescribeAlarmsResult;
import com.amazonaws.services.cloudwatch.model.MetricAlarm;
import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.check.CheckExecutor;
import io.github.whazzabi.whazzup.business.check.checkresult.CheckResult;
import io.github.whazzabi.whazzup.presentation.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {@link CheckExecutor} for {@link CloudWatchCheck}.
 */
@Service
public class CloudWatchCheckExecutor implements CheckExecutor<CloudWatchCheck> {

    @Autowired
    private CloudWatchStateMapper stateMapper;

    @Autowired
    private CloudWatchTeamMapper teamMapper;

    @Autowired
    private CloudWatchResultDecorator checkResultDecorator;

    @Override
    public List<CheckResult> executeCheck(final CloudWatchCheck check) {
        List<CheckResult> checks = new ArrayList<>();
        DescribeAlarmsRequest describeAlarmsRequest = new DescribeAlarmsRequest();

        do {
            DescribeAlarmsResult describeAlarmsResult = check.getCloudWatch().describeAlarms(describeAlarmsRequest);
            checks.addAll(describeAlarmsResult.getMetricAlarms()
                    .stream()
                    .map(v -> createCheckResult(v, check))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
            describeAlarmsRequest.setNextToken(describeAlarmsResult.getNextToken());
        } while (describeAlarmsRequest.getNextToken() != null);

        return checks;
    }

    private CheckResult createCheckResult(final MetricAlarm metricAlarm, final CloudWatchCheck check) {
        final State state = stateMapper.mapState(metricAlarm.getStateValue());
        final String name = metricAlarm.getAlarmName();
        final String info = metricAlarm.getAlarmDescription();

        CheckResult checkResult = new CheckResult(
                state,
                name,
                info,
                1,
                state == State.GREEN ? 0 : 1,
                check.getGroup())
                .withCheckResultIdentifier(check.getRegion() + "_" + name)
                .withTeamNames(teamMapper.map(metricAlarm));

        String awsRegion = check.getRegion();
        String alarmNameForLinks = metricAlarm.getAlarmName().replaceAll(" ", "+");
        checkResult.withLink("https://" + awsRegion + ".console.aws.amazon.com/cloudwatch/home?region=" + awsRegion + "#alarmsV2:alarm/" + alarmNameForLinks + "?");

        return checkResultDecorator.decorate(checkResult, check, metricAlarm);
    }

    @Override
    public boolean isApplicable(final Check check) {
        return check instanceof CloudWatchCheck;
    }
}

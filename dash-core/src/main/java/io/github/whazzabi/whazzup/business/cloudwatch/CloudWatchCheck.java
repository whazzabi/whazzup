package io.github.whazzabi.whazzup.business.cloudwatch;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import io.github.whazzabi.whazzup.business.check.Check;
import io.github.whazzabi.whazzup.business.customization.Group;
import io.github.whazzabi.whazzup.business.customization.Team;

import java.util.List;

/**
 * Amazon Cloudwatch {@link Check}.
 */
public class CloudWatchCheck extends Check {

    private final AmazonCloudWatch cloudWatch;
    private final String region;

    public CloudWatchCheck(
            String name,
            Group group,
            List<Team> teams,
            String awsRegion) {
        super(name, group, teams);
        this.region = awsRegion;
        this.cloudWatch = AmazonCloudWatchClientBuilder
                .standard()
                .withRegion(awsRegion)
                .build();;
    }

    public AmazonCloudWatch getCloudWatch() {
        return this.cloudWatch;
    }

    public String getRegion() {
        return region;
    }
}

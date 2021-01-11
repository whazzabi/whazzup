package io.github.whazzabi.whazzup.business.cloudwatch;

import com.amazonaws.auth.AWSCredentialsProvider;
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
            String awsRegion,
            AWSCredentialsProvider credentials) {
        super(name, group, teams);
        this.region = awsRegion;
        this.cloudWatch = AmazonCloudWatchClientBuilder
                .standard()
                .withRegion(awsRegion)
                .withCredentials(credentials)
                .build();
    }

    public CloudWatchCheck(
            String name,
            Group group,
            List<Team> teams,
            String awsRegion) {
        this(name, group, teams, awsRegion, null);
    }

    public AmazonCloudWatch getCloudWatch() {
        return this.cloudWatch;
    }

    public String getRegion() {
        return region;
    }
}

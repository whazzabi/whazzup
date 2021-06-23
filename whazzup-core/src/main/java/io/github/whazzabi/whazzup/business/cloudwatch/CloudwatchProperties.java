package io.github.whazzabi.whazzup.business.cloudwatch;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "dash.cloudwatch")
public class CloudwatchProperties {

    private Map<String, MappingPredicates> alarmToTeamsMapping;

    public Map<String, MappingPredicates> getAlarmToTeamsMapping() {
        return alarmToTeamsMapping;
    }

    public void setAlarmToTeamsMapping(Map<String, MappingPredicates> alarmToTeamsMapping) {
        this.alarmToTeamsMapping = alarmToTeamsMapping;
    }
}

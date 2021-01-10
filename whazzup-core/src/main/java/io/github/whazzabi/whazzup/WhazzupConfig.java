package io.github.whazzabi.whazzup;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import io.github.whazzabi.whazzup.business.cloudwatch.CloudWatchConfig;
import io.github.whazzabi.whazzup.business.jira.JiraConfig;
import io.github.whazzabi.whazzup.config.ClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan(basePackages = "io.github.whazzabi.whazzup", excludeFilters = @ComponentScan.Filter(Configuration.class))
@Import({ClientConfig.class, JiraConfig.class, CloudWatchConfig.class})
public class WhazzupConfig {

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public JsonParser jsonParser() {
        return new JsonParser();
    }
}

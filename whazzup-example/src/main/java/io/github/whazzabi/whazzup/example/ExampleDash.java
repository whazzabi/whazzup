package io.github.whazzabi.whazzup.example;

import io.github.whazzabi.whazzup.WhazzupConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(WhazzupConfig.class)
public class ExampleDash {

    public static void main(String[] args) {
        SpringApplication.run(ExampleDash.class, args);
    }
}

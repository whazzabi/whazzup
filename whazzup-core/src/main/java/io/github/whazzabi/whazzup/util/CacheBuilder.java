package io.github.whazzabi.whazzup.util;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

public class CacheBuilder {

    public static <K, V> ConcurrentHashMap<K, V> cache(Integer cacheDuraction, ChronoUnit timeUnit) {
        return new WeakConcurrentHashMap<>(Duration.of(cacheDuraction, timeUnit).toMillis());
    }
}

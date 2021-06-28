package io.github.whazzabi.whazzup.util;

import io.github.whazzabi.whazzup.business.github.common.GithubRepositoryMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WeakConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

    private static final Logger LOG = LoggerFactory.getLogger(WeakConcurrentHashMap.class);

    private Map<K, Long> timeMap = new ConcurrentHashMap<>();
    private long expiryInMillis = 1000;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SSS");

    public WeakConcurrentHashMap() {
        initialize();
    }

    public WeakConcurrentHashMap(long expiryInMillis) {
        this.expiryInMillis = expiryInMillis;
        initialize();
    }

    void initialize() {
        new CleanerThread().start();
    }

    @Override
    public V put(K key, V value) {
        Date date = new Date();
        timeMap.put(key, date.getTime());
        V returnVal = super.put(key, value);
        return returnVal;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (K key : m.keySet()) {
            put(key, m.get(key));
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (!containsKey(key))
            return put(key, value);
        else
            return get(key);
    }

    class CleanerThread extends Thread {
        @Override
        public void run() {
            LOG.debug("Initiating Cleaner Thread..");
            while (true) {
                cleanMap();
                try {
                    Thread.sleep(expiryInMillis / 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void cleanMap() {
            long currentTime = new Date().getTime();
            for (K key : timeMap.keySet()) {
                if (currentTime > (timeMap.get(key) + expiryInMillis)) {
                    V value = remove(key);
                    timeMap.remove(key);
                    LOG.debug("Removing : " + sdf.format(new Date()) + " : " + key + " : " + value);
                }
            }
        }
    }
}
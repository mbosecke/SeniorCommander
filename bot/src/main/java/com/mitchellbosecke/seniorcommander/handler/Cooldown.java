package com.mitchellbosecke.seniorcommander.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by mitch_000 on 2016-07-06.
 */
public class Cooldown {

    Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, Long> userTimestamps = new HashMap<>();

    private final long cooldown;

    private final TimeUnit timeUnit;

    public Cooldown(long cooldown, TimeUnit timeUnit) {
        this.cooldown = cooldown;
        this.timeUnit = timeUnit;
    }

    public void reset(String user) {
        cleanup();
        long now = now();
        userTimestamps.put(user, now);
    }

    public long secondsRemaining(String user) {
        long seconds = 0;
        if(userTimestamps.containsKey(user)){
            seconds = secondsRemaining(userTimestamps.get(user));
        }
        logger.trace(String.format("Cooldown seconds remaining for sender [%s] is: %d", user, seconds));
        return seconds;
    }

    private long secondsRemaining(long lastExecutionTimestamp){
        long elapsed = now() - lastExecutionTimestamp;
        long required = timeUnit.toSeconds(cooldown);
        return required -elapsed;
    }

    private long now(){
        return new Date().getTime() / 1000;
    }

    public boolean isReady(String user) {
        return secondsRemaining(user) <= 0;
    }

    private void cleanup(){
        Iterator<Map.Entry<String, Long>> iterator = userTimestamps.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, Long> timestamp = iterator.next();
            if(secondsRemaining(timestamp.getValue()) == 0){
                iterator.remove();
            }
        }
    }
}

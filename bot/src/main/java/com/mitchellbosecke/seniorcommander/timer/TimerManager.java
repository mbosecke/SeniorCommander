package com.mitchellbosecke.seniorcommander.timer;

import com.mitchellbosecke.seniorcommander.utils.ExecutorUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by mitch_000 on 2016-07-31.
 */
public class TimerManager {

    private final Map<Long, Timer> timers = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> ongoingTasks = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executorService;

    public TimerManager(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    public void addTimer(Timer timer){
        startTimer(timer);
        timers.put(timer.getId(), timer);
    }

    public void disableTimer(long id) {
        if (ongoingTasks.containsKey(id)) {
            ongoingTasks.get(id).cancel(false);
        }
    }

    public void enableTimer(long id){
        startTimer(timers.get(id));
    }

    private void startTimer(Timer timer){
        ScheduledFuture<?> future = executorService
                .scheduleAtFixedRate(() -> timer.perform(), timer.getInterval(), timer.getInterval(), TimeUnit
                        .SECONDS);
        ongoingTasks.put(timer.getId(), future);
    }

    public void shutdown(){
        ExecutorUtils.shutdown(executorService, 5, TimeUnit.SECONDS);
    }

}

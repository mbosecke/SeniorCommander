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

    private final Map<Long, ScheduledFuture<?>> ongoingTimers = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executorService;

    public TimerManager(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    /*
    public void startTimer(TimerModel timerModel) {
        if (tasks.containsKey(timerModel.getId())) {
            if (tasks.get(timerModel.getId()).isCancelled()) {
                scheduleTimer(timerModel);
            }
        } else {
            scheduleTimer(timerModel);
        }
    }
    */

    public void startTimer(Timer timer){
        ScheduledFuture<?> future = executorService
                .scheduleAtFixedRate(() -> timer.perform(), timer.getInterval(), timer.getInterval(), TimeUnit
                        .SECONDS);
        ongoingTimers.put(timer.getId(), future);
    }

    /*
    private Timer buildTask(TimerModel timerModel) {
        Timer timer = null;
        if (timerModel.getImplementation() == null) {
            timer = new Shout(messageQueue, timerModel.getMessage());
        }
        return timer;
    }

    private void scheduleTimer(TimerModel timerModel) {
        Timer timer = buildTask(timerModel);
        ScheduledFuture<?> future = executorService
                .scheduleAtFixedRate(() -> timer.perform(), timerModel.getInterval(), timerModel.getInterval(), TimeUnit.SECONDS);
        tasks.put(timerModel.getId(), future);
    }
    */

    public void stopTimer(long id) {
        if (ongoingTimers.containsKey(id)) {
            ongoingTimers.get(id).cancel(false);
        }
    }

    public void shutdown(){
        ExecutorUtils.shutdown(executorService, 5, TimeUnit.SECONDS);
    }

}

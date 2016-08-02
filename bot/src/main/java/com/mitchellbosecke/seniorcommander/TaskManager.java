package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.domain.Timer;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.timer.Shout;
import com.mitchellbosecke.seniorcommander.timer.Task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by mitch_000 on 2016-07-31.
 */
public class TaskManager {

    private final Map<Long, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executorService;

    private final MessageQueue messageQueue;

    public TaskManager(ScheduledExecutorService executorService, MessageQueue messageQueue) {
        this.executorService = executorService;
        this.messageQueue = messageQueue;
    }

    public void startTimer(Timer timer) {
        if (tasks.containsKey(timer.getId())) {
            if (tasks.get(timer.getId()).isCancelled()) {
                scheduleTask(timer);
            }
        } else {
            scheduleTask(timer);
        }
    }

    private Task buildTask(Timer timer) {
        Task task = null;
        if (timer.getImplementation() == null) {
            task = new Shout(messageQueue, timer.getMessage());
        }
        return task;
    }

    private void scheduleTask(Timer timer) {
        Task task = buildTask(timer);
        ScheduledFuture<?> future = executorService
                .scheduleAtFixedRate(() -> task.perform(), timer.getInterval(), timer.getInterval(), TimeUnit.SECONDS);
        tasks.put(timer.getId(), future);
    }

    public void stopTimer(long id) {
        if (tasks.containsKey(id)) {
            tasks.get(id).cancel(false);
        }
    }

}

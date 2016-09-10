package com.mitchellbosecke.seniorcommander.task;

import com.mitchellbosecke.seniorcommander.domain.TimerModel;
import com.mitchellbosecke.seniorcommander.extension.core.task.Shout;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;

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

    public void startTimer(TimerModel timerModel) {
        if (tasks.containsKey(timerModel.getId())) {
            if (tasks.get(timerModel.getId()).isCancelled()) {
                scheduleTask(timerModel);
            }
        } else {
            scheduleTask(timerModel);
        }
    }

    private Task buildTask(TimerModel timerModel) {
        Task task = null;
        if (timerModel.getImplementation() == null) {
            task = new Shout(messageQueue, timerModel.getMessage());
        }
        return task;
    }

    private void scheduleTask(TimerModel timerModel) {
        Task task = buildTask(timerModel);
        ScheduledFuture<?> future = executorService
                .scheduleAtFixedRate(() -> task.perform(), timerModel.getInterval(), timerModel.getInterval(), TimeUnit.SECONDS);
        tasks.put(timerModel.getId(), future);
    }

    public void stopTimer(long id) {
        if (tasks.containsKey(id)) {
            tasks.get(id).cancel(false);
        }
    }

}

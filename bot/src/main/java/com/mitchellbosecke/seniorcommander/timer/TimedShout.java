package com.mitchellbosecke.seniorcommander.timer;

import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by mitch_000 on 2016-07-06.
 */
public class TimedShout implements Timer {

    private final String content;

    private final long minutes;

    private final ScheduledExecutorService scheduledExecutorService;

    private final MessageQueue messageQueue;

    public TimedShout(ScheduledExecutorService scheduledExecutorService, MessageQueue messageQueue, String content,
                      long minutes) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.messageQueue = messageQueue;
        this.content = content;
        this.minutes = minutes;
    }

    @Override
    public void run() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            messageQueue.add(Message.shout(content));
        }, minutes, minutes, TimeUnit.MINUTES);
    }
}

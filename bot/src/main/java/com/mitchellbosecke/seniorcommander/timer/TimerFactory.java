package com.mitchellbosecke.seniorcommander.timer;

import com.mitchellbosecke.seniorcommander.message.MessageQueue;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface TimerFactory {

    List<Timer> build(ScheduledExecutorService scheduledExecutorService, MessageQueue messageQueue);

}

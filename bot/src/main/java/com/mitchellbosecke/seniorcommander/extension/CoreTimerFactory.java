package com.mitchellbosecke.seniorcommander.extension;

import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.timer.TimedShout;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import com.mitchellbosecke.seniorcommander.timer.TimerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class CoreTimerFactory implements TimerFactory {

    @Override
    public List<Timer> build(ScheduledExecutorService scheduledExecutorService, MessageQueue messageQueue) {
        List<Timer> timers = new LinkedList<>();
        timers.add(new TimedShout(scheduledExecutorService, messageQueue, "yo", 2l));
        return timers;
    }
}

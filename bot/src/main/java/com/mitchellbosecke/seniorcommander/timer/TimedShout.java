package com.mitchellbosecke.seniorcommander.timer;

import com.mitchellbosecke.seniorcommander.Context;
import com.mitchellbosecke.seniorcommander.message.Message;

import java.util.concurrent.TimeUnit;

/**
 * Created by mitch_000 on 2016-07-06.
 */
public class TimedShout implements Timer {

    private final String content;

    private final long minutes;

    public TimedShout(String content, long minutes) {
        this.content = content;
        this.minutes = minutes;
    }

    @Override
    public void run(Context context) {
        context.getScheduledExecutorService().scheduleAtFixedRate(() -> {
            context.getMessageQueue().add(Message.shout(content));
        }, minutes, minutes, TimeUnit.MINUTES);
    }
}

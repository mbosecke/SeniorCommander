package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.extension.core.channel.TwitchChannel;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import com.mitchellbosecke.seniorcommander.twitch.TwitchApi;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class TwitchOnlineChecker implements Timer {

    private final long id;
    private final long interval;
    private final TwitchChannel channel;

    public TwitchOnlineChecker(long id, long interval, TwitchChannel channel) {
        this.id = id;
        this.interval = interval;
        this.channel = channel;
    }

    @Override
    public void perform() {
        boolean isOnline = new TwitchApi().stream(channel.getChannel()) != null;
        channel.setOnline(isOnline);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public long getInterval() {
        return interval;
    }
}

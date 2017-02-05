package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.extension.core.channel.TwitchChannel;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import com.mitchellbosecke.twitchapi.TwitchApi;
import com.typesafe.config.ConfigFactory;

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
        String twitchClientId = ConfigFactory.load().getConfig("seniorcommander").getString("twitch.clientId");
        boolean isOnline = new TwitchApi(twitchClientId).stream(channel.getChannel()) != null;
        channel.setCommunityOnline(isOnline);
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

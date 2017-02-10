package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.extension.core.channel.TwitchChannel;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import com.mitchellbosecke.seniorcommander.utils.ConfigUtils;
import com.mitchellbosecke.twitchapi.TwitchApi;

import java.util.Optional;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class TwitchOnlineChecker implements Timer {

    private final long id;
    private final long interval;
    private final long channelId;
    private final SeniorCommander seniorCommander;

    public TwitchOnlineChecker(long id, long interval, long channelId, SeniorCommander seniorCommander) {
        this.id = id;
        this.interval = interval;
        this.channelId = channelId;
        this.seniorCommander = seniorCommander;
    }

    @Override
    public void perform() {
        Optional<Channel> optionalChannel = seniorCommander.getChannelManager().getChannel(channelId);

        if (optionalChannel.isPresent()) {
            TwitchChannel channel = (TwitchChannel) optionalChannel.get();
            String twitchClientId = ConfigUtils.getString("twitch.clientId");
            boolean isOnline = new TwitchApi(twitchClientId).stream(channel.getChannel()) != null;
            channel.setCommunityOnline(isOnline);
        }

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

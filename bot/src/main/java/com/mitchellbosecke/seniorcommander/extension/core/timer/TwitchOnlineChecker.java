package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.models.Stream;
import com.mitchellbosecke.seniorcommander.extension.core.channel.IrcChannel;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import com.typesafe.config.ConfigFactory;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class TwitchOnlineChecker implements Timer {

    private final long id;
    private final long interval;
    private final IrcChannel channel;

    public TwitchOnlineChecker(long id, long interval, IrcChannel channel) {
        this.id = id;
        this.interval = interval;
        this.channel = channel;
    }

    @Override
    public void perform() {
        Twitch client = new Twitch();
        client.setClientId(ConfigFactory.load().getString("twitch.clientId"));

        String channelName = channel.getChannel().replaceAll("#", "");
        client.streams().get(channelName, new StreamResponseHandler() {
            @Override
            public void onSuccess(Stream stream) {
                if(stream == null){
                    channel.setOnline(false);
                }else{
                    channel.setOnline(true);
                }
            }

            @Override
            public void onFailure(int i, String s, String s1) {
                channel.setOnline(false);
            }

            @Override
            public void onFailure(Throwable throwable) {
                channel.setOnline(false);
            }
        });
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

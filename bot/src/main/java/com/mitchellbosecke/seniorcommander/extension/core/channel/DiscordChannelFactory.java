package com.mitchellbosecke.seniorcommander.extension.core.channel;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class DiscordChannelFactory implements ChannelFactory {

    private static final String CONFIG_TOKEN = "token";
    private static final String CONFIG_GUILD = "guild";
    private static final String CONFIG_CHANNEL = "channel";

    @Override
    public boolean supports(String type) {
        return "discord".equalsIgnoreCase(type);
    }

    @Override
    public Channel build(ChannelModel channelModel) {
        String token = channelModel.getSetting(CONFIG_TOKEN);
        String guild = channelModel.getSetting(CONFIG_GUILD);
        String channel = channelModel.getSetting(CONFIG_CHANNEL);

        return new DiscordChannel(channelModel.getId(), token, guild, channel);
    }
}

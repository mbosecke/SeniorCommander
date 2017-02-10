package com.mitchellbosecke.seniorcommander.extension.core.channel;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class SocketChannelFactory implements ChannelFactory {

    private static final String CONFIG_PORT = "port";

    @Override
    public boolean supports(String type) {
        return "socket".equalsIgnoreCase(type);
    }

    @Override
    public Channel build(ChannelModel channelModel) {
        Integer port = Integer.valueOf(channelModel.getSetting(CONFIG_PORT));

        return new SocketChannel(channelModel.getId(), port);
    }
}

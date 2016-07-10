package com.mitchellbosecke.seniorcommander.channel;

import com.mitchellbosecke.seniorcommander.domain.ChannelConfiguration;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class SocketChannelFactory implements ChannelFactory {

    private static final String CONFIG_PORT = "port";

    @Override
    public List<Channel> build(Session session) {
        List<Channel> socketChannels = new ArrayList<>();

        List<ChannelConfiguration> channelConfigurations = session
                .createQuery("SELECT cc FROM ChannelConfiguration cc WHERE cc.type = 'socket'", ChannelConfiguration.class)
                .getResultList();

        for (ChannelConfiguration configuration : channelConfigurations) {

            Integer port = Integer.valueOf(configuration.getSetting(CONFIG_PORT));

            socketChannels.add(new SocketChannel(configuration.getId(), port));
        }

        return socketChannels;
    }
}

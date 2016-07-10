package com.mitchellbosecke.seniorcommander.channel;

import com.mitchellbosecke.seniorcommander.domain.ChannelConfiguration;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class IrcChannelFactory implements ChannelFactory {


    private static final String CONFIG_SERVER = "server";
    private static final String CONFIG_PORT = "port";
    private static final String CONFIG_USERNAME = "username";
    private static final String CONFIG_PASSWORD = "password";
    private static final String CONFIG_CHANNEL = "channel";

    @Override
    public List<Channel> build(Session session) {
        List<Channel> ircChannels = new ArrayList<>();

        List<ChannelConfiguration> channelConfigurations = session
                .createQuery("SELECT cc FROM ChannelConfiguration cc WHERE cc.type = 'irc'", ChannelConfiguration.class)
                .getResultList();

        for(ChannelConfiguration configuration : channelConfigurations){

            String server = configuration.getSetting(CONFIG_SERVER);
            Integer port = Integer.valueOf(configuration.getSetting(CONFIG_PORT));
            String username = configuration.getSetting(CONFIG_USERNAME);
            String password = configuration.getSetting(CONFIG_PASSWORD);
            String channel = configuration.getSetting(CONFIG_CHANNEL);

            ircChannels.add(new IrcChannel(server, port, username, password, channel));
        }

        return ircChannels;
    }
}

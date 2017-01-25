package com.mitchellbosecke.seniorcommander.extension.core.channel;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.channel.ChannelFactory;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class TwitchChannelFactory implements ChannelFactory {

    private static final Logger logger = LoggerFactory.getLogger(TwitchChannelFactory.class);

    private static final String CONFIG_SERVER = "server";
    private static final String CONFIG_PORT = "port";
    private static final String CONFIG_USERNAME = "username";
    private static final String CONFIG_PASSWORD = "password";
    private static final String CONFIG_CHANNEL = "channel";

    @Override
    public List<Channel> build(Session session) {
        List<Channel> ircChannels = new ArrayList<>();

        String hostname = null;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        //@formatter:off
        List<ChannelModel> channelModels = session
                .createQuery("" +
                        "SELECT cm " +
                        "FROM ChannelModel cm " +
                        "WHERE cm.type = 'irc' " +
                        "AND cm.communityModel.server = :server", ChannelModel.class)
                .setParameter("server", hostname)
                .getResultList();
        //@formatter:on

        for (ChannelModel channelModel : channelModels) {
            String server = channelModel.getSetting(CONFIG_SERVER);
            Integer port = Integer.valueOf(channelModel.getSetting(CONFIG_PORT));
            String username = channelModel.getSetting(CONFIG_USERNAME);
            String password = channelModel.getSetting(CONFIG_PASSWORD);
            String channel = channelModel.getSetting(CONFIG_CHANNEL);

            TwitchChannel twitchChannel = new TwitchChannel(channelModel
                    .getId(), server, port, username, password, channel);

            ircChannels.add(twitchChannel);
            logger.debug("Initiating channel [{}:{}]", username, channel);
        }

        return ircChannels;
    }
}

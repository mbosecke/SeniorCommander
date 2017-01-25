package com.mitchellbosecke.seniorcommander.extension.core.channel;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.channel.ChannelFactory;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;
import org.hibernate.Session;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class DiscordChannelFactory implements ChannelFactory {

    private static final String CONFIG_TOKEN = "token";
    private static final String CONFIG_GUILD = "guild";
    private static final String CONFIG_CHANNEL = "channel";

    @Override
    public List<Channel> build(Session session) {
        List<Channel> channels = new ArrayList<>();

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
                        "WHERE cm.type = 'discord' " +
                        "AND cm.communityModel.server = :server", ChannelModel.class)
                .setParameter("server", hostname)
                .getResultList();
        //@formatter:on

        for (ChannelModel channelModel : channelModels) {

            String token = channelModel.getSetting(CONFIG_TOKEN);
            String guild = channelModel.getSetting(CONFIG_GUILD);
            String channel = channelModel.getSetting(CONFIG_CHANNEL);

            DiscordChannel discordChannel  = new DiscordChannel(channelModel.getId(), token, guild, channel);

            channels.add(discordChannel);
        }

        return channels;
    }
}

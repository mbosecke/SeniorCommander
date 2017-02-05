package com.mitchellbosecke.seniorcommander.extension.core.channel;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;
import com.mitchellbosecke.seniorcommander.utils.NetworkUtils;
import com.mitchellbosecke.seniorcommander.utils.TransactionManager;

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
    public List<Channel> build() {
        List<Channel> channels = new ArrayList<>();

        //@formatter:off
        List<ChannelModel> channelModels =  TransactionManager.getCurrentSession()
                .createQuery("" +
                        "SELECT cm " +
                        "FROM ChannelModel cm " +
                        "WHERE cm.type = 'discord' " +
                        "AND cm.communityModel.server = :server", ChannelModel.class)
                .setParameter("server",  NetworkUtils.getLocalHostname())
                .getResultList();
        //@formatter:on

        for (ChannelModel channelModel : channelModels) {

            String token = channelModel.getSetting(CONFIG_TOKEN);
            String guild = channelModel.getSetting(CONFIG_GUILD);
            String channel = channelModel.getSetting(CONFIG_CHANNEL);

            DiscordChannel discordChannel = new DiscordChannel(channelModel.getId(), token, guild, channel);

            channels.add(discordChannel);
        }

        return channels;
    }
}

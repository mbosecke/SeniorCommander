package com.mitchellbosecke.seniorcommander.extension.core.channel;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;
import com.mitchellbosecke.seniorcommander.utils.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public boolean supports(String type) {
        return "irc".equalsIgnoreCase(type);
    }

    @Override
    public Channel build(ChannelModel channelModel) {
        String server = channelModel.getSetting(CONFIG_SERVER);
        Integer port = Integer.valueOf(channelModel.getSetting(CONFIG_PORT));
        String username = channelModel.getSetting(CONFIG_USERNAME);
        String password = channelModel.getSetting(CONFIG_PASSWORD);
        String channel = channelModel.getSetting(CONFIG_CHANNEL);

        username = username == null ? ConfigUtils.getString("twitch.defaultUsername") : username;
        password = password == null ? ConfigUtils.getString("twitch.defaultPassword") : password;

        TwitchChannel twitchChannel = new TwitchChannel(channelModel
                .getId(), server, port, username, password, channel);

        logger.debug("Initiating channel [{}:{}]", username, channel);
        return twitchChannel;
    }
}

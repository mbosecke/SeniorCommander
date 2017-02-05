package com.mitchellbosecke.seniorcommander.extension.core.channel;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;
import com.mitchellbosecke.seniorcommander.utils.TransactionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class SocketChannelFactory implements ChannelFactory {

    private static final String CONFIG_PORT = "port";

    @Override
    public List<Channel> build() {
        List<Channel> socketChannels = new ArrayList<>();

        List<ChannelModel> channelModels = TransactionManager.getCurrentSession()
                .createQuery("SELECT cc FROM ChannelModel cc WHERE cc.type = 'socket'", ChannelModel.class)
                .getResultList();

        for (ChannelModel configuration : channelModels) {

            Integer port = Integer.valueOf(configuration.getSetting(CONFIG_PORT));

            socketChannels.add(new SocketChannel(configuration.getId(), port));
        }

        return socketChannels;
    }
}

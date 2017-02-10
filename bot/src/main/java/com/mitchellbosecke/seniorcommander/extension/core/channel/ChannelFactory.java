package com.mitchellbosecke.seniorcommander.extension.core.channel;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface ChannelFactory {

    boolean supports(String type);

    Channel build(ChannelModel channelModel);

}

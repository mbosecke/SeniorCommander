package com.mitchellbosecke.seniorcommander.channel;

import java.util.List;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface ChannelFactory {

    List<Channel> build();

}

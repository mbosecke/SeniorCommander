package com.mitchellbosecke.seniorcommander.extension;

import com.mitchellbosecke.seniorcommander.channel.ChannelFactory;
import com.mitchellbosecke.seniorcommander.message.MessageHandlerFactory;
import com.mitchellbosecke.seniorcommander.timer.TimerFactory;

import java.util.List;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public interface Extension {

    MessageHandlerFactory getMessageHandlerFactory();

    List<ChannelFactory> getChannelFactories();

    TimerFactory getTimerFactory();
}

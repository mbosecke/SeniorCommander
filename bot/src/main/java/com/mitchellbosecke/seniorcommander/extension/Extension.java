package com.mitchellbosecke.seniorcommander.extension;

import com.mitchellbosecke.seniorcommander.channel.ChannelFactory;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;
import com.mitchellbosecke.seniorcommander.timer.Timer;

import java.util.List;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public interface Extension {

    List<MessageHandler> getMessageHandlers();

    List<ChannelFactory> getChannelFactories();

    List<Timer> getTimers();
}

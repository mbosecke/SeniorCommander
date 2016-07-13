package com.mitchellbosecke.seniorcommander.extension.core;

import com.mitchellbosecke.seniorcommander.channel.ChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.Extension;
import com.mitchellbosecke.seniorcommander.extension.core.channel.IrcChannelFactory;
import com.mitchellbosecke.seniorcommander.extension.core.channel.SocketChannelFactory;
import com.mitchellbosecke.seniorcommander.message.MessageHandlerFactory;
import com.mitchellbosecke.seniorcommander.timer.TimerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public class CoreExtension implements Extension {

    @Override
    public MessageHandlerFactory getMessageHandlerFactory() {
        return new CoreMessageHandlerFactory();
    }

    @Override
    public List<ChannelFactory> getChannelFactories() {
        List<ChannelFactory> factories = new ArrayList<>();
        factories.add(new IrcChannelFactory());
        factories.add(new SocketChannelFactory());
        return factories;
    }

    @Override
    public TimerFactory getTimerFactory() {
        return new CoreTimerFactory();
    }
}

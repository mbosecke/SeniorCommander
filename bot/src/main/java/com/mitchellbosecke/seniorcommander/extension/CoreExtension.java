package com.mitchellbosecke.seniorcommander.extension;

import com.mitchellbosecke.seniorcommander.channel.ChannelFactory;
import com.mitchellbosecke.seniorcommander.channel.IrcChannelFactory;
import com.mitchellbosecke.seniorcommander.channel.SocketChannelFactory;
import com.mitchellbosecke.seniorcommander.handler.MessageHandlerFactory;
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

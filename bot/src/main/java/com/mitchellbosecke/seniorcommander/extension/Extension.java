package com.mitchellbosecke.seniorcommander.extension;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.channel.ChannelFactory;
import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.timer.TimerFactory;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public interface Extension {

    List<EventHandler> buildEventHandlers(SessionFactory sessionFactory, MessageQueue messageQueue,
                                          List<Channel> channels, List<CommandHandler> commandHandlers);

    List<ChannelFactory> getChannelFactories();

    TimerFactory getTimerFactory();

    List<CommandHandler> buildCommandHandlers(SessionFactory sessionFactory, MessageQueue messageQueue);
}

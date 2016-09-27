package com.mitchellbosecke.seniorcommander.extension;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.channel.ChannelFactory;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.timer.TimerManager;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public interface Extension {

    /**
     * Builds and returns all event handlers.
     *
     * @param sessionFactory
     * @param messageQueue
     * @param channels
     * @param commandHandlers
     * @return
     */
    List<EventHandler> buildEventHandlers(SessionFactory sessionFactory, MessageQueue messageQueue,
                                          List<Channel> channels, List<CommandHandler> commandHandlers);

    /**
     * Builds and returns channel factories
     *
     * @return
     */
    List<ChannelFactory> getChannelFactories();

    /**
     * Should build and register all timers with the {@link TimerManager}
     *
     * @param sessionFactory
     * @param messageQueue
     * @param channels
     * @param timerManager
     */
    void startTimers(SessionFactory sessionFactory, MessageQueue messageQueue, List<Channel> channels,
                     TimerManager timerManager);

    /**
     * Builds and returns all command handlers.
     *
     * @param sessionFactory
     * @param messageQueue
     * @param timerManager
     * @return
     */
    List<CommandHandler> buildCommandHandlers(SessionFactory sessionFactory, MessageQueue messageQueue,
                                              TimerManager timerManager);

    /**
     * A hook for the bot shutdown which can be used to perform any cleanup.
     *
     * @param sessionFactory
     */
    void onShutdown(SessionFactory sessionFactory);
}

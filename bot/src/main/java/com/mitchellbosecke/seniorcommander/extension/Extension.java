package com.mitchellbosecke.seniorcommander.extension;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import com.mitchellbosecke.seniorcommander.timer.TimerManager;

import java.util.List;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public interface Extension {

    /**
     * Builds and returns all event handlers.
     *
     * @param seniorCommander
     * @return
     */
    List<EventHandler> buildEventHandlers(SeniorCommander seniorCommander);

    /**
     * Builds and returns channel factories
     *
     * @return
     */
    List<Channel> buildChannels();

    /**
     * Builds and returns timers
     *
     * @param messageQueue
     * @param channels
     */
    List<Timer> buildTimers(MessageQueue messageQueue, List<Channel> channels);

    /**
     * Builds and returns all command handlers.
     *
     * @param messageQueue
     * @param timerManager
     * @return
     */
    List<CommandHandler> buildCommandHandlers(MessageQueue messageQueue, TimerManager timerManager);

    /**
     * A hook for the bot shutdown which can be used to perform any cleanup.
     */
    void onShutdown();
}

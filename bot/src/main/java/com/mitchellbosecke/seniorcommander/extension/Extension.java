package com.mitchellbosecke.seniorcommander.extension;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.extension.core.channel.ChannelFactory;
import com.mitchellbosecke.seniorcommander.timer.Timer;

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
    List<ChannelFactory> buildChannelFactories();

    /**
     * Builds and returns timers
     *
     * @param seniorCommander
     */
    List<Timer> buildTimers(SeniorCommander seniorCommander);

    /**
     * Builds and returns all command handlers.
     *
     * @param seniorCommander
     * @return
     */
    List<CommandHandler> buildCommandHandlers(SeniorCommander seniorCommander);

    /**
     * A hook for the bot shutdown which can be used to perform any cleanup.
     */
    void onShutdown();
}

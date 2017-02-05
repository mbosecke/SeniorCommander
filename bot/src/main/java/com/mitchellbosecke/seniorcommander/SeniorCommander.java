package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.channel.ChannelManager;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.timer.TimerManager;

import java.io.IOException;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface SeniorCommander {
    /**
     * Name used to populate the "recipient" field on incoming messages, even if the channel
     * typically uses a custom name. This allows for a consistent method of detecting if a message
     * was targetted for the bot.
     *
     * @return
     */
    static String getName() {
        return SeniorCommander.class.getSimpleName();
    }

    void run();

    void shutdown();

    MessageQueue getMessageQueue();

    TimerManager getTimerManager();

    ChannelManager getChannelManager();

    List<EventHandler> getEventHandlers();

    List<CommandHandler> getCommandHandlers();

    static void main(String[] args) throws IOException {
        SeniorCommander commander = new SeniorCommanderImpl();
        commander.run();
    }
}

package com.mitchellbosecke.seniorcommander;

import java.io.IOException;

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

    static void main(String[] args) throws IOException {
        SeniorCommander commander = new SeniorCommanderImpl();
        commander.run();
    }
}

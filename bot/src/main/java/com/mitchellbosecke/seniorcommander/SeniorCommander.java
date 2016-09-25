package com.mitchellbosecke.seniorcommander;

import java.io.IOException;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface SeniorCommander {
    /**
     * Name used to populate the "sender" and "recipient" fields on a message.
     *
     * @return
     */
    static String getName() {
        return SeniorCommander.class.getName();
    }

    void run();

    void shutdown();

    static void main(String[] args) throws IOException {
        SeniorCommander commander = new SeniorCommanderImpl();
        commander.run();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                commander.shutdown();
            }
        });
    }
}

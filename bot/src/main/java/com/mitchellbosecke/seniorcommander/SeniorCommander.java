package com.mitchellbosecke.seniorcommander;

import java.io.IOException;
import java.util.Collections;

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
        Configuration config = new Configuration("config.properties");
        SeniorCommander commander = new SeniorCommanderImpl(config, Collections.emptyList());
        commander.run();
    }
}

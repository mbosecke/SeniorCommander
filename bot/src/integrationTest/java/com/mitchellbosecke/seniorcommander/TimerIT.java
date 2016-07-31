package com.mitchellbosecke.seniorcommander;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class TimerIT extends AbstractIT {

    private Logger logger = LoggerFactory.getLogger(TimerIT.class);

    @Test
    public void newQuote() throws InterruptedException {
        addTimer("Hello World", 1);
        Thread.sleep(30 * 1000);
        expectNoBotOutput();
        Thread.sleep(35 * 1000);
        recv("Hello World");
    }

    /**
     * Adds a quote and returns the ID of it
     *
     * @return
     */
    private long addTimer(String message, int interval) {
        Pattern pattern = Pattern.compile("Timer #([0-9]{1,2}) has been added");
        send(String.format("moderator: !timer add \"%s\" interval=%d", message, interval));
        String reply = recv(pattern);
        Matcher matcher = pattern.matcher(reply);
        matcher.matches();
        return Long.valueOf(matcher.group(1));
    }
}

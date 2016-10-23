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
    public void newTimer() throws InterruptedException {
        addTimer("Hello World", 10, 0);
        Thread.sleep(10 * 1000);
        recv("Hello World");
    }

    @Test
    public void ensureUserCantAddTimer() throws InterruptedException {
        send("user: !timer add \"hello world\" interval=1");
        expectNoBotOutput(); // would otherwise receive a confirmation response
    }


    @Test
    public void newTimerWithChatLinesRequirement() throws InterruptedException {
        int interval = 10;
        addTimer("Hello World", interval, 1);
        Thread.sleep(interval * 1000);
        recv("Hello World"); // first time a timer ignores chat lines
        Thread.sleep(interval * 1000);
        expectNoBotOutput();
        send("user: example chat line");
        Thread.sleep(interval * 1000);
        recv("Hello World");
    }

    @Test
    public void disableAndEnableTimer() throws InterruptedException {
        int interval = 10;
        long id = addTimer("Hello World", interval, 0);
        send("moderator: !timer disable " + id);
        recv(String.format("Timer #%d has been disabled", id));
        Thread.sleep(interval * 1000);
        expectNoBotOutput();
        send("moderator: !timer enable " + id);
        recv(String.format("Timer #%d has been enabled", id));
        Thread.sleep(interval * 1000);
        recv("Hello World");
    }


    /**
     * Adds a quote and returns the ID of it
     *
     * @return
     */
    private long addTimer(String message, int interval, int chatLines) {
        Pattern pattern = Pattern.compile("Timer #([0-9]{1,2}) has been added");
        send(String.format("admin: !timer add \"%s\" interval=%ds chat-lines=%d", message, interval, chatLines));
        String reply = recv(pattern);
        Matcher matcher = pattern.matcher(reply);
        matcher.matches();
        return Long.valueOf(matcher.group(1));
    }
}

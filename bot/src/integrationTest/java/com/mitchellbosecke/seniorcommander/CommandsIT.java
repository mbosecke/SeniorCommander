package com.mitchellbosecke.seniorcommander;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class CommandsIT extends AbstractIT {

    private Logger logger = LoggerFactory.getLogger(CommandsIT.class);

    @Test
    public void addCommand() {
        send("moderator: !command add !foo \"bar\"");
        recv("Command has been added: !foo");
        send("user: !foo");
        recv("bar");
    }

    @Test
    public void nonModeratorAddsCommand() {
        send("user: !command add !foo \"bar\"");
        send("user: !foo");
        expectNoBotOutput();
    }


    @Test
    public void commandWithAccessLevel() {
        send("moderator: !command add !foo \"bar\" access=regular");
        recv("Command has been added: !foo");
        send("user: !foo");
        expectNoBotOutput();
        send("regular: !foo");
        recv("bar");
    }

    @Test
    public void commandWithCooldown() throws InterruptedException {
        send("admin: !command add !foo \"bar\" cooldown=10s");
        recv("Command has been added: !foo");
        send("user: !foo");
        recv("bar");
        send("user: !foo");
        expectNoBotOutput();
        Thread.sleep(10 * 1000);
        send("user: !foo");
        recv("bar");
    }

    @Test
    public void addExistingCommand() {
        send("moderator: !command add !foo \"bar\"");
        recv("Command has been added: !foo");
        send("moderator: !command add !foo \"baz\"");
        recv("Command already exists.");
    }

    @Test
    public void forgetQuote() {
        send("moderator: !command add !foo bar");
        recv("You are missing the quoted text to be used as output");
    }

    @Test
    public void editCommand() {
        send("moderator: !command add !foo \"bar\"");
        recv("Command has been added: !foo");
        send("user: !foo");
        recv("bar");

        send("moderator: !command edit !foo \"baz\"");
        send("user: !foo");
        recv("baz");
    }

    @Test
    public void disableCommand() {
        send("moderator: !command add !foo \"bar\"");
        recv("Command has been added: !foo");
        send("user: !foo");
        recv("bar");

        send("moderator: !command disable !foo");
        recv("Command has been disabled: !foo");
        send("user: !foo");
        expectNoBotOutput();

        send("moderator: !command enable !foo");
        recv("Command has been enabled: !foo");
        send("user: !foo");
        recv("bar");
    }
}

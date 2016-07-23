package com.mitchellbosecke.seniorcommander;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class CommandsTest extends AbstractTest {

    private Logger logger = LoggerFactory.getLogger(CommandsTest.class);

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
        // expect no response
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
}

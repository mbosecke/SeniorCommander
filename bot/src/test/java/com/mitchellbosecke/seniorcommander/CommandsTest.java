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
        testCommandAndResult("!command add !foo \"bar\"", "Command has been added: !foo");
        testCommandAndResult("!foo", "bar");
    }

    @Test
    public void addExistingCommand() {
        sendAndIgnoreResponse("!command add !foo \"bar\"");
        testCommandAndResult("!command add !foo \"baz\"", "Command already exists.");
    }

    @Test
    public void forgetQuote() {
        testCommandAndResult("!command add !foo bar", "You are missing the quoted text to be used as output");
    }

    @Test
    public void editCommand() {
        sendAndIgnoreResponse("!command add !foo \"bar\"");
        testCommandAndResult("!foo", "bar");

        sendWithoutResponse("!command edit !foo \"baz\"");
        testCommandAndResult("!foo", "baz");
    }
}

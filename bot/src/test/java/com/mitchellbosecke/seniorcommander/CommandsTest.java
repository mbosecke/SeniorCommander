package com.mitchellbosecke.seniorcommander;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class CommandsTest extends AbstractTest {

    private Logger logger = LoggerFactory.getLogger(CommandsTest.class);

    @Test
    public void addCommand() {
        assertTrue(testCommandAndResult("!commands add !foo bar", "The command has been added."));
        assertTrue(testCommandAndResult("!foo", "bar"));
    }
}

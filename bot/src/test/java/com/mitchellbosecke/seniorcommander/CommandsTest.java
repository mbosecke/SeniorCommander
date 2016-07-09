package com.mitchellbosecke.seniorcommander;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class CommandsTest extends AbstractTest {

    @Test
    public void addCommand (){
        assertTrue(testCommandAndResult("!commands add !foo bar", "The command has been added."));
    }
}

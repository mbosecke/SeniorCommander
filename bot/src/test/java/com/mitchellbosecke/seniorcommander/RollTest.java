package com.mitchellbosecke.seniorcommander;

import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class RollTest extends AbstractTest {

    @Test
    public void rollD100() {
        testCommandAndResult("!roll 100", Pattern.compile("You rolled a \\d{1,2}"));
    }
}

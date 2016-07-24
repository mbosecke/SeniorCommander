package com.mitchellbosecke.seniorcommander;

import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class RollIT extends AbstractIT {

    @Test
    public void rollD100() {
        send("user: !roll 100");
        recv(Pattern.compile("You rolled a \\d{1,3}"));
    }
}

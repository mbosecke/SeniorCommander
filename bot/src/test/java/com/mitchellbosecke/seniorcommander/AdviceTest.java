package com.mitchellbosecke.seniorcommander;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class AdviceTest extends AbstractTest {

    private Logger logger = LoggerFactory.getLogger(AdviceTest.class);

    @Test
    public void advice() {
        send("user: !advice");
        recv(Pattern.compile(".*"));
    }
}

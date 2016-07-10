package com.mitchellbosecke.seniorcommander;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class AdviceTest extends AbstractTest {

    private Logger logger = LoggerFactory.getLogger(AdviceTest.class);

    @Test
    public void advice() {
        assertTrue(testCommandAndResult("!advice", Pattern.compile(".*")));
    }
}

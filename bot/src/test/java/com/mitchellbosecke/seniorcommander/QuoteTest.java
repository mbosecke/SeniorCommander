package com.mitchellbosecke.seniorcommander;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class QuoteTest extends AbstractTest {

    private Logger logger = LoggerFactory.getLogger(QuoteTest.class);

    @Test
    public void addQuote() {
        testCommandAndResult("!quote add Mitchell \"hello world\"", "Quote #1 has been added");
        testCommandAndResult("!quote", "hello world");
    }
}

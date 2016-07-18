package com.mitchellbosecke.seniorcommander;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class QuoteTest extends AbstractTest {

    private Logger logger = LoggerFactory.getLogger(QuoteTest.class);

    @Test
    public void addQuote() {
        Pattern pattern = Pattern.compile("Quote #([0-9]{1,2}) has been added");
        String reply = testCommandAndResult("!quote add Mitchell \"hello world\"", pattern);
        Matcher matcher = pattern.matcher(reply);
        matcher.matches();
        String number = matcher.group(1);
        testCommandAndResult("!quote " + number, "\"hello world\" -Mitchell");
    }
}

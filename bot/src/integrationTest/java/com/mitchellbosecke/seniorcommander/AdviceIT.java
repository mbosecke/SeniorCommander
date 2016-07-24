package com.mitchellbosecke.seniorcommander;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class AdviceIT extends AbstractIT {

    private Logger logger = LoggerFactory.getLogger(AdviceIT.class);

    @Test
    public void advice() {
        send("user: !advice");
        recv(Pattern.compile(".*"));
    }
}

package com.mitchellbosecke.seniorcommander;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserIT extends AbstractIT {

    private Logger logger = LoggerFactory.getLogger(UserIT.class);

    @Test
    public void createUserWhenTheyChat() {
        send("moderator: !points santa");
        recv("username not found");
        send("santa: hey everyone");
        send("moderator: !points santa");
        recv("santa has 0 points.");
    }
}

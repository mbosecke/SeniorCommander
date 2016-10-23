package com.mitchellbosecke.seniorcommander;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PointsIT extends AbstractIT {

    private Logger logger = LoggerFactory.getLogger(PointsIT.class);


    @Test
    public void pointsOfOtherUser() {
        send("moderator: !points user");
        recv("user has 0 points.");
    }

    @Test
    public void pointsOfCurrentUser() {
        send("moderator: !points");
        recv("you have 0 points.");
    }

}

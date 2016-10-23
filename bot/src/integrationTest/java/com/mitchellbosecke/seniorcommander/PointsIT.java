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

    @Test
    public void givePoints() {
        send("moderator: !points give user 100");
        recv("user now has 100 points");
        send("moderator: !points user");
        recv("user has 100 points.");
    }

    @Test
    public void givePointsReversedArguments() {
        send("moderator: !points give 100 user");
        recv("user now has 100 points");
        send("moderator: !points user");
        recv("user has 100 points.");
    }

    @Test
    public void takePoints() {
        send("moderator: !points give user 100");
        recv("user now has 100 points");
        send("moderator: !points take user 10");
        recv("user now has 90 points");
    }

    @Test
    public void takePointsReversedArguments() {
        send("moderator: !points give user 100");
        recv("user now has 100 points");
        send("moderator: !points take 10 user");
        recv("user now has 90 points");
    }

    @Test
    public void takeMoreThanUserHas() {
        send("moderator: !points give user 100");
        recv("user now has 100 points");
        send("moderator: !points take user 999");
        recv("user now has 0 points");
    }

}

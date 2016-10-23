package com.mitchellbosecke.seniorcommander;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GiveawayIT extends AbstractIT {

    private Logger logger = LoggerFactory.getLogger(GiveawayIT.class);


    @Test
    public void openGiveaway() {
        openGiveaway("keyword");
    }

    @Test
    public void cancel() {
        openGiveaway("foo");
        send("moderator: !giveaway cancel");
        recv("The giveaway has been cancelled.");
    }

    @Test
    public void close() {
        openGiveaway("foo");
        send("moderator: !giveaway close");
        recv("The giveaway has been closed.");
    }

    @Test
    public void oneEntryAndWinner() {
        openGiveaway("foo");
        send("user: foo");
        send("moderator: !giveaway draw");
        recv("The giveaway is closed and a winner has been chosen. The winner is user.");
    }

    @Test
    public void cantEnterAfterClosed() {
        openGiveaway("foo");
        send("moderator: !giveaway close");
        recv("The giveaway has been closed.");
        send("user: foo");
        send("moderator: !giveaway draw");
        recv("The giveaway is over; there were no entries.");
    }

    private void openGiveaway(String keyword){

        send("moderator: !giveaway open keyword=" + keyword);
        recv("A giveaway has begun!");
    }
}

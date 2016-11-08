package com.mitchellbosecke.seniorcommander;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuctionIT extends AbstractIT {

    private Logger logger = LoggerFactory.getLogger(AuctionIT.class);


    @Test
    public void openAuction() {
        openAuction("prize");
    }

    @Test
    public void cancel() {
        openAuction("foo");
        send("moderator: !auction cancel");
        recv("The auction has been cancelled.");
    }

    @Test
    public void close() {
        openAuction("foo");
        send("moderator: !auction close");
        recv("The auction is closed; there were no bids.");
    }

    @Test
    public void oneEntryAndWinner() {
        openAuction("prize");
        send("user: !bid 100");
        send("moderator: !auction close");
        recv("The auction is closed and we have a winner. The winner is user.");
    }

    private void openAuction(String prize){

        send("moderator: !auction open " + prize);
        recv("An auction has begun!");
    }
}

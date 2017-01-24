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
    public void infoWithoutBid() {
        openAuction("prize");
        send("user: !auction");
        recv("The current auction is for \"prize\" but no bids have been made yet");
    }

    @Test
    public void oneBidWinner() {
        send("moderator: !points give user 100");
        recv("user now has 100 points");
        openAuction("prize");
        send("user: !bid 50");
        recv("You are the new highest bidder with a bid of 50 points.");
        send("user: !auction");
        recv("The current auction is for \"prize\" and the highest bidder is currently user with a bid of 50 points.");
        send("moderator: !auction close");
        recv("The auction is closed and we have a winner. The winner is user.");
    }

    @Test
    public void competingBid() {
        send("user1: creating user account.");
        send("user2: creating user account.");

        send("moderator: !points give user1 100");
        recv("user1 now has 100 points");
        send("moderator: !points give user2 100");
        recv("user2 now has 100 points");

        openAuction("prize");

        send("user1: !bid 50");
        recv("You are the new highest bidder with a bid of 50 points.");
        send("user1: !auction");
        recv("The current auction is for \"prize\" and the highest bidder is currently user1 with a bid of 50 points.");

        send("user2: !bid 51");
        recv("You are the new highest bidder with a bid of 51 points.");
        send("user2: !auction");
        recv("The current auction is for \"prize\" and the highest bidder is currently user2 with a bid of 51 points.");

        send("moderator: !auction close");
        recv("The auction is closed and we have a winner. The winner is user2.");
    }

    @Test
    public void notEnoughPoints() {

        send("moderator: !points give user 100");
        recv("user now has 100 points");
        openAuction("prize");
        send("user: !bid 500");
        recv("You don't have enough points. RIP.");
    }

    @Test
    public void cancel() {
        openAuction("foo");
        send("moderator: !auction cancel");
        recv("The auction has been cancelled.");
    }

    @Test
    public void cancelWithInfoAfterwards() {
        openAuction("foo");
        send("moderator: !auction cancel");
        recv("The auction has been cancelled.");
        send("user: !auction");
        recv("There is no ongoing auction.");
        expectNoBotOutput();
    }

    @Test
    public void closeWithoutWinner() {
        openAuction("foo");
        send("moderator: !auction close");
        recv("The auction is closed; there were no bids.");
    }

    private void openAuction(String prize){

        send("moderator: !auction open " + prize);
        recv("An auction has begun!");
    }
}

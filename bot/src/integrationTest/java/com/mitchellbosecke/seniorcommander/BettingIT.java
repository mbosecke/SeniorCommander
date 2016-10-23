package com.mitchellbosecke.seniorcommander;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BettingIT extends AbstractIT {

    private Logger logger = LoggerFactory.getLogger(BettingIT.class);


    @Test
    public void openBet() {
        openBet("option1", "option2");
    }

    @Test
    public void cancelBet() {
        openBet("option1", "option2");
        send("moderator: !bet cancel");
        recv("The bet has been cancelled and all placed bets have been returned.");
    }

    @Test
    public void closeBet() {
        openBet("option1", "option2");
        send("moderator: !bet close");
        recv("The bet has been closed.");
    }

    @Test
    public void betWinner(){
        send("moderator: !points give user 100");
        recv("user now has 100 points");
        openBet("option1", "option2");
        send("user: !bet 100 option1");
        recv("Your bet has been placed.");
        send("moderator: !bet winner option1");
        recv("The bet has been closed and payouts have been rewarded. The winners are: user");
    }

    @Test
    public void betNoWinner(){
        send("moderator: !points give user 100");
        recv("user now has 100 points");
        openBet("option1", "option2");
        send("user: !bet 100 option1");
        recv("Your bet has been placed.");
        send("moderator: !bet winner option2");
        recv("The bet has been closed; there were no winners.");
    }

    @Test
    public void notEnoughPoints() {
        send("moderator: !bet open option1 option2");
        recv("A bet has begun!");
        send("user: !bet 999 option1");
        recv("You don't have enough points. RIP.");
    }

    private void openBet(String... options){
        StringBuilder command = new StringBuilder("moderator: !bet open");
        for(String option : options){
            command.append(" ").append(option);
        }
        send(command.toString());
        recv("A bet has begun!");
    }

}

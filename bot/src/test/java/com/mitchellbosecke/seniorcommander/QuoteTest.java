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
    public void newQuote() {
        addQuote("Mitchell", "hello world");
    }

    @Test
    public void nonModeratorAddQuote(){
        send("user: !quote add Mitchell \"hello world\"");
        expectNoBotOutput();
    }

    @Test
    public void requestQuoteById() {
        long id = addQuote("Mitchell", "hello world");
        send("user: !quote " + id);
        recv("\"hello world\" -Mitchell");
    }

    @Test
    public void deleteQuote() {
        long id = addQuote("Mitchell", "hello world");
        send("moderator: !quote delete " + id);
        recv(String.format("Quote #%d has been deleted", id));
        send("user: !quote " + id);
        recv("Quote does not exist");
    }

    @Test
    public void editQuote(){
        long id  = addQuote("Mitchell", "hello world");
        send(String.format("moderator: !quote edit %d \"%s\"", id, "goodbye world"));
        recv(String.format("Quote #%d has been edited", id));
        send("user: !quote " + id);
        recv("\"goodbye world\" -Mitchell");
    }

    /**
     * Adds a quote and returns the ID of it
     * @return
     */
    private long addQuote(String author, String quote){
        Pattern pattern = Pattern.compile("Quote #([0-9]{1,2}) has been added");
        send(String.format("moderator: !quote add %s \"%s\"", author, quote));
        String reply = recv(pattern);
        Matcher matcher = pattern.matcher(reply);
        matcher.matches();
        return Long.valueOf(matcher.group(1));
    }
}

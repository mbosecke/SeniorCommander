package com.mitchellbosecke.seniorcommander;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class QuoteIT extends AbstractIT {

    private Logger logger = LoggerFactory.getLogger(QuoteIT.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM d, yyyy");

    @Test
    public void newQuote() {
        addQuote("Mitchell", "hello world");
    }

    @Test
    public void nonModeratorAddQuote() {
        send("user: !quote add Mitchell \"hello world\"");
        expectNoBotOutput();
    }

    @Test
    public void requestQuoteById() {
        long id = addQuote("Mitchell", "hello world");
        send("user: !quote " + id);
        recv(String.format("\"hello world\" -Mitchell on %s", DATE_FORMAT.format(new Date())));
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
    public void editQuote() {
        long id = addQuote("Mitchell", "hello world");
        send(String.format("moderator: !quote edit %d \"%s\"", id, "goodbye world"));
        recv(String.format("Quote #%d has been edited", id));
        send("user: !quote " + id);
        recv(String.format("\"goodbye world\" -Mitchell on %s", DATE_FORMAT.format(new Date())));
    }

    @Test
    public void randomQuote() {
        addQuote("Mitchell", "hello world");
        send("user: !quote");
        recv(String.format("\"hello world\" -Mitchell on %s", DATE_FORMAT.format(new Date())));
    }

    @Test
    public void randomAuthorQuote() {
        addQuote("Mitchell", "hello world");
        addQuote("Chris", "my name is Chris");
        send("user: !quote mitchell");
        recv(String.format("\"hello world\" -Mitchell on %s", DATE_FORMAT.format(new Date())));
    }

    /**
     * Adds a quote and returns the ID of it
     *
     * @return
     */
    private long addQuote(String author, String quote) {
        Pattern pattern = Pattern.compile("Quote #([0-9]{1,2}) has been added");
        send(String.format("moderator: !quote add %s \"%s\"", author, quote));
        String reply = recv(pattern);
        Matcher matcher = pattern.matcher(reply);
        matcher.matches();
        return Long.valueOf(matcher.group(1));
    }
}

package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.utils.CommandParser;
import com.mitchellbosecke.seniorcommander.utils.ParsedCommand;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by mitch_000 on 2016-07-17.
 */
public class CommandParserTest {

    @Test
    public void completeParseTest(){
        CommandParser parser = new CommandParser();
        String command = "!command add !hello \"yo dude\" userlevel=moderator cooldown=0 alias=!yo";
        ParsedCommand parsedCommand = parser.parse(command);
        Assert.assertEquals("!command", parsedCommand.getTrigger());
        Assert.assertEquals("yo dude", parsedCommand.getQuotedText());
        Assert.assertEquals(3, parsedCommand.getOptions().size());
        Assert.assertEquals(2, parsedCommand.getComponents().size());
    }

    @Test
    public void onlyTrigger(){
        CommandParser parser = new CommandParser();
        String command = "!command";
        ParsedCommand parsedCommand = parser.parse(command);
        Assert.assertEquals("!command", parsedCommand.getTrigger());
        Assert.assertEquals(null, parsedCommand.getQuotedText());
        Assert.assertEquals(0, parsedCommand.getOptions().size());
        Assert.assertEquals(0, parsedCommand.getComponents().size());
    }

    @Test
    public void oneWordQuote(){
        CommandParser parser = new CommandParser();
        String command = "!command \"yo\"";
        ParsedCommand parsedCommand = parser.parse(command);
        Assert.assertEquals("!command", parsedCommand.getTrigger());
        Assert.assertEquals("yo", parsedCommand.getQuotedText());
    }


    @Test
    public void optionAliases(){
        CommandParser parser = new CommandParser();
        String command = "!command alias=foo a=bar";
        ParsedCommand parsedCommand = parser.parse(command);
        Assert.assertEquals("foo", parsedCommand.getOption("alias", "a"));
    }

    @Test
    public void optionMissingAlias(){
        CommandParser parser = new CommandParser();
        String command = "!command a=bar";
        ParsedCommand parsedCommand = parser.parse(command);
        Assert.assertEquals("bar", parsedCommand.getOption("alias", "a"));
    }
}

package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.domain.Community;
import com.mitchellbosecke.seniorcommander.domain.Quote;
import com.mitchellbosecke.seniorcommander.extension.core.service.QuoteService;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.utils.CommandParser;
import com.mitchellbosecke.seniorcommander.utils.ParsedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * !quote
 * !quote 123
 * !quote Justin
 * </p>
 * Created by mitch_000 on 2016-07-04.
 */
public class RandomQuote implements CommandHandler {

    private Logger logger = LoggerFactory.getLogger(RandomQuote.class);

    private final QuoteService quoteService;

    private final MessageQueue messageQueue;

    public RandomQuote(MessageQueue messageQueue, QuoteService quoteService) {
        this.messageQueue = messageQueue;
        this.quoteService = quoteService;
    }

    @Override
    public void execute(Message message) {

        ParsedCommand parsed = new CommandParser().parse(message.getContent());
        Community community = quoteService.findCommunity(message.getChannel());

        if (parsed.getComponents().isEmpty()) {
            shoutQuote(message, quoteService.findRandomQuote(community));
        } else {
            String identifier = parsed.getComponents().get(0);

            try {
                long id = Long.parseLong(identifier);
                Quote quote = quoteService.findQuote(community, id);

                shoutQuote(message, quote);

            } catch (NumberFormatException ex) {
                String author = identifier;
                shoutQuote(message, quoteService.findRandomQuote(community, author));
            }
        }
    }

    private void shoutQuote(Message message, Quote quote) {
        if (quote == null) {
            messageQueue.add(Message.response(message, "Quote does not exist"));
        } else {
            messageQueue.add(Message
                    .shout(String.format("\"%s\" -%s", quote.getContent(), quote.getAuthor()), message.getChannel()));
        }
    }

}

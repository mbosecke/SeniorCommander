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
 * !quote add Justin “yo my name is big dawg”
 * !quote delete 123
 * !quote edit 123 “yo my name is small dawg”
 * </p>
 * Created by mitch_000 on 2016-07-04.
 */
public class QuoteCrudCommand implements CommandHandler {

    private Logger logger = LoggerFactory.getLogger(QuoteCrudCommand.class);

    private final QuoteService quoteService;

    private final MessageQueue messageQueue;

    public QuoteCrudCommand(MessageQueue messageQueue, QuoteService quoteService) {
        this.messageQueue = messageQueue;
        this.quoteService = quoteService;
    }

    @Override
    public void execute(Message message) {

        ParsedCommand parsed = new CommandParser().parse(message.getContent());
        Community community = quoteService.findCommunity(message.getChannel());

        if (parsed.getComponents().isEmpty()) {
            // TODO: get random quote
        } else {
            String subCommand = parsed.getComponents().get(0);

            if ("add".equalsIgnoreCase(subCommand)) {

                if (parsed.getQuotedText() == null) {
                    messageQueue.add(Message.response(message, "You are missing the quoted text"));
                } else {
                    Quote result = quoteService
                            .addQuote(community, parsed.getComponents().get(1), parsed.getQuotedText());
                    messageQueue
                            .add(Message.response(message, String.format("Quote #%d has been added", result.getId())));
                }
            } else {
                try {
                    long id = Long.parseLong(subCommand);
                    Quote quote = quoteService.find(Quote.class, id);
                    messageQueue.add(Message
                            .shout(String.format("\"%s\" -%s", quote.getContent(), quote.getAuthor()), message
                                    .getChannel()));
                } catch (NumberFormatException ex) {
                    // must be a name...
                }
            }
        }

    }

}

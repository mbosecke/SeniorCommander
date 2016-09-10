package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.QuoteModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.QuoteService;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.utils.CommandParser;
import com.mitchellbosecke.seniorcommander.utils.ParsedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM d, yyyy");

    public RandomQuote(MessageQueue messageQueue, QuoteService quoteService) {
        this.messageQueue = messageQueue;
        this.quoteService = quoteService;
    }

    @Override
    public void execute(Message message) {

        ParsedCommand parsed = new CommandParser().parse(message.getContent());
        CommunityModel communityModel = quoteService.findCommunity(message.getChannel());

        if (parsed.getComponents().isEmpty()) {
            shoutQuote(message, quoteService.findRandomQuote(communityModel));
        } else {
            String identifier = parsed.getComponents().get(0);

            try {
                long id = Long.parseLong(identifier);
                QuoteModel quoteModel = quoteService.findQuote(communityModel, id);

                shoutQuote(message, quoteModel);

            } catch (NumberFormatException ex) {
                String author = identifier;
                shoutQuote(message, quoteService.findRandomQuote(communityModel, author));
            }
        }
    }

    private void shoutQuote(Message message, QuoteModel quoteModel) {
        if (quoteModel == null) {
            messageQueue.add(Message.response(message, "Quote does not exist"));
        } else {
            messageQueue.add(Message.shout(message.getChannel(), String
                    .format("\"%s\" -%s on %s", quoteModel.getContent(), quoteModel.getAuthor(), DATE_FORMAT
                            .format(quoteModel.getCreatedDate()))));
        }
    }

}

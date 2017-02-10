package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.QuoteModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.QuoteService;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.utils.CommandParser;
import com.mitchellbosecke.seniorcommander.utils.ParsedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

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

    private final SeniorCommander seniorCommander;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.CANADA);

    public RandomQuote(SeniorCommander seniorCommander, QuoteService quoteService) {
        this.seniorCommander = seniorCommander;
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
        MessageQueue messageQueue = seniorCommander.getMessageQueue();
        if (quoteModel == null) {
            messageQueue.add(Message.response(message, "Quote does not exist"));
        } else {
            messageQueue.add(Message.shout(message.getChannel(), String
                    .format("\"%s\" -%s on %s", quoteModel.getContent(), quoteModel.getAuthor(), DATE_FORMAT
                            .format(quoteModel.getCreatedDate()))));
        }
    }

}

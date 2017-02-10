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

/**
 * <p>
 * !quote add Justin “yo my name is big dawg”
 * !quote delete 123
 * !quote edit 123 “yo my name is small dawg”
 * </p>
 * Created by mitch_000 on 2016-07-04.
 */
public class QuoteCrud implements CommandHandler {

    private Logger logger = LoggerFactory.getLogger(QuoteCrud.class);

    private final QuoteService quoteService;

    private final SeniorCommander seniorCommander;

    public QuoteCrud(SeniorCommander seniorCommander, QuoteService quoteService) {
        this.seniorCommander = seniorCommander;
        this.quoteService = quoteService;
    }

    @Override
    public void execute(Message message) {

        MessageQueue messageQueue = seniorCommander.getMessageQueue();
        ParsedCommand parsed = new CommandParser().parse(message.getContent());
        CommunityModel communityModel = quoteService.findCommunity(message.getChannel());

        String subCommand = parsed.getComponents().get(0);

        if ("add".equalsIgnoreCase(subCommand)) {

            if (parsed.getQuotedText() == null) {
                messageQueue.add(Message.response(message, "You are missing the quoted text"));
            } else {
                QuoteModel result = quoteService
                        .addQuote(communityModel, parsed.getComponents().get(1), parsed.getQuotedText());
                messageQueue.add(Message
                        .response(message, String.format("Quote #%d has been added", result.getCommunitySequence())));
            }
        } else if ("edit".equalsIgnoreCase(subCommand)) {
            long id = Long.parseLong(parsed.getComponents().get(1));
            QuoteModel quoteModel = quoteService.findQuote(communityModel, id);
            quoteModel.setContent(parsed.getQuotedText());
            messageQueue.add(Message.response(message, String.format("Quote #%d has been edited", id)));
        } else if ("delete".equalsIgnoreCase(subCommand)) {
            long id = Long.parseLong(parsed.getComponents().get(1));
            QuoteModel quoteModel = quoteService.findQuote(communityModel, id);
            quoteService.delete(quoteModel);
            messageQueue.add(Message.response(message, String.format("Quote #%d has been deleted", id)));
        }
    }

}

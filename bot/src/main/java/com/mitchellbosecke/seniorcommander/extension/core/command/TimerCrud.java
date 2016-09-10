package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;
import com.mitchellbosecke.seniorcommander.timer.TimerManager;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.TimerService;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.utils.CommandParser;
import com.mitchellbosecke.seniorcommander.utils.ParsedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * !timer add “yo my name is big dawg” interval=10 chat=10
 * !timer add alias=!yo interval=10 chat=10
 * !timer delete 123
 * !timer edit 123 “yo my name is small dawg”
 * !timer enable 123
 * !timer disable 123
 * !timer list
 * </p>
 * Created by mitch_000 on 2016-07-04.
 */
public class TimerCrud implements CommandHandler {

    private Logger logger = LoggerFactory.getLogger(TimerCrud.class);

    private final TimerService timerService;

    private final MessageQueue messageQueue;

    private final TimerManager timerManager;

    private String[] intervalOption = {"interval", "in"};
    private String[] chatLinesOption = {"chat-lines", "cl"};

    public TimerCrud(MessageQueue messageQueue, TimerService timerService, TimerManager timerManager) {
        this.messageQueue = messageQueue;
        this.timerService = timerService;
        this.timerManager = timerManager;
    }

    @Override
    public void execute(Message message) {

        ParsedCommand parsed = new CommandParser().parse(message.getContent());
        CommunityModel communityModel = timerService.findCommunity(message.getChannel());

        String subCommand = parsed.getComponents().get(0);

        if ("add".equalsIgnoreCase(subCommand)) {

            if (parsed.getQuotedText() == null) {
                messageQueue.add(Message.response(message, "You are missing the quoted text to be used as output"));
            } else {
                TimerModel timerModel = timerService.addTimer(communityModel, parsed
                        .getQuotedText(), getInterval(parsed), getChatLines(parsed));
                //timerManager.startTimer(timerModel);
                messageQueue.add(Message.response(message, String.format("Timer #%d has been added", timerModel
                        .getCommunitySequence())));
            }
        } else if ("delete".equalsIgnoreCase(subCommand)) {
            long id = Long.parseLong(parsed.getComponents().get(1));
            TimerModel timerModel = timerService.findTimer(communityModel, id);
            timerManager.stopTimer(timerModel.getId());
            timerService.delete(timerModel);
            messageQueue.add(Message.response(message, String.format("Timer #%dhas been deleted: ", id)));

        } else if ("enable".equalsIgnoreCase(subCommand)) {
            long id = Long.parseLong(parsed.getComponents().get(1));
            TimerModel timerModel = timerService.findTimer(communityModel, id);
            timerModel.setEnabled(true);
            //timerManager.startTimer(timerModel);
            messageQueue.add(Message.response(message, String.format("Timer #%d has been enabled", id)));
        } else if ("disable".equalsIgnoreCase(subCommand)) {
            long id = Long.parseLong(parsed.getComponents().get(1));
            TimerModel timerModel = timerService.findTimer(communityModel, id);
            timerModel.setEnabled(false);
            timerManager.stopTimer(timerModel.getId());
            messageQueue.add(Message.response(message, String.format("Timer #%d has been disabled", id)));
        }

    }

    private long getInterval(ParsedCommand parsed) {
        String cooldownText = parsed.getOption(intervalOption);
        long cooldown = cooldownText == null ? 0 : Long.valueOf(cooldownText);

        // convert minutes to seconds
        return cooldown * 60;
    }

    private long getChatLines(ParsedCommand parsed) {
        String chatLinesText = parsed.getOption(chatLinesOption);
        return chatLinesText == null ? 0 : Long.valueOf(chatLinesText);
    }

}

package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.AccessLevel;
import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.TimerService;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import com.mitchellbosecke.seniorcommander.extension.core.timer.ShoutTimer;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.timer.TimerManager;
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

    private final UserService userService;

    private String[] intervalOption = {"interval", "in"};
    private String[] chatLinesOption = {"chat-lines", "cl"};

    public TimerCrud(MessageQueue messageQueue, TimerService timerService, TimerManager timerManager,
                     UserService userService) {
        this.messageQueue = messageQueue;
        this.timerService = timerService;
        this.timerManager = timerManager;
        this.userService = userService;
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
                TimerModel timerModel = timerService.addTimer(message.getChannel(), parsed
                        .getQuotedText(), getInterval(message, parsed), getChatLines(parsed));
                ShoutTimer shoutTimer = new ShoutTimer(timerModel.getId(), timerModel.getInterval(), message
                        .getChannel(), messageQueue, timerModel.getMessage());
                timerManager.addTimer(shoutTimer);
                messageQueue.add(Message.response(message, String
                        .format("Timer #%d has been added", timerModel.getCommunitySequence())));
            }
        } else if ("delete".equalsIgnoreCase(subCommand)) {
            long id = Long.parseLong(parsed.getComponents().get(1));
            TimerModel timerModel = timerService.findTimer(communityModel, id);
            timerManager.disableTimer(timerModel.getId());
            timerService.delete(timerModel);
            messageQueue.add(Message.response(message, String.format("Timer #%dhas been deleted: ", id)));

        } else if ("enable".equalsIgnoreCase(subCommand)) {
            long id = Long.parseLong(parsed.getComponents().get(1));
            TimerModel timerModel = timerService.findTimer(communityModel, id);
            timerModel.setEnabled(true);
            timerManager.enableTimer(timerModel.getId());
            messageQueue.add(Message.response(message, String.format("Timer #%d has been enabled", id)));
        } else if ("disable".equalsIgnoreCase(subCommand)) {
            long id = Long.parseLong(parsed.getComponents().get(1));
            TimerModel timerModel = timerService.findTimer(communityModel, id);
            timerModel.setEnabled(false);
            timerManager.disableTimer(timerModel.getId());
            messageQueue.add(Message.response(message, String.format("Timer #%d has been disabled", id)));
        }

    }

    private long getInterval(Message message, ParsedCommand parsed) {
        String cooldownText = parsed.getOption(intervalOption);
        long cooldown = 0;
        if (cooldownText.endsWith("s")) {

            cooldownText = cooldownText.substring(0, cooldownText.length() - 1);

            if (userService.findOrCreateUser(message.getChannel(), message.getSender()).getAccessLevel()
                    .hasAccess(AccessLevel.ADMIN)) {
                cooldown = Long.valueOf(cooldownText);
            } else {
                // convert minutes to seconds
                cooldown = Long.valueOf(cooldownText) * 60;
            }
        } else {
            // convert minutes to seconds
            cooldown = Long.valueOf(cooldownText) * 60;
        }

        return cooldown;

    }

    private long getChatLines(ParsedCommand parsed) {
        String chatLinesText = parsed.getOption(chatLinesOption);
        return chatLinesText == null ? 0 : Long.valueOf(chatLinesText);
    }

}

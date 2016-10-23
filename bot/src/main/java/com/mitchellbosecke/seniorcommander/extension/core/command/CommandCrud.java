package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.AccessLevel;
import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.domain.CommandModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.CommandService;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.utils.CommandParser;
import com.mitchellbosecke.seniorcommander.utils.ParsedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * !command add !hello “yo my name is dave” userlevel=moderator cooldown=0 alias=!yo
 * !command add !hello “yo my name is dave” ul=moderator cd=0 al=!yo
 * !command edit !hello “yo my name is actually jim”
 * !command delete !hello
 * !command enable !hello
 * !command disable !hello
 * </p>
 * Created by mitch_000 on 2016-07-04.
 */
public class CommandCrud implements CommandHandler {

    private Logger logger = LoggerFactory.getLogger(CommandCrud.class);

    private final CommandService commandService;

    private final UserService userService;

    private final MessageQueue messageQueue;

    private String[] cooldownOption = {"cooldown", "cd"};
    private String[] accessLevel = {"access", "ac"};

    public CommandCrud(MessageQueue messageQueue, CommandService commandService, UserService userService) {
        this.messageQueue = messageQueue;
        this.commandService = commandService;
        this.userService = userService;
    }

    @Override
    public void execute(Message message) {

        ParsedCommand parsed = new CommandParser().parse(message.getContent());
        CommunityModel communityModel = commandService.findCommunity(message.getChannel());

        String subCommand = parsed.getComponents().get(0);
        String commandName = parsed.getComponents().get(1);

        if ("add".equalsIgnoreCase(subCommand)) {

            CommandModel existingCommandModel = commandService.findCommand(communityModel, commandName);
            if (existingCommandModel == null) {

                if (parsed.getQuotedText() == null) {
                    messageQueue.add(Message.response(message, "You are missing the quoted text to be used as output"));
                } else {
                    commandService.addCommand(communityModel, commandName, parsed
                            .getQuotedText(), getCooldown(message, parsed), getAccessLevel(parsed));
                    messageQueue.add(Message.response(message, "Command has been added: " + commandName));
                }
            } else {
                messageQueue.add(Message.response(message, "Command already exists."));
            }
        } else if ("delete".equalsIgnoreCase(subCommand)) {
            commandService.deleteCommand(communityModel, commandName);
            messageQueue.add(Message.response(message, "Command has been deleted: " + commandName));
        } else if ("edit".equalsIgnoreCase(subCommand)) {

            CommandModel commandModel = commandService.findCommand(communityModel, commandName);
            if (parsed.getQuotedText() != null) {
                commandModel.setMessage(parsed.getQuotedText());
            }

            if (parsed.getOption(cooldownOption) != null) {
                commandModel.setCooldown(getCooldown(message, parsed));
            }

            if (parsed.getOption(accessLevel) != null) {
                commandModel.setAccessLevel(getAccessLevel(parsed));
            }
        } else if ("enable".equalsIgnoreCase(subCommand)) {
            CommandModel commandModel = commandService.findCommand(communityModel, commandName);
            commandModel.setEnabled(true);
            messageQueue.add(Message.response(message, "Command has been enabled: " + commandName));
        } else if ("disable".equalsIgnoreCase(subCommand)) {
            CommandModel commandModel = commandService.findCommand(communityModel, commandName);
            commandModel.setEnabled(false);
            messageQueue.add(Message.response(message, "Command has been disabled: " + commandName));
        }

    }

    private long getCooldown(Message message, ParsedCommand parsed) {
        String cooldownText = parsed.getOption(cooldownOption);
        long cooldown = 0;
        if (cooldownText.endsWith("s")) {

            cooldownText = cooldownText.substring(0, cooldownText.length() - 1);

            if (userService.findOrCreateUser(message.getChannel(), message.getSender()).getAccessLevel()
                    .hasAccess(AccessLevel.ADMIN)) {
                cooldown = Long.valueOf(cooldownText);
            } else {
                // convert minutes to seconds
                cooldown = (cooldownText == null? 0 : Long.valueOf(cooldownText)) * 60;
            }
        }else {
            cooldown = (cooldownText == null ? 0 : Long.valueOf(cooldownText)) * 60;
        }
        return cooldown;
    }

    private AccessLevel getAccessLevel(ParsedCommand parsed) {
        String access = parsed.getOption(accessLevel);
        if (access != null) {
            return AccessLevel.valueOf(access.toUpperCase());
        } else {
            return AccessLevel.USER;
        }
    }

}

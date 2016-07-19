package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.AccessLevel;
import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.domain.Command;
import com.mitchellbosecke.seniorcommander.domain.Community;
import com.mitchellbosecke.seniorcommander.extension.core.service.CommandService;
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
public class CommandCrudCommand implements CommandHandler {

    private Logger logger = LoggerFactory.getLogger(CommandCrudCommand.class);

    private final CommandService commandService;

    private final MessageQueue messageQueue;

    private String[] cooldownOption = {"cooldown", "cd"};
    private String[] accessLevel = {"access", "ac"};

    public CommandCrudCommand(MessageQueue messageQueue, CommandService commandService) {
        this.messageQueue = messageQueue;
        this.commandService = commandService;
    }

    @Override
    public void execute(Message message) {

        ParsedCommand parsed = new CommandParser().parse(message.getContent());
        Community community = commandService.findCommunity(message.getChannel());

        String subCommand = parsed.getComponents().get(0);
        String commandName = parsed.getComponents().get(1);

        if ("add".equalsIgnoreCase(subCommand)) {

            Command existingCommand = commandService.findCommand(community, commandName);
            if (existingCommand == null) {

                if (parsed.getQuotedText() == null) {
                    messageQueue.add(Message.response(message, "You are missing the quoted text to be used as output"));
                } else {
                    commandService.addCommand(community, commandName, parsed
                            .getQuotedText(), getCooldown(parsed), getAccessLevel(parsed));
                    messageQueue.add(Message.response(message, "Command has been added: " + commandName));
                }
            } else {
                messageQueue.add(Message.response(message, "Command already exists."));
            }
        } else if ("delete".equalsIgnoreCase(subCommand)) {
            commandService.deleteCommand(community, commandName);
            messageQueue.add(Message.response(message, "Command has been deleted: " + commandName));
        } else if ("edit".equalsIgnoreCase(subCommand)) {

            Command command = commandService.findCommand(community, commandName);
            if (parsed.getQuotedText() != null) {
                command.setMessage(parsed.getQuotedText());
            }

            if (parsed.getOption(cooldownOption) != null) {
                command.setCooldown(getCooldown(parsed));
            }

            if (parsed.getOption(accessLevel) != null) {
                command.setAccessLevel(getAccessLevel(parsed));
            }
        } else if ("enable".equalsIgnoreCase(subCommand)) {
            Command command = commandService.findCommand(community, commandName);
            command.setEnabled(true);
            messageQueue.add(Message.response(message, "Command has been enabled: " + commandName));
        } else if ("disable".equalsIgnoreCase(subCommand)) {
            Command command = commandService.findCommand(community, commandName);
            command.setEnabled(false);
            messageQueue.add(Message.response(message, "Command has been disabled: " + commandName));
        }

    }

    private long getCooldown(ParsedCommand parsed) {
        String cooldownText = parsed.getOption(cooldownOption);
        long cooldown = cooldownText == null ? 0 : Long.valueOf(cooldownText);

        // convert minutes to seconds
        return cooldown * 60;
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

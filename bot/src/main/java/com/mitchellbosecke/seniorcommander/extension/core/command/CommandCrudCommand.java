package com.mitchellbosecke.seniorcommander.extension.core.command;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.domain.Community;
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
public class CommandCrudCommand implements CommandHandler {

    private Logger logger = LoggerFactory.getLogger(CommandCrudCommand.class);

    private final UserService userService;

    private final CommandService commandService;

    private final MessageQueue messageQueue;

    public CommandCrudCommand(MessageQueue messageQueue, UserService userService, CommandService commandService) {
        this.userService = userService;
        this.messageQueue = messageQueue;
        this.commandService = commandService;
    }

    @Override
    public void execute(Message message) {

        ParsedCommand parsed = new CommandParser().parse(message.getContent());
        Community community = userService.findCommunity(message.getChannel());

        String subCommand = parsed.getComponents().get(0);
        String commandName = parsed.getComponents().get(1);

        if ("add".equalsIgnoreCase(subCommand)) {
            logger.debug("Command has been added: " + commandName);
            messageQueue.add(Message.response(message, "The command has been added."));

            String cooldownText = parsed.getOption("cooldown", "cd");
            long cooldown = cooldownText == null ? 0 : Long.valueOf(cooldownText) * 60;
            commandService.addCommand(community, commandName, parsed.getQuotedText(), cooldown);
        }

    }

}

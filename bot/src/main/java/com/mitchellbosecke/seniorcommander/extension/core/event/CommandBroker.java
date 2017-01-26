package com.mitchellbosecke.seniorcommander.extension.core.event;

import com.mitchellbosecke.seniorcommander.CommandHandler;
import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.domain.CommandLogModel;
import com.mitchellbosecke.seniorcommander.domain.CommandModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.CommunityUserModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.CommandService;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class CommandBroker implements EventHandler {

    private Logger logger = LoggerFactory.getLogger(CommandBroker.class);

    private final UserService userService;

    private final CommandService commandService;

    private final MessageQueue messageQueue;

    private final Map<String, CommandHandler> commandHandlers;

    public CommandBroker(MessageQueue messageQueue, List<CommandHandler> commandHandlers, UserService userService,
                         CommandService commandService) {
        this.userService = userService;
        this.commandService = commandService;
        this.messageQueue = messageQueue;
        this.commandHandlers = commandHandlers.stream()
                .collect(Collectors.toMap(handler -> handler.getClass().getName(), handler -> handler));
    }

    @Override
    public void handle(Message message) {
        if (Message.Type.USER.equals(message.getType())) {
            CommunityModel communityModel = commandService.findCommunity(message.getChannel());
            CommandModel commandModel = commandService.findCommand(communityModel, message.getContent());

            if (commandModel != null && commandModel.isEnabled()) {

                CommunityUserModel user = userService.findOrCreateUser(message.getChannel(), message.getSender());
                if (commandModel.getAlias() != null) {
                    commandModel = commandService.findCommand(communityModel, commandModel.getAlias());
                    if (commandModel == null) {
                        messageQueue.add(Message
                                .response(message, "command is misconfigured; alias references non-existing command."));
                        return;
                    }
                }

                if (hasPermission(commandModel, user)) {
                    executeAfterCooldown(message, commandModel, user);
                }
            }
        }
    }

    private boolean hasPermission(CommandModel commandModel, CommunityUserModel user) {
        if (commandModel.getAccessLevel() == null) {
            return true;
        }
        return user.getAccessLevel().hasAccess(commandModel.getAccessLevel());
    }

    private void executeAfterCooldown(Message message, CommandModel commandModel, CommunityUserModel user) {
        if (commandModel.getCooldown() > 0) {

            CommandLogModel commandLogModel = commandService.findMostRecentCommandLog(commandModel, user);
            if (commandLogModel == null) {
                executeCommand(message, commandModel, user);
            } else {
                long cooldownSeconds = commandModel.getCooldown();
                ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
                ZonedDateTime tooSoon = now.minusSeconds(cooldownSeconds);
                if (commandLogModel.getLogDate().isBefore(tooSoon)) {
                    logger.debug("Command executed [{}]", commandModel.getTrigger());
                    executeCommand(message, commandModel, user);
                }else{
                    Duration duration = Duration.between(tooSoon, ZonedDateTime.now(ZoneId.of("UTC")));
                    logger.debug("Too soon before cooldown period. Wait " + (duration.toMillis() / 1000) + " more seconds");
                }
            }
        } else {
            executeCommand(message, commandModel, user);
        }
    }

    private void executeCommand(Message message, CommandModel commandModel, CommunityUserModel user) {

        if (commandModel.getImplementation() != null) {
            CommandHandler commandHandler = commandHandlers.get(commandModel.getImplementation());
            commandHandler.execute(message);
        } else {
            messageQueue.add(Message.shout(message.getChannel(), commandModel.getMessage()));
        }

        // log this use
        CommandLogModel log = new CommandLogModel();
        log.setCommandModel(commandModel);
        log.setCommunityUserModel(user);
        log.setLogDate(ZonedDateTime.now(ZoneId.of("UTC")));
        userService.persist(log);
    }

}

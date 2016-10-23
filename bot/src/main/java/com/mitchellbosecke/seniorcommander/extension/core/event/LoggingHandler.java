package com.mitchellbosecke.seniorcommander.extension.core.event;

import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;
import com.mitchellbosecke.seniorcommander.domain.ChatLogModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import com.mitchellbosecke.seniorcommander.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class LoggingHandler implements EventHandler {

    private final static Logger logger = LoggerFactory.getLogger(LoggingHandler.class);

    private final UserService userService;

    public LoggingHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(Message message) {
        logger.debug(String.format("[%s] %s: %s", message.getType(), message.getSender(), message.getContent()));

        if(Message.Type.USER == message.getType()) {

            ChatLogModel log = new ChatLogModel();
            log.setMessage(message.getContent());
            log.setChannel(userService.find(ChannelModel.class, message.getChannel().getId()));
            log.setCommunityUserModel(userService.findUser(message.getChannel(), message.getSender()));
            log.setDate(ZonedDateTime.now(ZoneId.of("UTC")));
            userService.persist(log);
        }
    }
}

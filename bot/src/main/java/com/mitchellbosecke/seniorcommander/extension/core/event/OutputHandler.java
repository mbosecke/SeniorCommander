package com.mitchellbosecke.seniorcommander.extension.core.event;

import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;
import com.mitchellbosecke.seniorcommander.domain.ChatLogModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.ChannelService;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import com.mitchellbosecke.seniorcommander.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public class OutputHandler implements EventHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<Channel> channels;

    private final ChannelService channelService;

    private final UserService userService;

    private final String CONFIG_MUTE = "mute";

    public OutputHandler(List<Channel> channels, ChannelService channelService, UserService userService) {
        this.channels = channels;
        this.channelService = channelService;
        this.userService = userService;
    }

    @Override
    public void handle(Message message) {
        if (Message.Type.OUTPUT.equals(message.getType())) {

            logger.debug("Output handler: " + message.getContent());
            List<Channel> outputChannels = new ArrayList<>();
            if (message.getChannel() != null) {
                outputChannels.add(message.getChannel());
            } else {
                outputChannels.addAll(channels);
            }

            for (Channel channel : outputChannels) {
                emit(channel, message);
            }
        }
    }

    private void emit(Channel channel, Message message) {
        ChannelModel channelModel = channelService.find(ChannelModel.class, channel.getId());
        if (Boolean.valueOf(channelModel.getSetting(CONFIG_MUTE))) {
            return;
        }

        log(message);

        String recipient = message.getRecipient();
        String content = message.getContent();

        if (recipient != null) {
            if (message.isWhisper()) {
                channel.sendWhisper(recipient, content);
            } else {
                channel.sendMessage(recipient, content);
            }
        } else {
            channel.sendMessage(content);
        }
    }

    private void log(Message message){
        ChatLogModel log = new ChatLogModel();
        log.setMessage(message.getContent());
        log.setChannel(userService.find(ChannelModel.class, message.getChannel().getId()));
        log.setCommunityUserModel(userService.findOrCreateUser(message.getChannel(), message.getChannel().getBotUsername()));
        log.setDate(ZonedDateTime.now(ZoneId.of("UTC")));
        userService.persist(log);
    }
}

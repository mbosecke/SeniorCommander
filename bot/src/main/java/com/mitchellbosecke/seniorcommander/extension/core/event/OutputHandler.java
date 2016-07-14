package com.mitchellbosecke.seniorcommander.extension.core.event;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public class OutputHandler implements EventHandler {

    public static final String CONFIG_LURK = "seniorcommander.lurk";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<Channel> channels;

    public OutputHandler(List<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public void handle(Message message) {
        if (Message.Type.OUTPUT.equals(message.getType())) {

            List<Channel> outputChannels = new ArrayList<>();
            if (message.getChannel() != null) {
                outputChannels.add(message.getChannel());
            } else {
                outputChannels.addAll(channels);
            }

            for (Channel channel : outputChannels) {
                emit(channel, message.getRecipient(), message.getContent(), message.isWhisper());
            }
        }
    }

    private void emit(Channel channel, String recipient, String content, boolean whisper) {
        if (recipient != null) {
            if (whisper) {
                channel.sendWhisper(recipient, content);
            } else {
                channel.sendMessage(recipient, content);
            }
        } else {
            channel.sendMessage(content);
        }
    }
}

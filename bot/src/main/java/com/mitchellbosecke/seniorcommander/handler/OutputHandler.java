package com.mitchellbosecke.seniorcommander.handler;

import com.mitchellbosecke.seniorcommander.Context;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public class OutputHandler implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handle(Context context, Message message) {
        if (Message.Type.OUTPUT.equals(message.getType())) {
            List<Channel> outputChannels = new ArrayList<>();
            if (message.getChannel() != null) {
                outputChannels.add(message.getChannel());
            } else {
                outputChannels.addAll(context.getChannels());
            }

            for (Channel channel : outputChannels) {
                emit(context, channel, message.getRecipient(), message.getContent(), message.isWhisper());
            }
        }
    }

    private void emit(Context context, Channel channel, String recipient, String content, boolean whisper) {
        if (recipient != null) {
            if (whisper) {
                channel.sendWhisper(context, recipient, content);
            } else {
                channel.sendMessage(context, recipient, content);
            }
        } else {
            channel.sendMessage(context, content);
        }
    }
}

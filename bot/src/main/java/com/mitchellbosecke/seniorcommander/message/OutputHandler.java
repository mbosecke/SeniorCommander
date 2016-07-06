package com.mitchellbosecke.seniorcommander.message;

import com.mitchellbosecke.seniorcommander.channel.Channel;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public class OutputHandler implements MessageHandler {

    private final Channel outputChannel;

    public OutputHandler(Channel outputChannel) {
        this.outputChannel = outputChannel;
    }

    @Override
    public void handle(Message message) {
        if (Message.Type.OUTPUT.equals(message.getType())) {
            outputChannel.sendMessage(message.getContent());
        }
    }
}

package com.mitchellbosecke.seniorcommander.extension.core.task;

import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.task.Task;

/**
 * Created by mitch_000 on 2016-07-31.
 */
public class Shout implements Task {

    private final MessageQueue messageQueue;

    private final String message;

    public Shout(MessageQueue messageQueue, String message) {
        this.messageQueue = messageQueue;
        this.message = message;
    }

    public MessageQueue getMessageQueue() {
        return messageQueue;
    }

    @Override
    public void perform() {
        messageQueue.add(Message.shout(message));
    }
}

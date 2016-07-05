package com.mitchellbosecke.seniorcommander.message;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-03.
 */
public class MessageQueue {

    List<Message> queue = new LinkedList<>();

    public void addMessage(Message message) {
        queue.add(message);
        System.out.println(message.getSender() + ": " + message.getContent());
    }


}

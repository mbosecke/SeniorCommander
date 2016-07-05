package com.mitchellbosecke.seniorcommander.message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by mitch_000 on 2016-07-03.
 */
public class MessageQueue {

    BlockingQueue<Message> queue = new LinkedBlockingDeque<>();

    /**
     * Does not block
     * @param message
     */
    public void addMessage(Message message) {
        queue.add(message);
        System.out.println(message.getSender() + ": " + message.getContent());
    }

    /**
     * Blocks until a message is ready
     * @return
     */
    public Message readMessage(){
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}

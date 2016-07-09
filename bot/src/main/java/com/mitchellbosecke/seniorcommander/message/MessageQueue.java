package com.mitchellbosecke.seniorcommander.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by mitch_000 on 2016-07-03.
 */
public class MessageQueue {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BlockingQueue<Message> queue = new LinkedBlockingDeque<>();

    /**
     * Does not block
     * @param message
     */
    public void add(Message message) {
        queue.add(message);
    }

    /**
     * Blocks for a small period of time until a message is ready
     * @return
     */
    public Message readMessage(){
        try {
            return queue.poll(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}

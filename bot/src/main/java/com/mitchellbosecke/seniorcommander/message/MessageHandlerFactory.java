package com.mitchellbosecke.seniorcommander.message;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface MessageHandlerFactory {

    List<MessageHandler> build(SessionFactory sessionFactory, MessageQueue messageQueue, List<Channel> channels);

}

package com.mitchellbosecke.seniorcommander.handler;

import com.mitchellbosecke.seniorcommander.Configuration;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface MessageHandlerFactory {

    List<MessageHandler> build(SessionFactory sessionFactory, MessageQueue messageQueue, Configuration configuration,
                               List<Channel> channels);

}

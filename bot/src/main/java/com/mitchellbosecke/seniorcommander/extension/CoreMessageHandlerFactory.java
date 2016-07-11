package com.mitchellbosecke.seniorcommander.extension;

import com.mitchellbosecke.seniorcommander.Configuration;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.handler.*;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.repository.Repository;
import com.mitchellbosecke.seniorcommander.repository.RepositoryImpl;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public class CoreMessageHandlerFactory implements MessageHandlerFactory {

    @Override
    public List<MessageHandler> build(SessionFactory sessionFactory, MessageQueue messageQueue,
                                      Configuration configuration, List<Channel> channels) {

        List<MessageHandler> messageHandlers = new ArrayList<>();

        Repository repository = new RepositoryImpl(sessionFactory);

        messageHandlers.add(new LoggingHandler());
        messageHandlers.add(new DiceHandler(messageQueue));
        messageHandlers.add(new OutputHandler(configuration, channels));
        messageHandlers.add(new CommandsHandler(repository, messageQueue));
        messageHandlers.add(new ConversationalHandler(messageQueue));
        messageHandlers.add(new UserStatsHandler(repository));
        messageHandlers.add(new RouletteHandler(messageQueue));
        messageHandlers.add(new AdviceHandler(messageQueue));
        messageHandlers.add(new JoinPartHandler(repository));
        return messageHandlers;
    }

}

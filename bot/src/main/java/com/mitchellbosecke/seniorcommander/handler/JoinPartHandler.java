package com.mitchellbosecke.seniorcommander.handler;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class JoinPartHandler implements MessageHandler {

    private Logger logger = LoggerFactory.getLogger(JoinPartHandler.class);

    private final Repository repository;

    public JoinPartHandler(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(Message message) {

        if (Message.Type.MEMBERSHIP_JOIN.equals(message.getType())) {
            join(message);
        } else if (Message.Type.MEMBERSHIP_PART.equals(message.getType())) {
            part(message);
        }

    }

    private void join(Message message) {
        Channel channel = repository.find(Channel.class, message.getChannel().getId());
        //CommunityUser user = findUser(context.getSession(), )
    }

    private void part(Message message) {
        Channel channel = repository.find(Channel.class, message.getChannel().getId());
    }

}

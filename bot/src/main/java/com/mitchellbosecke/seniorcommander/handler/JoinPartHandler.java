package com.mitchellbosecke.seniorcommander.handler;

import com.mitchellbosecke.seniorcommander.domain.ChannelConfiguration;
import com.mitchellbosecke.seniorcommander.domain.Community;
import com.mitchellbosecke.seniorcommander.domain.CommunityUser;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

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
        ChannelConfiguration channel = repository.find(ChannelConfiguration.class, message.getChannel().getId());
        CommunityUser user = repository.findSender(message);
        if (user == null) {
            user = addUser(message);
        }
        channel.getOnlineUsers().add(user);
    }

    private void part(Message message) {
        ChannelConfiguration channel = repository.find(ChannelConfiguration.class, message.getChannel().getId());
        CommunityUser user = repository.findSender(message);
        if (user == null) {
            user = addUser(message);
        }
        channel.getOnlineUsers().remove(user);
    }

    private CommunityUser addUser(Message message) {
        Community community = repository.findCommunity(message);

        CommunityUser user = new CommunityUser();
        user.setCommunity(community);
        user.setName(message.getSender().toLowerCase());
        user.setFirstSeen(new Date());
        user.setAccessLevel(CommunityUser.AccessLevel.USER.name());

        repository.persist(user);

        return user;
    }
}

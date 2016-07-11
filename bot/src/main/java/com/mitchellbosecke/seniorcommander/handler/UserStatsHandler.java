package com.mitchellbosecke.seniorcommander.handler;

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
public class UserStatsHandler implements MessageHandler {

    private Logger logger = LoggerFactory.getLogger(UserStatsHandler.class);

    private final Repository repository;

    public UserStatsHandler(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(Message message) {

        if (Message.Type.USER.equals(message.getType())) {

            CommunityUser user = repository.findSender(message);

            if (user == null) {
                Community community = repository.findCommunity(message);

                user = new CommunityUser();
                user.setCommunity(community);
                user.setName(message.getSender().toLowerCase());
                user.setFirstSeen(new Date());
                user.setLastChatted(new Date());
                user.setAccessLevel(CommunityUser.AccessLevel.USER.name());

                repository.persist(user);
            } else {
                user.setLastChatted(new Date());
            }
        }
    }

}

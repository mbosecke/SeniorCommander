package com.mitchellbosecke.seniorcommander.handler;

import com.mitchellbosecke.seniorcommander.Context;
import com.mitchellbosecke.seniorcommander.domain.Community;
import com.mitchellbosecke.seniorcommander.domain.CommunityUser;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import java.util.Date;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class UserAddHandler implements MessageHandler {

    private Logger logger = LoggerFactory.getLogger(UserAddHandler.class);

    @Override
    public void handle(Context context, Message message) {
        Session session = context.getSession();

        CommunityUser user;

        try {
            //@formatter:off
            user = session.createQuery("SELECT DISTINCT cu " +
                    "FROM CommunityUser cu " +
                    "JOIN cu.community c " +
                    "JOIN c.channelConfigurations ccs " +
                    "WHERE cu.name = :name " +
                    "AND ccs.id = :channelId", CommunityUser.class)
                    .setParameter("name", message.getSender())
                    .setParameter("channelId", message.getChannel().getId())
                    .getSingleResult();
            //@formaterr:on

            user.setLastChatted(new Date());

        }catch(NoResultException ex){

            //@formatter:off
            Community community = session.createQuery("SELECT DISTINCT c " +
                    "FROM Community c " +
                    "JOIN c.channelConfigurations ccs " +
                    "WHERE ccs.id = :channelId", Community.class)
                    .setParameter("channelId", message.getChannel().getId())
                    .getSingleResult();
            //@formatter:on

            user = new CommunityUser();
            user.setCommunity(community);
            user.setName(message.getSender().toLowerCase());
            user.setFirstSeen(new Date());
            user.setLastChatted(new Date());
            user.setAccessLevel("user");
        }

        session.persist(user);
    }

}

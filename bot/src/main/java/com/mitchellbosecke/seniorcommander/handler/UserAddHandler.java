package com.mitchellbosecke.seniorcommander.handler;

import com.mitchellbosecke.seniorcommander.Context;
import com.mitchellbosecke.seniorcommander.domain.Community;
import com.mitchellbosecke.seniorcommander.domain.CommunityUser;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class UserAddHandler implements MessageHandler {

    private Logger logger = LoggerFactory.getLogger(UserAddHandler.class);



    @Override
    public void handle(Context context, Message message) {
       if(message.getContent().startsWith("!useradd")){
           Session session = context.getSession();

           Community community = session.find(Community.class, 0l);

           CommunityUser user = new CommunityUser();
           user.setCommunity(community);
           user.setName("mbosecke");
           user.setAccessLevel("superadmin");

           session.persist(user);
       }
    }

}

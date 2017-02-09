package com.mitchellbosecke.seniorcommander.extension.core.event;

import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.domain.AccessLevel;
import com.mitchellbosecke.seniorcommander.domain.CommunityUserModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import com.mitchellbosecke.seniorcommander.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class ModListHandler implements EventHandler {

    private Logger logger = LoggerFactory.getLogger(ModListHandler.class);

    private final UserService userService;

    public ModListHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(Message message) {
        if (Message.Type.MOD_LIST.equals(message.getType())) {

            String[] usernames = message.getContent().substring(message.getContent().indexOf(':') + 1).split(", ");

            for(String username : usernames){
                CommunityUserModel user = userService.findOrCreateUser(message.getChannel(), username.trim());

                if(user != null && !user.getAccessLevel().hasAccess(AccessLevel.MODERATOR)){
                    user.setAccessLevel(AccessLevel.MODERATOR);
                }
            }
        }
    }
}

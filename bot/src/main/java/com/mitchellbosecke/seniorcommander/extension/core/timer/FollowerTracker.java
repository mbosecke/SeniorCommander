package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mb3364.http.RequestParams;
import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.ChannelFollowsResponseHandler;
import com.mb3364.twitch.api.models.ChannelFollow;
import com.mitchellbosecke.seniorcommander.AccessLevel;
import com.mitchellbosecke.seniorcommander.domain.CommunityUserModel;
import com.mitchellbosecke.seniorcommander.extension.core.channel.TwitchChannel;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import com.mitchellbosecke.seniorcommander.timer.Timer;
import com.typesafe.config.ConfigFactory;
import org.hibernate.SessionFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class FollowerTracker implements Timer {

    private final long id;
    private final long interval;
    private final TwitchChannel channel;
    private final UserService userService;
    private final SessionFactory sessionFactory;

    public FollowerTracker(long id, long interval, TwitchChannel channel, UserService userService, SessionFactory sessionFactory) {
        this.id = id;
        this.interval = interval;
        this.channel = channel;
        this.userService = userService;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void perform() {
        Twitch client = new Twitch();
        client.setClientId(ConfigFactory.load().getString("twitch.clientId"));

        String channelName = channel.getChannel().replaceAll("#", "");
        RequestParams params = new RequestParams();
        client.channels().getFollows(channelName, new ChannelFollowsResponseHandler() {
            @Override
            public void onSuccess(int i, List<ChannelFollow> list) {
                try {
                    sessionFactory.getCurrentSession().beginTransaction();

                    for (ChannelFollow follow : list) {
                        String username = follow.getUser().getName();
                        CommunityUserModel user = userService.findUser(channel, username);

                        Date followDate = follow.getCreatedAt();
                        if (user.getFirstFollowed() == null) {
                            user.setFirstFollowed(followDate);
                        }
                        user.setLastFollowed(followDate);

                        if(!user.getAccessLevel().hasAccess(AccessLevel.FOLLOWER)){
                            user.setAccessLevel(AccessLevel.FOLLOWER);
                        }
                    }
                    sessionFactory.getCurrentSession().getTransaction().commit();
                }catch(Exception ex){
                    sessionFactory.getCurrentSession().getTransaction().rollback();
                    throw ex;
                }
            }

            @Override
            public void onFailure(int i, String s, String s1) {

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public long getInterval() {
        return interval;
    }
}

package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;
import com.mitchellbosecke.seniorcommander.extension.core.channel.TwitchChannel;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class FollowerTrackerFactory {

    public List<FollowerAudit> build(SessionFactory sessionFactory, List<Channel> channels, UserService userService) {
        List<FollowerAudit> timers = new ArrayList<>();

        List<TimerModel> timerModels = sessionFactory.getCurrentSession()
                .createQuery("SELECT tm FROM TimerModel tm WHERE tm.implementation = :implementation AND tm.enabled = true", TimerModel.class)
                .setParameter("implementation", FollowerAudit.class.getName()).getResultList();

        for (TimerModel timerModel : timerModels) {

            Map<Long, Channel> availableChannels = channels.stream().filter(c -> c instanceof TwitchChannel)
                    .collect(Collectors.toMap(Channel::getId, c -> c));

            Set<ChannelModel> communityChannelModels = timerModel.getCommunityModel().getChannelModels();
            for (ChannelModel channelModel : communityChannelModels) {
                if (availableChannels.containsKey(channelModel.getId())) {
                    FollowerAudit tracker = new FollowerAudit(timerModel.getId(), timerModel
                            .getInterval(), (TwitchChannel) availableChannels
                            .get(channelModel.getId()), userService, sessionFactory);
                    timers.add(tracker);
                }
            }

        }

        return timers;
    }
}

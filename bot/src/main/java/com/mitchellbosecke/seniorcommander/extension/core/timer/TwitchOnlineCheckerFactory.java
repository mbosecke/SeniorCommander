package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;
import com.mitchellbosecke.seniorcommander.extension.core.channel.IrcChannel;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class TwitchOnlineCheckerFactory {

    public List<TwitchOnlineChecker> build(Session session, List<Channel> channels, UserService userService) {
        List<TwitchOnlineChecker> timers = new ArrayList<>();

        List<TimerModel> timerModels = session
                .createQuery("SELECT tm FROM TimerModel tm WHERE tm.implementation = :implementation AND tm.enabled = true", TimerModel.class)
                .setParameter("implementation", TwitchOnlineChecker.class.getName()).getResultList();

        for (TimerModel timerModel : timerModels) {

            Map<Long, Channel> availableChannels = channels.stream().filter(c -> c instanceof IrcChannel)
                    .collect(Collectors.toMap(Channel::getId, c -> c));

            Set<ChannelModel> communityChannelModels = timerModel.getCommunityModel().getChannelModels();
            for (ChannelModel channelModel : communityChannelModels) {
                if (availableChannels.containsKey(channelModel.getId())) {
                    TwitchOnlineChecker onlineChecker = new TwitchOnlineChecker(timerModel.getId(), timerModel
                            .getInterval(), (IrcChannel) availableChannels.get(channelModel.getId()));
                    timers.add(onlineChecker);
                }
            }

        }

        return timers;
    }
}

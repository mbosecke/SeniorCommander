package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.ChannelModel;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class ShoutTimerFactory {

    public List<Shout> build(Session session, List<Channel> channels, MessageQueue messageQueue) {
        List<Shout> shouts = new ArrayList<>();

        List<TimerModel> timerModels = session
                .createQuery("SELECT tm FROM TimerModel tm WHERE tm.implementation = :implementation AND tm.enabled = true", TimerModel.class)
                .setParameter("implementation", Shout.class.getName()).getResultList();

        for (TimerModel timerModel : timerModels) {

            Map<Long, Channel> availableChannels = channels.stream().collect(Collectors.toMap(Channel::getId, c -> c));

            Set<ChannelModel> communityChannelModels = timerModel.getCommunityModel().getChannelModels();
            for (ChannelModel channelModel : communityChannelModels) {
                Shout shout = new Shout(timerModel.getId(), timerModel.getInterval(), messageQueue, availableChannels
                        .get(channelModel.getId()), timerModel.getMessage());

                shouts.add(shout);
            }

        }

        return shouts;
    }
}

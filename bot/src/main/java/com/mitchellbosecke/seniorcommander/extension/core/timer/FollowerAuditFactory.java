package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;
import com.mitchellbosecke.seniorcommander.extension.core.channel.TwitchChannel;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;

import java.util.List;
import java.util.Map;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class FollowerAuditFactory extends AbstractTimerFactory<FollowerAudit> {

    private final UserService userService;

    public FollowerAuditFactory(List<Channel> channels, UserService userService) {
        super(channels);
        this.userService = userService;
    }

    @Override
    protected FollowerAudit constructTimerFromModel(TimerModel timerModel, Map<Long, Channel> channels) {
        return new FollowerAudit(timerModel.getId(), timerModel.getInterval(), (TwitchChannel) channels
                .get(timerModel.getChannelModel().getId()), userService);
    }

    @Override
    protected Class<FollowerAudit> getTimerClass() {
        return FollowerAudit.class;
    }
}

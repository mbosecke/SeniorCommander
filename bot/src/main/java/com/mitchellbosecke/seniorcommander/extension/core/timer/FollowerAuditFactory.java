package com.mitchellbosecke.seniorcommander.extension.core.timer;

import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;
import com.mitchellbosecke.seniorcommander.extension.core.service.UserService;

/**
 * Created by mitch_000 on 2016-09-10.
 */
public class FollowerAuditFactory extends AbstractTimerFactory<FollowerAudit> {

    private final UserService userService;

    public FollowerAuditFactory(SeniorCommander seniorCommander, UserService userService) {
        super(seniorCommander);
        this.userService = userService;
    }

    @Override
    protected FollowerAudit constructTimerFromModel(SeniorCommander seniorCommander, TimerModel timerModel) {
        return new FollowerAudit(timerModel.getId(), timerModel.getInterval(), timerModel.getChannelModel()
                .getId(), seniorCommander, userService);
    }

    @Override
    protected Class<FollowerAudit> getTimerClass() {
        return FollowerAudit.class;
    }
}

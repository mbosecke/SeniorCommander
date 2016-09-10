package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.domain.CommunityModel;
import com.mitchellbosecke.seniorcommander.domain.TimerModel;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface TimerService extends BaseService {

    TimerModel addTimer(CommunityModel communityModel, String message, long interval, long chatLines);

    TimerModel findTimer(CommunityModel communityModel, long communitySequence);

}

package com.mitchellbosecke.seniorcommander.extension.core.service;

import com.mitchellbosecke.seniorcommander.domain.Community;
import com.mitchellbosecke.seniorcommander.domain.Timer;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface TimerService extends BaseService {

    Timer addTimer(Community community, String message, long interval, long chatLines);

    Timer findTimer(Community community, long communitySequence);

}

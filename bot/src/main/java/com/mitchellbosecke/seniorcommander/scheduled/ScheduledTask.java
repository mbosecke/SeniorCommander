package com.mitchellbosecke.seniorcommander.scheduled;

import com.mitchellbosecke.seniorcommander.Context;

/**
 * Created by mitch_000 on 2016-07-06.
 */
public interface ScheduledTask {

    void initiate(Context context);
}

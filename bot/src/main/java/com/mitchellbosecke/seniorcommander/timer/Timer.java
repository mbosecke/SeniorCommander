package com.mitchellbosecke.seniorcommander.timer;

/**
 * Created by mitch_000 on 2016-07-31.
 */
public interface Timer {

    long getId();

    void perform();

    long getInterval();
}

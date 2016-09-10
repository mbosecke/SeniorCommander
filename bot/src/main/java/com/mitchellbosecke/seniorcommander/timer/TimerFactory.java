package com.mitchellbosecke.seniorcommander.timer;

import org.hibernate.Session;

import java.util.List;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface TimerFactory {

    List<Timer> build(Session session);

}

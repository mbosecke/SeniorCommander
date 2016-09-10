package com.mitchellbosecke.seniorcommander.task;

import org.hibernate.Session;

import java.util.List;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface TaskFactory {

    List<Task> build(Session session);

}

package com.mitchellbosecke.seniorcommander.channel;

import org.hibernate.Session;

import java.util.List;

/**
 * Created by mitch_000 on 2016-07-10.
 */
public interface ChannelFactory {

    List<Channel> build(Session session);

}

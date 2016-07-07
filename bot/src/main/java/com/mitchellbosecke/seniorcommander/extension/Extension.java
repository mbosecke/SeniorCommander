package com.mitchellbosecke.seniorcommander.extension;

import com.mitchellbosecke.seniorcommander.message.MessageHandler;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.scheduled.ScheduledTask;

import java.util.List;

/**
 * Created by mitch_000 on 2016-07-05.
 */
public interface Extension {

    List<MessageHandler> getMessageHandlers();

    List<Channel> getChannels();

    List<ScheduledTask> getScheduledTasks();
}

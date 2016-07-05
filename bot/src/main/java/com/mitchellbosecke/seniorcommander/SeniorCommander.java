package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.channel.IrcChannel;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import org.jibble.pircbot.IrcException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class SeniorCommander {


    public SeniorCommander(Configuration configuration){

        MessageQueue messageQueue = new MessageQueue();

        List<Channel> channels = new ArrayList<>();
        channels.add(new IrcChannel());


        for(Channel channel : channels){
            try {
                channel.listen(configuration, messageQueue);
            } catch (IOException e) {
               throw new RuntimeException(e);
            }
        }


    }

    public static void main(String[] args) throws IOException, IrcException {
        Configuration config = new Configuration("config.properties");
        new SeniorCommander(config);
    }
}

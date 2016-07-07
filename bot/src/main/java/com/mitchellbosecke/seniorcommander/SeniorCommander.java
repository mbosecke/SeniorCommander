package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.core.CoreExtension;
import org.jibble.pircbot.IrcException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class SeniorCommander {


    public SeniorCommander(Configuration configuration, List<Extension> extensions) {

        Context context = buildContext(configuration, extensions);

        // each channel runs on it's own thread
        ExecutorService executor = Executors.newFixedThreadPool(context.getChannels().size());
        for (Channel channel : context.getChannels()) {
            executor.submit(() -> {
                try {
                    channel.listen(context);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        for (int i = 0; i < 10; i++) {
            Message message = context.getMessageQueue().readMessage();
            for (MessageHandler handler : context.getMessageHandlers()) {
                try {
                    handler.handle(context, message);
                }catch(Exception ex){
                    // we don't want to die! Just log the error.
                   System.out.println(ex.getMessage());
                }
            }
        }

        for (Channel channel : context.getChannels()) {
            channel.shutdown();
        }
        executor.shutdown();
    }

    private Context buildContext(Configuration configuration, List<Extension> extensions) {
        List<Channel> channels = new ArrayList<>();
        List<MessageHandler> handlers = new ArrayList<>();
        for (Extension extension : extensions) {
            channels.addAll(extension.getChannels());
            handlers.addAll(extension.getMessageHandlers());
        }
        return new Context(configuration, new MessageQueue(), channels, handlers);
    }


    public static void main(String[] args) throws IOException, IrcException {
        Configuration config = new Configuration("config.properties");
        Extension extension = new CoreExtension();
        new SeniorCommander(config, Collections.singletonList(extension));
    }
}

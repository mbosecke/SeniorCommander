package com.mitchellbosecke.seniorcommander.handler;

import com.mitchellbosecke.seniorcommander.Context;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageHandler;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mitch_000 on 2016-07-04.
 */
public class DiceHandler implements MessageHandler {

    private final Pattern pattern = Pattern.compile("!d(\\d+)");

    private final Cooldown cooldown = new Cooldown(1, TimeUnit.MINUTES);

    @Override
    public void handle(Context context, Message message) {
        Matcher matcher = pattern.matcher(message.getContent());
        if (matcher.matches()) {

            String user = message.getUser();

            if (cooldown.isReady(user)) {
                Integer num = Integer.valueOf(matcher.group(1));
                Random generator = new Random();
                int result = generator.nextInt(num) + 1;

                context.getMessageQueue().add(Message.response(message, "You rolled a " + result + "."));
                cooldown.reset(user);
            } else {
                context.getMessageQueue().add(Message.response(message, "Son, you need to slow down. " + cooldown
                        .secondsRemaining(message.getUser()) + " seconds remaining."));
            }
        }
    }
}

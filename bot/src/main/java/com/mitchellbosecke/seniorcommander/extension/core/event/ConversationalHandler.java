package com.mitchellbosecke.seniorcommander.extension.core.event;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class ConversationalHandler implements EventHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final MessageQueue messageQueue;

    private ChatterBot bot;

    public ConversationalHandler(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
        ChatterBotFactory factory = new ChatterBotFactory();
        try {
            bot = factory.create(ChatterBotType.CLEVERBOT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(Message message) {
        if(Message.Type.USER.equals(message.getType())) {
            if (SeniorCommander.getName().equalsIgnoreCase(message.getRecipient()) && !message.getContent().startsWith("!")) {

                String response = "Sorry, I can't talk right now.";

                if(bot != null) {
                    ChatterBotSession session = bot.createSession(Locale.CANADA);
                    try {
                        response = session.think(message.getContent());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                messageQueue.add(Message.response(message, response));
            }
        }
    }
}

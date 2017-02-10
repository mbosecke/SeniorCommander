package com.mitchellbosecke.seniorcommander.extension.core.event;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import com.mitchellbosecke.seniorcommander.EventHandler;
import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.utils.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class AiHandler implements EventHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final MessageQueue messageQueue;

    private final AIDataService bot;

    public AiHandler(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;

        String clientId = ConfigUtils.getString("ai.clientId");
        AIConfiguration configuration = new AIConfiguration(clientId);
        bot = new AIDataService(configuration);
    }

    @Override
    public void handle(Message message) {
        if (Message.Type.USER.equals(message.getType())) {
            if (SeniorCommander.getName().equalsIgnoreCase(message.getRecipient()) && !message.getContent()
                    .startsWith("!")) {

                if (bot != null) {
                    AIRequest request = new AIRequest(message.getContent());
                    try {
                        AIResponse response = bot.request(request);
                        if (response.isError()) {
                            messageQueue.add(Message.response(message, "Sorry, I can't talk right now."));
                        } else {
                            String reply = response.getResult().getFulfillment().getSpeech();
                            if (reply == null || reply.isEmpty()) {
                                messageQueue.add(Message.response(message, "I have nothing to say to you."));
                            } else {
                                messageQueue.add(Message.response(message, reply));
                            }
                        }
                    } catch (AIServiceException e) {
                        messageQueue.add(Message.response(message, "Sorry, I can't talk right now."));
                    }
                }
            }
        }
    }
}

package com.mitchellbosecke.seniorcommander.extension.core.channel;

import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by mitch_000 on 2017-01-28.
 */
public class HttpChannel extends SocketChannel {

    public static final String HEADER_CONTENT_LENGTH = "content-length";

    private static final Logger logger = LoggerFactory.getLogger(HttpChannel.class);

    private final List<Channel> channels;

    public HttpChannel(Integer port, List<Channel> channels) {
        super(-1, port);
        this.channels = channels;
    }

    @Override
    public long getId() {
        return -1;
    }

    @Override
    public String getBotUsername() {
        return SeniorCommander.getName();
    }

    @Override
    protected void handleConnection(MessageQueue messageQueue, BufferedReader input,
                                    PrintWriter output) throws IOException {
        logger.debug("Handling HTTP connection");

        Map<String, String> headers = new HashMap<>();
        int contentLength;
        String body = null;

        String inputLine;
        try {
            while ((inputLine = input.readLine()) != null) {
                if (inputLine.isEmpty()) { // empty line is separates headers from body
                    contentLength = headers.containsKey(HEADER_CONTENT_LENGTH) ? Integer
                            .valueOf(headers.get(HEADER_CONTENT_LENGTH)) : 0;

                    if (contentLength > 0) {
                        char[] bodyChars = new char[contentLength];
                        input.read(bodyChars, 0, contentLength);

                        body = new String(bodyChars);
                    }

                    break;
                }

                if (inputLine.contains(":")) {
                    String[] split = inputLine.split(":");
                    headers.put(split[0].trim().toLowerCase(), split[1].trim());
                }
            }
        } catch (SocketTimeoutException ex) {
            // do nothing, there may have not been a body to the request
        }

        handleMessage(messageQueue, output, body);
    }

    private void handleMessage(MessageQueue messageQueue, PrintWriter output, String body) {
        logger.debug("Received message on HttpChannel [{}]", body);

        // body is in format ACTION:ID where action is one of START, STOP, RESTART and ID is
        // the ID of the channel to perform the action on.
        if (body.contains(":")) {
            String[] split = body.split(":");
            String action = split[0].toUpperCase();
            Long channelId = Long.valueOf(split[1].trim());
            Optional<Channel> channelOptional = channels.stream().filter(c -> c.getId() == channelId).findFirst();

            if (channelOptional.isPresent()) {
                Channel channel = channelOptional.get();

                switch (action) {
                    case "START":
                        messageQueue.add(Message.startChannel(channel));
                        httpResponse(output, 200, "Channel started");
                        break;
                    case "STOP":
                        messageQueue.add(Message.stopChannel(channel));
                        httpResponse(output, 200, "Channel stopped");
                        break;
                    case "RESTART":
                        messageQueue.add(Message.restartChannel(channel));
                        httpResponse(output, 200, "Channel restarted");
                        break;
                    case "STATUS":
                        httpResponse(output, 200, String.valueOf(channel.isListening()));
                        break;
                    default:
                        httpResponse(output, 400, "Unknown command");
                }
            } else {
                httpResponse(output, 400, "Channel ID doesn't exist");
            }
        } else {
            httpResponse(output, 500, "Bad request");
        }
    }

    private void httpResponse(PrintWriter output, int responseCode, String content) {
        output.write("HTTP/1.0 " + responseCode + " OK\r\n");
        output.write("Server: SeniorCommander\r\n");
        output.write("Content-Type: text/html\r\n");
        output.write("Connection: close\r\n");
        if (content != null) {
            output.write("Content-Length: " + content.length() + "\r\n");
        }
        output.write("\r\n");
        output.write(content + "\r\n");
        output.flush();
    }

    @Override
    public void sendMessage(String content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendMessage(String recipient, String content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendWhisper(String recipient, String content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void timeout(String user, long duration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCommunityOnline() {
        return false;
    }
}

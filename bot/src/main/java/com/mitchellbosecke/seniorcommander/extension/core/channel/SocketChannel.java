package com.mitchellbosecke.seniorcommander.extension.core.channel;

import com.mitchellbosecke.seniorcommander.SeniorCommander;
import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.message.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class SocketChannel implements Channel {

    Logger logger = LoggerFactory.getLogger(getClass());

    private ServerSocket serverSocket;

    private PrintWriter output;

    private static Pattern targetedMessage = Pattern.compile("(\\w+):\\s*(.*)");

    /**
     * Ensure that either startup or shutdown are performed exclusively.
     */
    private Object startupLock = new Object();

    private volatile boolean running = true;

    private final long id;

    private final Integer port;

    /**
     * How long it blocks while reading from the socket before
     * it temporarily stops to perform maintenance (ex. handle shutdown request).
     */
    private static final int READ_TIMEOUT = 1000;

    public SocketChannel(long id, Integer port) {
        this.id = id;
        this.port = port;
    }

    @Override
    public void listen(MessageQueue messageQueue) throws IOException {
        BufferedReader input = null;

        synchronized (startupLock) {
            if (running) {
                serverSocket = new ServerSocket(port);
            }
        }

        while (running) {

            Socket clientSocket;
            // block until a client connects
            try {
                clientSocket = serverSocket.accept();
            } catch (SocketException ex) {
                // socket has been closed
                break;
            }
            logger.debug("Socket channel connection established");
            clientSocket.setSoTimeout(READ_TIMEOUT);

            output = new PrintWriter(clientSocket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            handleConnection(messageQueue, input, output);
        }

    }

    private void handleConnection(MessageQueue messageQueue, BufferedReader input,
                                  PrintWriter output) throws IOException {
        if (input != null) {

            // indefinite connection until channel is explicitly shutdown
            while (true) {
                String inputLine = null;

                try {
                    inputLine = input.readLine();
                } catch (SocketTimeoutException ex) {
                    // do nothing
                }

                if (inputLine != null) {
                    String[] senderSplit = splitSender(inputLine);
                    String sender = senderSplit[0];
                    String message = senderSplit[1];

                    String[] recipientSplit = MessageUtils.splitRecipient(message);
                    String recipient = recipientSplit[0];
                    message = recipientSplit[1];
                    messageQueue.add(Message.userInput(this, sender, recipient, message, false));
                }
                if (!running) {
                    break;
                }
            }
        }
    }

    private String[] splitSender(String message) {
        String sender;
        Matcher matcher = targetedMessage.matcher(message);
        if (matcher.matches()) {
            sender = matcher.group(1);
            message = matcher.group(2);
        } else {
            logger.debug(String.format("No sender for message: [%s]", message));
            throw new RuntimeException("No sender for message");
        }
        return new String[]{sender, message};
    }

    @Override
    public void sendMessage(String content) {
        if (running && output != null) {
            output.println(content);
        }
    }

    @Override
    public void sendMessage(String recipient, String content) {
        if (running && output != null) {
            content = String.format("@%s, %s", recipient, content);
            output.println(content);
        }
    }

    @Override
    public void sendWhisper(String recipient, String content) {
        if (running && output != null) {
            content = String.format("/w @%s, %s", recipient, content);
            output.println(content);
        }
    }

    @Override
    public void timeout(String user, long duration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void shutdown() {
        synchronized (startupLock) {
            running = false;
            if (serverSocket != null) {
                logger.debug("Shutting down socket channel.");
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean isCommunityOnline() {
        return false;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getBotUsername() {
        return SeniorCommander.getName();
    }
}


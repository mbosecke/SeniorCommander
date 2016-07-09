package com.mitchellbosecke.seniorcommander.channel;

import com.mitchellbosecke.seniorcommander.Context;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class SocketChannel implements Channel {

    public static final String CONFIG_SOCKET_PORT = "socket.port";

    Logger logger = LoggerFactory.getLogger(getClass());

    private ServerSocket serverSocket;

    private PrintWriter output;

    /**
     * Ensure that either startup or shutdown are performed exclusively.
     */
    private Object startupLock = new Object();

    private volatile boolean running = true;

    @Override
    public void listen(Context context) throws IOException {
        BufferedReader input = null;

        synchronized (startupLock) {
            if (running) {
                String portConfig = context.getConfiguration().getProperty(CONFIG_SOCKET_PORT);
                if (portConfig != null) {
                    serverSocket = new ServerSocket(Integer.valueOf(portConfig));

                    // block until a client connects
                    Socket clientSocket = serverSocket.accept();
                    clientSocket.setSoTimeout(100);

                    output = new PrintWriter(clientSocket.getOutputStream(), true);
                    input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    logger.debug("Socket channel started");
                }
            }
        }

        String inputLine;
        if (input != null) {

            while (true) {
                inputLine = input.readLine();
                if (inputLine != null) {
                    String[] split = MessageUtils.splitRecipient(inputLine);
                    String recipient = split[0];
                    String message = split[1];
                    context.getMessageQueue().add(Message.userInput(this, "user", recipient, message, false));
                }
                if (!running) {
                    break;
                }
            }
        }
    }

    @Override
    public void sendMessage(Context context, String content) {
        if (running) {
            output.println(content);
        }
    }

    @Override
    public void sendMessage(Context context, String recipient, String content) {
        if (running) {
            content = String.format("@%s, %s", recipient, content);
            output.println(content);
        }
    }

    @Override
    public void sendWhisper(Context context, String recipient, String content) {
        if (running) {
            content = String.format("/w @%s, %s", recipient, content);
            output.println(content);
        }
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
}

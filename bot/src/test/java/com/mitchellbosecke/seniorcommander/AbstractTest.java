package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.channel.SocketChannel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class AbstractTest {

    static Logger logger = LoggerFactory.getLogger(AbstractTest.class);

    protected static PrintWriter output;

    protected static BufferedReader input;

    /**
     * Thread for the bot
     */
    private static final ExecutorService executorService = Executors.newFixedThreadPool(1);

    @BeforeClass
    public static void connectToSocket(){
        Configuration config = new Configuration("config.properties");

        executorService.submit(() -> new SeniorCommander(config, Collections.emptyList()));

        // connect to socket channel
        Socket socket = null;

        int threshold = 10;
        int retryCounter = 0;
        while (retryCounter < threshold) {
            retryCounter++;
            try {
                logger.debug(retryCounter + ": Attempting to connect to socket channel.");
                socket = new Socket("localhost", Integer.valueOf(config.getProperty(SocketChannel.CONFIG_SOCKET_PORT)));
                break;
            } catch (IOException ex) {
                logger.debug("Socket attempt failed");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (socket != null) {
            try {
                output = new PrintWriter(socket.getOutputStream(), true);
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Could not connect to socket channel");
        }
    }

    @AfterClass
    public static void shutdown(){
        executorService.shutdown();
    }



    protected boolean testCommandAndResult(String command, String expectedResult) {
        output.println(command);
        try {
            return expectedResult.equalsIgnoreCase(input.readLine());
        } catch (IOException e) {
            return false;
        }
    }

    protected boolean testCommandAndResult(String command, Pattern expectedResult) {
        output.println(command);
        try {
            String reply = input.readLine();
            logger.debug("Reply: " + reply);
            Matcher matcher = expectedResult.matcher(reply);
            return matcher.matches();
        } catch (IOException e) {
            return false;
        }
    }

}

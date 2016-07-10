package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.message.MessageUtils;
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
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class AbstractTest {

    static Logger logger = LoggerFactory.getLogger(AbstractTest.class);

    protected static PrintWriter output;

    protected static BufferedReader input;

    private static SeniorCommander commander;

    private static ExecutorService executorService;

    @BeforeClass
    public static void connectToSocket() {
        executorService = Executors.newFixedThreadPool(1);
        Configuration config = new Configuration("config.properties");

        commander = new SeniorCommander(config, Collections.emptyList());
        executorService.submit(() -> commander.run());

        // connect to socket channel
        Socket socket = null;

        int threshold = 10;
        int retryCounter = 0;
        while (retryCounter < threshold) {
            retryCounter++;
            try {
                logger.debug(retryCounter + ": Attempting to connect to socket channel.");
                socket = new Socket("localhost", Integer.valueOf(config.getProperty("socket.port")));
                socket.setSoTimeout(5 * 60 * 1000);
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
    public static void shutdown() {
        logger.debug("Shutting down executor service");
        commander.shutdown();
        ExecutorUtils.shutdown(executorService, 10, TimeUnit.SECONDS);
    }

    protected boolean testCommandAndResult(String command, String expectedResult) {
        output.println(command);
        try {
            String reply = removeRecipient(input.readLine());
            return expectedResult.equalsIgnoreCase(reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean testCommandAndResult(String command, Pattern expectedResult) {
        output.println(command);
        try {
            String reply = removeRecipient(input.readLine());
            Matcher matcher = expectedResult.matcher(reply);
            return matcher.matches();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String removeRecipient(String reply) {
        return MessageUtils.splitRecipient(reply)[1];
    }

}

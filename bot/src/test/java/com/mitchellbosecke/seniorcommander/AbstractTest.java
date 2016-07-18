package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.message.MessageUtils;
import com.mitchellbosecke.seniorcommander.utils.DatabaseManager;
import com.mitchellbosecke.seniorcommander.utils.ExecutorUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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

    protected PrintWriter output;

    protected BufferedReader input;

    private SeniorCommander commander;

    private ExecutorService executorService;

    private int READ_TIMEOUT = 20 * 1000;

    @Before
    public void connectToSocket() {
        executorService = Executors.newFixedThreadPool(1);

        commander = new SeniorCommanderImpl();
        executorService.submit(() -> commander.run());

        Config config = ConfigFactory.load();

        // connect to socket channel
        Socket socket = null;

        int threshold = 10;
        int retryCounter = 0;
        while (retryCounter < threshold) {
            retryCounter++;
            try {
                logger.debug(retryCounter + ": Attempting to connect to socket channel.");
                socket = new Socket("localhost", config.getInt("socket.port"));
                socket.setSoTimeout(READ_TIMEOUT);
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

    @After
    public void shutdown() {
        logger.debug("Shutting down executor service");
        commander.shutdown();
        ExecutorUtils.shutdown(executorService, 10, TimeUnit.SECONDS);

        DatabaseManager manager = new DatabaseManager();
        manager.teardown();
    }

    protected void testCommandAndResult(String command, String expectedResult) {
        output.println(command);
        try {
            String reply = removeRecipient(input.readLine());
            Assert.assertEquals(expectedResult, reply);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void testCommandAndResult(String command, Pattern expectedResult) {
        output.println(command);
        try {
            String reply = removeRecipient(input.readLine());
            Matcher matcher = expectedResult.matcher(reply);
            Assert.assertTrue(matcher.matches());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private String removeRecipient(String reply) {
        return MessageUtils.splitRecipient(reply)[1];
    }

}

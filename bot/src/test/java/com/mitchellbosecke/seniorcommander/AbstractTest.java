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
import java.net.SocketTimeoutException;
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

    private Socket socket;

    private int READ_TIMEOUT = 5 * 1000;

    @Before
    public void connectToSocket() {
        DatabaseManager manager = new DatabaseManager();
        manager.teardown();

        executorService = Executors.newFixedThreadPool(1);

        commander = new SeniorCommanderImpl();
        executorService.submit(() -> commander.run());

        Config config = ConfigFactory.load();

        // connect to socket channel
        socket = null;

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
    public void shutdown() throws IOException {

        try {
            // check that there is no input from the bot
            socket.setSoTimeout(500);
            try {
                String unexpectedOutput = input.readLine();
                if (unexpectedOutput != null) {
                    throw new RuntimeException("Unexpected output from bot: " + unexpectedOutput);
                }
            } catch (SocketTimeoutException ex) {
                // we expect to be here
            }
        } finally {

            // shutdown everything
            logger.debug("Shutting down executor service");
            commander.shutdown();
            ExecutorUtils.shutdown(executorService, 10, TimeUnit.SECONDS);
        }

    }

    protected void send(String command) {
        output.println(command);
        output.flush();
    }

    protected String recv(String expectedReply) {
        try {
            String reply = removeRecipient(input.readLine());
            Assert.assertEquals(expectedReply, reply);
            return reply;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String recv(Pattern expectedResult) {
        try {
            String reply = removeRecipient(input.readLine());
            Matcher matcher = expectedResult.matcher(reply);
            Assert.assertTrue(String.format("Reply does not match. Expected: [%s] Actual: [%s]", expectedResult
                    .toString(), reply), matcher.matches());
            return reply;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String removeRecipient(String reply) {
        return MessageUtils.splitRecipient(reply)[1];
    }

}

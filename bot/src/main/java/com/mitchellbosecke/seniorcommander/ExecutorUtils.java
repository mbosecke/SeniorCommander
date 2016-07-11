package com.mitchellbosecke.seniorcommander;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by mitch_000 on 2016-07-09.
 */
public class ExecutorUtils {

    static Logger logger = LoggerFactory.getLogger(ExecutorUtils.class);

    public static void shutdown(ExecutorService executorService, long timeout, TimeUnit timeUnit) {

        // disable new tasks
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(timeout, timeUnit)) {

                logger.debug("Executor repository is not terminating by itself.");

                // cancel currently executing tasks
                executorService.shutdownNow();

                if(!executorService.awaitTermination(timeout, timeUnit)){
                    logger.debug("Could not shut down executor repository");
                }
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

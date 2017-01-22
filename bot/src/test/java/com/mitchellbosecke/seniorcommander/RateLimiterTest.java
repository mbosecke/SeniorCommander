package com.mitchellbosecke.seniorcommander;

import com.mitchellbosecke.seniorcommander.utils.RateLimiter;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

/**
 * Created by mitch_000 on 2017-01-22.
 */
public class RateLimiterTest {

    @Test
    public void rateLimitTest() throws InterruptedException {
        int quantity = 10;
        int windowSize = 3;
        int numberOfJobs = 20;

        RateLimiter limiter = new RateLimiter(quantity, windowSize);
        AtomicInteger successes = new AtomicInteger(0);

        for (int i = 0; i < numberOfJobs; i++) {
            limiter.submit(() -> successes.incrementAndGet());
        }

        // check the first window
        int wait = windowSize - 1; // remove a second from the wait to be sure the jobs from the second window aren't run yet.
        Thread.sleep(wait * 1000);
        assertEquals(quantity, successes.get());

        // check final results
        int numberOfRemainingWindows = (numberOfJobs/quantity) - 1;
        Thread.sleep(numberOfRemainingWindows * windowSize * 1000);
        assertEquals(numberOfJobs, successes.get());
    }
}

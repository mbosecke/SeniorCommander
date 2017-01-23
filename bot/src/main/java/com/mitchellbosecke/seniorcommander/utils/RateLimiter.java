package com.mitchellbosecke.seniorcommander.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by mitch_000 on 2017-01-22.
 */
public class RateLimiter {

    private final ConcurrentLinkedQueue<Runnable> jobs;

    private final DelayQueue<DelayedElement> tokens;

    private final int windowSize;

    private volatile boolean running = true;

    private static final int IDLE_SLEEP = 250;

    Logger logger = LoggerFactory.getLogger(RateLimiter.class);

    /**
     * @param quantity   The number of "actions" that can be taken within a window
     * @param windowSize The number of seconds that a window represents.
     */
    public RateLimiter(int quantity, int windowSize) {
        tokens = new DelayQueue<>();
        jobs = new ConcurrentLinkedQueue<>();
        this.windowSize = windowSize;
        initQueue(quantity);
        initListener();
    }

    private void initQueue(int quantity) {
        for (int i = 0; i < quantity; i++) {
            tokens.add(new DelayedElement(0));
        }
    }

    private void initListener() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> {
            while (running) {
                try {
                    Runnable job = jobs.poll();
                    if (job != null) {
                        long start = System.currentTimeMillis();
                        tokens.take(); // blocks
                        long end = System.currentTimeMillis();
                        double duration = (end - start) / 1000.0;
                        if (duration > 2 * 1000) {
                            logger.debug("RateLimiter throttle a job for {} seconds", duration);
                        }
                        tokens.add(new DelayedElement(windowSize * 1000));
                        job.run();
                    } else {
                        Thread.sleep(IDLE_SLEEP);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void submit(Runnable runnable) {
        jobs.add(runnable);
    }

    public void shutdown() {
        running = false;
        logger.debug("Shutting down RateLimiter");
    }

    private static class DelayedElement implements Delayed {

        private long expiryTime;

        public DelayedElement(long delay) {
            this.expiryTime = System.currentTimeMillis() + delay;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long diff = expiryTime - System.currentTimeMillis();
            return unit.convert(diff, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            if (this.expiryTime < ((DelayedElement) o).expiryTime) {
                return -1;
            }
            if (this.expiryTime > ((DelayedElement) o).expiryTime) {
                return 1;
            }
            return 0;
        }
    }
}

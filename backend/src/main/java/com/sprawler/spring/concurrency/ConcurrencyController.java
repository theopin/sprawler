package com.sprawler.spring.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/spring/concurrency")
public class ConcurrencyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrencyController.class);



    // Async Task Trigger
    @Async
    @GetMapping("/async/task")
    public CompletableFuture<String> triggerAsyncTask() {
        LOGGER.info("Executing async task");
        try {
            Thread.sleep(3000); // Simulate a long-running task
        } catch (InterruptedException e) {
            LOGGER.info("Failed to sleep");
        }
        return CompletableFuture.completedFuture("Async task triggered");
    }


    @Scheduled(fixedRate = 5000) // Executes every 5 seconds independently
    public void runScheduledFixedRateTask() {
        LOGGER.info("Executing scheduled task every 5 seconds");
    }

    @Scheduled(fixedDelay = 1000) // Executes 1 s after conclusion of previous task
    public void runScheduledFixedDelayTask() {
        LOGGER.info("Executing task every after delay");
    }
}

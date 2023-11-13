package com.reddot.tracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class GreetingSchedulingService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GreetingService greetingService;

    @Scheduled(fixedDelay = 30000)
    public void scheduledWorkInSameSpan() throws InterruptedException {
        logger.info("Starting same span work");
        greetingService.doSomeWorkSameSpan();
    }

    @Scheduled(fixedDelay = 30000)
    public void scheduledWorkInNewSpan() throws InterruptedException {
        logger.info("Starting new span work");
        greetingService.doSomeWorkNewSpan();
    }
}

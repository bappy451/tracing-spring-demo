package com.reddot.tracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {
    private static final Logger logger = LoggerFactory.getLogger(GreetingService.class);

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    private final GreetingService greetingService;
    private final GreetingServiceAsync greetingServiceAsync;

    public GreetingController(GreetingService greetingService, GreetingServiceAsync greetingServiceAsync) {
        this.greetingService = greetingService;
        this.greetingServiceAsync = greetingServiceAsync;
    }

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) throws InterruptedException {
        logger.info("Before Service Method Call");
        this.greetingService.doSomeWorkNewSpan();
        logger.info("After Service Method Call");
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @GetMapping("/greeting-async")
    public Greeting greetingAsync(@RequestParam(value = "name", defaultValue = "World") String name) throws InterruptedException {
        logger.info("Before Async Service Method Call");
        this.greetingServiceAsync.doSomeWorkNewSpan();
        logger.info("After Async Service Method Call");
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }
}

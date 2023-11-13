package com.reddot.tracing;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class GreetingSchedulingServiceAsync {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private GreetingServiceAsync greetingService;

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

    @Scheduled(fixedDelay = 30000)
    public void scheduledAsyncWorkInNewSpan() {
        logger.info("Starting Async new span work");
        List<Integer> ids = ids();
        Span span = Span.current();
        Collection<Future<String>> results = new ArrayList<>(ids.size());
        span.setAttribute("records.count", ids.size());

        triggerProcessing(ids, results);
        waitForCompletion(results);
    }

    private void triggerProcessing(List<Integer> ids, Collection<Future<String>> results) {
        ids.forEach(record -> {
            if (record != null) {
                doProcess(record, results);
            }
        });
    }

    private void doProcess(Integer record, Collection<Future<String>> results) {
        Span span = Span.current();
        span.setAttribute("processing.scheduled", record);
        results.add(greetingService.asyncOperation(record));
    }

    private void waitForCompletion(Collection<Future<String>> results) {
        Span span = Span.current();
        results.forEach(result -> {
            try {
                result.get();
            } catch (InterruptedException | ExecutionException e) {
                span.setStatus(StatusCode.ERROR, e.getMessage());
                logger.error("Error processing :", e);
            }
        });
    }

    private List<Integer> ids() {
        return Arrays.asList(1, 2, 3, 4);
    }
}

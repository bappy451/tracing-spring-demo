package com.reddot.tracing;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.extension.annotations.WithSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class GreetingService {

    private static final Logger logger = LoggerFactory.getLogger(GreetingService.class);

    public void doSomeWorkSameSpan() throws InterruptedException {
        Span span = Span.current();

        span.setAttribute("attribute.a0", "value");
        logger.info("Doing some work In Same span");
        TimeUnit.SECONDS.sleep(1);
    }

    @WithSpan
    public void doSomeWorkNewSpan() throws InterruptedException {
        logger.info("Doing some work In new span");
        Span span = Span.current();

        span.setAttribute("attribute.a1", "some value");

        span.addEvent("app.processing1.start", atttributes("123"));
        doSomeWorkNestedSpan();
        span.addEvent("app.processing1.end", atttributes("123"));
    }

    @WithSpan
    public void doSomeWorkNestedSpan() throws InterruptedException {
        logger.info("Doing some work In Nested span");
        Span span = Span.current();

        span.setAttribute("attribute.a2", "some value");

        span.addEvent("app.processing2.start", atttributes("321"));
        TimeUnit.SECONDS.sleep(1);
        span.addEvent("app.processing2.end", atttributes("321"));
    }

    private Attributes atttributes(String id) {
        return Attributes.of(AttributeKey.stringKey("app.id"), "" + id);
    }
}

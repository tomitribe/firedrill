/*
 * Tomitribe Confidential
 *
 * Copyright Tomitribe Corporation. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.tomitribe.firedrill.rs;

import com.tomitribe.firedrill.util.Chance;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.Options;
import org.tomitribe.util.Duration;

import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.function.Function;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

@Options
public class Time implements Function<Response.ResponseBuilder, Response.ResponseBuilder> {

    private final Duration min;
    private final Duration max;

    public Time(@Option("min-time") Duration min, @Option("max-time") Duration max) {
        this.min = Optional.ofNullable(min).orElse(new Duration("0"));
        this.max = Optional.ofNullable(max).orElse(this.min);

        if (min.getTime(NANOSECONDS) < 0) {
            throw new IllegalStateException(String.format("Min and Max must be zero or more: min='%s' max='%s'", min, max));
        }

        if (min.getTime(NANOSECONDS) > max.getTime(NANOSECONDS)) {
            throw new IllegalStateException(String.format("Min cannot be greater than Max: min='%s' max='%s'", min, max));
        }
    }

    @Override
    public Response.ResponseBuilder apply(Response.ResponseBuilder responseBuilder) {

        final long wait = Chance.chance.get().range(min.getTime(MILLISECONDS), max.getTime(MILLISECONDS));

        try {
            Thread.sleep(wait);
        } catch (InterruptedException e) {
            Thread.interrupted();
        }

        return responseBuilder;
    }
}

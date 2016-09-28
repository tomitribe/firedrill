/*
 * Tomitribe Confidential
 *
 * Copyright Tomitribe Corporation. 2014
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
import org.tomitribe.util.Size;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.util.Optional;
import java.util.function.Function;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.tomitribe.util.SizeUnit.BYTES;

public class Responses {

    @Options
    public static class ResponseCode implements Function<Response.ResponseBuilder, Response.ResponseBuilder> {

        private final int code;

        public ResponseCode(@Option("response-code") int code) {
            this.code = code;
        }

        @Override
        public Response.ResponseBuilder apply(Response.ResponseBuilder responseBuilder) {
            return responseBuilder.status(code);
        }
    }

    @Options
    public static class Bytes implements Function<Response.ResponseBuilder, Response.ResponseBuilder> {

        private final Size min;
        private final Size max;

        public Bytes(@Option("min-bytes") Size min, @Option("max-bytes") Size max) {
            this.min = Optional.ofNullable(min).orElse(new Size("0 bytes"));
            this.max = Optional.ofNullable(max).orElse(this.min);

            if (min.getSize(BYTES) < 0) {
                throw new IllegalStateException(String.format("Min and Max must be zero or more: min='%s' max='%s'", min, max));
            }

            if (min.getSize(BYTES) > max.getSize(BYTES)) {
                throw new IllegalStateException(String.format("Min cannot be greater than Max: min='%s' max='%s'", min, max));
            }
        }

        @Override
        public Response.ResponseBuilder apply(Response.ResponseBuilder responseBuilder) {
            long bytes = Chance.chance.get().range(min.getSize(BYTES), max.getSize(BYTES));
            return responseBuilder.entity(bytes(bytes));
        }

        public static StreamingOutput bytes(final long bytes) {
            return (StreamingOutput) outputStream -> {
                final byte[] data = new byte[1024];
                long remaining = bytes;
                for (; remaining > data.length; remaining -= data.length) {
                    outputStream.write(data);
                }
                outputStream.write(new byte[(int) remaining]);
            };
        }
    }

    @Options
    public static class Time implements Function<Response.ResponseBuilder, Response.ResponseBuilder> {

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
}

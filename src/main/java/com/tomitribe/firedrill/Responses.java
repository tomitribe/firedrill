/*
 * Tomitribe Confidential
 *
 * Copyright Tomitribe Corporation. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.tomitribe.firedrill;

import org.tomitribe.util.Duration;
import org.tomitribe.util.Size;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.tomitribe.util.SizeUnit.BYTES;

public class Responses {

    public static long pause(String min, String max) {
        return pause(new Duration(min).getTime(MILLISECONDS), new Duration(max).getTime(MILLISECONDS));
    }

    public static long pause(long min, long max) {
        return Chance.chance.get().range(min, max);
    }

    public static long bytes(String min, String max) {
        return pause(new Size(min).getSize(BYTES), new Size(max).getSize(BYTES));
    }

    public static long bytes(long min, long max) {
        return Chance.chance.get().range(min, max);
    }

    public static Response response(Response.Status code, long pause, long bytes) {
        try {
            Thread.sleep(pause);
        } catch (InterruptedException e) {
            Thread.interrupted();
        }

        return Response.status(code).entity(bytes(bytes)).build();
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

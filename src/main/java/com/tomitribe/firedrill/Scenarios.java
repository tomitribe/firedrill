/*
 * Tomitribe Confidential
 *
 * Copyright Tomitribe Corporation. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.tomitribe.firedrill;

import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.StreamingOutput;
import org.tomitribe.sheldon.api.CommandListener;
import org.tomitribe.util.PrintString;

import javax.ejb.MessageDriven;
import javax.ws.rs.core.Response;
import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@MessageDriven
@Command("scenario")
public class Scenarios implements CommandListener {

    final Map<BucketId, Bucket> buckets = new ConcurrentHashMap<>();

    // list, copy, modify, add, remove

    @Command
    public StreamingOutput list() {
        return outputStream -> {
            final PrintStream stream = new PrintStream(outputStream);
            for (final Bucket bucket : buckets.values()) {
                print(stream, bucket);
            }
        };
    }

    private static void print(final PrintStream stream, final Bucket bucket) {
        final BucketId id = bucket.getId();
        stream.printf("%20s %40s%n", id, bucket);
    }

    @Command
    public String add(Pattern pattern) {
        final Bucket<Response, Response> bucket = new Bucket<Response, Response>(pattern);
        buckets.put(bucket.getId(), bucket);
        return print(bucket);
    }

    @Command
    public String copy(BucketId bucketId, Pattern pattern) {
        final Bucket bucket = buckets.get(bucketId);
        if (bucket == null) {
            return "No such bucket " + bucketId;
        }
        final Bucket copy = bucket.copy(pattern);
        buckets.put(copy.getId(), copy);
        return print(copy);
    }

    @Command
    public String remove(BucketId bucketId) {
        final Bucket bucket = buckets.remove(bucketId);
        if (bucket == null) {
            return "No such bucket " + bucketId;
        }
        return print(bucket);
    }

    private static String print(Bucket<Response, Response> bucket) {
        final PrintString stream = new PrintString();
        print(stream, bucket);
        return stream.toString();
    }
}

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

    @Command
    public StreamingOutput detail(BucketId bucketId) {
        return outputStream -> {
            final PrintStream out = new PrintStream(outputStream);

            final Bucket bucket = buckets.get(bucketId);
            if (bucket == null) {
                out.println("No such scenario " + bucketId);
                return;
            }

            printScenarioDetails(out, bucket);
        };
    }

    @Command
    public StreamingOutput add(Condition condition) {
        final Bucket<Response, Response> bucket = new Bucket<>(condition);
        buckets.put(bucket.getId(), bucket);
        return outputStream -> {
            printScenarioDetails(new PrintStream(outputStream), bucket);
        };
    }

    @Command
    public String update(BucketId bucketId, Condition condition) {
        final Bucket bucket = buckets.get(bucketId);

        if (bucket == null) {
            return "No such scenario " + bucketId;
        }

        final Bucket updated = bucket.update(condition);
        buckets.put(updated.getId(), updated);

        final PrintString out = new PrintString();
        printScenarioDetails(out, updated);

        return out.toString();
    }

    @Command
    public String copy(BucketId bucketId, Condition pattern) {
        final Bucket bucket = buckets.get(bucketId);
        if (bucket == null) {
            return "No such scenario " + bucketId;
        }
        final Bucket copy = bucket.copy(pattern);
        buckets.put(copy.getId(), copy);
        return print(copy);
    }

    @Command
    public String remove(BucketId bucketId) {
        final Bucket bucket = buckets.remove(bucketId);
        if (bucket == null) {
            return "No such scenario " + bucketId;
        }
        return print(bucket);
    }

    private static String print(Bucket<Response, Response> bucket) {
        final PrintString stream = new PrintString();
        print(stream, bucket);
        return stream.toString();
    }

    private static void printScenarioDetails(PrintStream out, Bucket bucket) {
        out.println("Scenario " + bucket.getId());
        out.println();

        final Condition condition = bucket.getCondition();
        for (final Map.Entry<String, Pattern> entry : condition.getPatterns().entrySet()) {
            out.printf("%20s = %s\n", entry.getKey(), entry.getValue());
        }
    }

    private static void print(final PrintStream stream, final Bucket bucket) {
        final BucketId id = bucket.getId();
        stream.printf("%20s %40s%n", id, bucket);
    }
}

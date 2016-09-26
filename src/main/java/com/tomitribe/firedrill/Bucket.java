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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Bucket<T, R> {

    private final BucketId id;
    private final Pattern pattern;
    private final List<Function<T, R>> functions = new CopyOnWriteArrayList<>();

    public Bucket(final Pattern pattern) {
        this.id = BucketId.generate();
        this.pattern = pattern;
    }

    public BucketId getId() {
        return id;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public List<Function<T, R>> getFunctions() {
        return functions;
    }

    public Bucket copy(final Pattern pattern) {
        final Bucket<T, R> bucket = new Bucket<T, R>(pattern);
        bucket.functions.addAll(this.functions);
        return bucket;
    }
}

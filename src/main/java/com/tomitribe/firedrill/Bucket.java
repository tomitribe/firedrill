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

public class Bucket<T, R> {

    private final BucketId id;
    private final Condition condition;
    private final List<Function<T, R>> functions = new CopyOnWriteArrayList<>();

    private Bucket(Bucket<T, R> bucket, Condition condition) {
        this.condition = condition;
        this.id = bucket.id;
        this.functions.addAll(bucket.functions);
    }

    public Bucket(Condition condition) {
        this.condition = condition;
        this.id = BucketId.generate();
    }

    public Condition getCondition() {
        return condition;
    }

    public BucketId getId() {
        return id;
    }

    public List<Function<T, R>> getFunctions() {
        return functions;
    }

    public Bucket copy(Condition condition) {
        final Bucket<T, R> bucket = new Bucket<T, R>(condition);
        bucket.functions.addAll(this.functions);
        return bucket;
    }

    public Bucket update(Condition condition) {
        return new Bucket(this, this.condition.merge(condition));
    }
}

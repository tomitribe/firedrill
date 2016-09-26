/*
 * Tomitribe Confidential
 *
 * Copyright Tomitribe Corporation. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.tomitribe.firedrill;

import org.tomitribe.util.Base32;
import org.tomitribe.util.Longs;
import org.tomitribe.util.hash.Slice;
import org.tomitribe.util.hash.Slices;
import org.tomitribe.util.hash.XxHash64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.regex.Pattern;

public class BucketId {

    private static final String prefix = "sc-";
    private static final Pattern format = Pattern.compile(prefix + "[a-z0-9]{13}");
    private final String id;

    private BucketId(String id) {
        this.id = id;
    }

    public static BucketId generate() {

        final byte[] bytes;

        try {
            // Generate some Random Data
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.getProperties().store(out, "" + System.currentTimeMillis() + new Random().nextDouble());
            out.flush();

            // XxHash64 hash it
            final byte[] array = out.toByteArray();
            final Slice data = Slices.wrappedBuffer(array);
            final long hash = XxHash64.hash(data);
            bytes = Longs.toBytes(hash);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        // Base32 encode it
        return new BucketId(prefix + Base32.encode(bytes).replaceAll("=", "").toLowerCase());
    }

    public static BucketId parse(final String id) {
        if (id == null) throw new IllegalArgumentException("id cannot be null");
        if (!format.matcher(id).matches()) throw new IllegalArgumentException("Invalid id format. Expecting " + format.pattern());
        return new BucketId(id);
    }

    public String get() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BucketId that = (BucketId) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id;
    }
}

/*
 * Tomitribe Confidential
 *
 * Copyright Tomitribe Corporation. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.tomitribe.firedrill.util;

import java.util.Random;

public class Chance {

    public static final ThreadLocal<Chance> chance = new ThreadLocal<Chance>() {
        @Override
        protected Chance initialValue() {
            return new Chance();
        }
    };

    private final Random random = new Random();

    public int range(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public long range(long min, long max) {
        final long range = (max - min) + 1;
        if (range > Integer.MAX_VALUE) {
            throw new IllegalStateException("Min and Max cannot be further than Integer.MAX_VALUE apart");
        }
        return random.nextInt((int) range) + min;
    }

    public boolean in(int in, int of) {
        final int result = random.nextInt(of);
        return result <= in;
    }
}

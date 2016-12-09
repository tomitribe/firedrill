/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tomitribe.firedrill.util;

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

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Pattern
 * Hash
 * RandomArrayList of Functions
 * @param <T>
 */
public class WeightedRandomResult<T> implements Supplier<T> {
    final Random random = new Random();

    final List<T> statusCodes = new ArrayList<>();

    public WeightedRandomResult(final T... statusCodes) {
        Collections.addAll(this.statusCodes, statusCodes);
    }

    public WeightedRandomResult(final Collection<T> statusCodes) {
        this.statusCodes.addAll(statusCodes);
    }

    public T get() {
        final Random random = new Random();

        final int i = random.nextInt(statusCodes.size());
        return statusCodes.get(i);
    }
}

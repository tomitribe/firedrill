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
package org.tomitribe.firedrill;

import org.tomitribe.firedrill.util.WeightedRandomResult;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class ScenarioBuilder {

    final List<Supplier<Response>> behaviors = new ArrayList<>();

    public static void technique1() {

        final Runnable spike = () -> {
            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        final Runnable slow = () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        final Runnable goodBehavior = () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        final WeightedRandomResult<Runnable> behavior = new WeightedRandomResult<Runnable>(
                goodBehavior,
                goodBehavior,
                goodBehavior,
                goodBehavior,
                goodBehavior,
                goodBehavior,
                goodBehavior,
                goodBehavior,
                goodBehavior,
                goodBehavior,
                goodBehavior,
                goodBehavior,
                goodBehavior,
                goodBehavior,
                goodBehavior,
                goodBehavior,
                goodBehavior,
                goodBehavior,
                goodBehavior,
                slow,
                slow,
                slow,
                slow,
                spike
        );

    }

    final AtomicReference<Supplier<Response>> next = new AtomicReference<>();

    public AtomicReference<Supplier<Response>> getNext() {
        return next;
    }

    public Supplier<Response> build() {
        final WeightedRandomResult<Supplier<Response>> possibilities = new WeightedRandomResult<>(behaviors);

        return () -> {
            {
                final Supplier<Response> supplier = next.getAndSet(null);
                if (supplier != null) return supplier.get();
            }

            {
                final Supplier<Response> supplier = possibilities.get();
                if (supplier != null) return supplier.get();
            }

            throw new IllegalStateException("No supplier found");
        };
    }

    public void technique2() {

        add(1000, ScenarioBuilder::sleepOk);
        add(100, ScenarioBuilder::sortOfSlow);
        add(2, ScenarioBuilder::spike);
        add(12, () -> Response.serverError().build());
        add(12, () -> Response.status(403).build());
    }

    public ScenarioBuilder add(final int weight, final Supplier<Response> responseSupplier) {
        for (int i = 0; i < weight; i++) {
            behaviors.add(responseSupplier);
        }
        return this;
    }

    private static Response sleepOk() {
        try {
            // note we'd still want some variance in response time
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Response.ok().build();
    }

    private static Response spike() {
        try {
            // note we'd still want some variance in response time
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Response.ok().build();
    }

    private static Response sortOfSlow() {
        try {
            // note we'd still want some variance in response time
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Response.ok().build();

    }

}

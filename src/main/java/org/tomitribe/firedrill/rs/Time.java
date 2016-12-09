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
package org.tomitribe.firedrill.rs;

import org.tomitribe.firedrill.util.Chance;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.Options;
import org.tomitribe.util.Duration;

import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.function.Function;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

@Options
public class Time implements Function<Response.ResponseBuilder, Response.ResponseBuilder> {

    private final Duration min;
    private final Duration max;

    public Time(@Option("min-time") Duration min, @Option("max-time") Duration max) {
        this.min = Optional.ofNullable(min).orElse(new Duration("0"));
        this.max = Optional.ofNullable(max).orElse(this.min);

        if (min.getTime(NANOSECONDS) < 0) {
            throw new IllegalStateException(String.format("Min and Max must be zero or more: min='%s' max='%s'", min, max));
        }

        if (min.getTime(NANOSECONDS) > max.getTime(NANOSECONDS)) {
            throw new IllegalStateException(String.format("Min cannot be greater than Max: min='%s' max='%s'", min, max));
        }
    }

    @Override
    public Response.ResponseBuilder apply(Response.ResponseBuilder responseBuilder) {

        final long wait = Chance.chance.get().range(min.getTime(MILLISECONDS), max.getTime(MILLISECONDS));

        try {
            Thread.sleep(wait);
        } catch (InterruptedException e) {
            Thread.interrupted();
        }

        return responseBuilder;
    }
}

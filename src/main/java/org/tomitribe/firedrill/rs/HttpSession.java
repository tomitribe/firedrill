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

import org.tomitribe.crest.api.Options;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

// We could have done finer scenario, but for the sake of the demo, it is easier to produce this way and have a single entry point
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "http-session")
@Options
public class HttpSession implements Function<Response.ResponseBuilder, Response.ResponseBuilder> {

    private final int count;
    private final int ratePerSec;
    private final int invalidateAfterSec;

    public HttpSession(final int count, final int ratePerSec, final int invalidateAfterSec) {
        this.count = count;
        this.ratePerSec = ratePerSec;
        this.invalidateAfterSec = invalidateAfterSec;

        if (ratePerSec <= 0) {
            throw new IllegalStateException(String.format("rate per second must be higher that 0", ratePerSec));
        }
        if (count <= 0) {
            throw new IllegalStateException(String.format("count must be higher that 0", count));
        }
        if (invalidateAfterSec <= 0) {
            throw new IllegalStateException(String.format("invalidate after second must be higher that 0", invalidateAfterSec));
        }
    }

    @Override
    public Response.ResponseBuilder apply(final Response.ResponseBuilder responseBuilder) {

        final HttpServletRequest httpServletRequest = ResponseFilter.REQUESTS.get();
        if (httpServletRequest != null) {

            // very very simple algorithm here

            final List<javax.servlet.http.HttpSession> sessions = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                sessions.add(httpServletRequest.getSession(true));

                try {
                    Thread.sleep(1000 / ratePerSec);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (javax.servlet.http.HttpSession session : sessions) {
                session.invalidate();
            }
        }

        return responseBuilder;
    }
}

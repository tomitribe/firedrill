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

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.Response;
import java.util.function.Function;

/**
 * Simple RequestScoped bean eliminates needing to store the
 * current Function in a thread local.
 *
 * The beans that want to augment their behavior by calling a scenario's
 * function will simply have this bean injected so they can access the
 * function they should call.
 */
@RequestScoped
public class ResponseFunction {

    private Function<Response.ResponseBuilder, Response.ResponseBuilder> function;

    public void setFunction(Function<Response.ResponseBuilder, Response.ResponseBuilder> function) {
        this.function = function;
    }

    public Function<Response.ResponseBuilder, Response.ResponseBuilder> getFunction() {
        return function;
    }
}

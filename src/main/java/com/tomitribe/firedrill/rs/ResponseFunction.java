/*
 * Tomitribe Confidential
 *
 * Copyright Tomitribe Corporation. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.tomitribe.firedrill.rs;

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

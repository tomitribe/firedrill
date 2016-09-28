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

import com.tomitribe.firedrill.Scenarios;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class ResponseScenarios {

    private final Scenarios<Response.ResponseBuilder> scenarios = new Scenarios<>();

    public Scenarios<Response.ResponseBuilder> getScenarios() {
        return scenarios;
    }

}

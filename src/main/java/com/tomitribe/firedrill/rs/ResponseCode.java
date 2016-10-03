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

import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.Options;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.util.function.Function;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@Options
public class ResponseCode implements Function<Response.ResponseBuilder, Response.ResponseBuilder> {

    @XmlAttribute
    private final int code;

    public ResponseCode() {
        code = 0;
    }

    public ResponseCode(@Option("response-code") int code) {
        this.code = code;
    }

    @Override
    public Response.ResponseBuilder apply(Response.ResponseBuilder responseBuilder) {
        return responseBuilder.status(code);
    }
}

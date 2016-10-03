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

import com.tomitribe.firedrill.util.Chance;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.Options;
import org.tomitribe.util.Size;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Optional;
import java.util.function.Function;

import static org.tomitribe.util.SizeUnit.BYTES;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "bytes")
@Options
public class Bytes implements Function<Response.ResponseBuilder, Response.ResponseBuilder> {

    @XmlAttribute
    private final Size min;

    @XmlAttribute
    private final Size max;

    public Bytes() {
        min = null;
        max = null;
    }

    public Bytes(@Option("min-bytes") Size min, @Option("max-bytes") Size max) {
        this.min = Optional.ofNullable(min).orElse(new Size("0 bytes"));
        this.max = Optional.ofNullable(max).orElse(this.min);

        if (min.getSize(BYTES) < 0) {
            throw new IllegalStateException(String.format("Min and Max must be zero or more: min='%s' max='%s'", min, max));
        }

        if (min.getSize(BYTES) > max.getSize(BYTES)) {
            throw new IllegalStateException(String.format("Min cannot be greater than Max: min='%s' max='%s'", min, max));
        }
    }

    @Override
    public Response.ResponseBuilder apply(Response.ResponseBuilder responseBuilder) {
        long bytes = Chance.chance.get().range(min.getSize(BYTES), max.getSize(BYTES));
        return responseBuilder.entity(bytes(bytes));
    }

    public static StreamingOutput bytes(final long bytes) {
        return (StreamingOutput) outputStream -> {
            final byte[] data = new byte[1024];
            long remaining = bytes;
            for (; remaining > data.length; remaining -= data.length) {
                outputStream.write(data);
            }
            outputStream.write(new byte[(int) remaining]);
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bytes bytes = (Bytes) o;

        if (!min.equals(bytes.min)) return false;
        if (!max.equals(bytes.max)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = min.hashCode();
        result = 31 * result + max.hashCode();
        return result;
    }
}

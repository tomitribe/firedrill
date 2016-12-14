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

import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.Options;
import org.tomitribe.firedrill.util.Chance;
import org.tomitribe.util.IO;
import org.tomitribe.util.Size;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.function.Function;

import static org.tomitribe.util.SizeUnit.BYTES;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "disk-fill")
@Options
public class DiskFill implements Function<Response.ResponseBuilder, Response.ResponseBuilder> {

    @XmlAttribute
    private final Size min;

    @XmlAttribute
    private final Size max;

    public DiskFill() {
        min = null;
        max = null;
    }

    public DiskFill(@Option("min-bytes") Size min, @Option("max-bytes") Size max) {
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
        try {
            final File tempFile = File.createTempFile("firedrill-", ".bytes");
            final OutputStream outputStream = IO.write(tempFile);
            final byte[] data = new byte[1024];
            long remaining = bytes;
            for (; remaining > data.length; remaining -= data.length) {
                outputStream.write(data);
            }
            outputStream.write(new byte[(int) remaining]);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseBuilder;
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

        DiskFill bytes = (DiskFill) o;

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

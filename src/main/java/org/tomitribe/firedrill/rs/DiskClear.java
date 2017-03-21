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
import org.tomitribe.util.IO;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "disk-clear")
@Options
public class DiskClear implements Function<Response.ResponseBuilder, Response.ResponseBuilder> {

    public DiskClear() {
    }

    public boolean matches(File file) {
        final String name = file.getName();
        return name.startsWith("firedrill-") && name.endsWith(".bytes");
    }

    @Override
    public Response.ResponseBuilder apply(Response.ResponseBuilder responseBuilder) {
        try {
            final File tempFile = File.createTempFile("firedrill-", ".bytes");
            Stream.of(tempFile.getParentFile().listFiles())
                    .filter(this::matches)
                    .forEach(IO::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseBuilder;
    }
}

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

import org.tomitribe.util.Base32;
import org.tomitribe.util.Longs;
import org.tomitribe.util.hash.Slice;
import org.tomitribe.util.hash.Slices;
import org.tomitribe.util.hash.XxHash64;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.regex.Pattern;

@XmlJavaTypeAdapter(ScenarioId.Adapter.class)
public class ScenarioId {

    private static final String prefix = "sc-";
    private static final Pattern format = Pattern.compile(prefix + "[a-z0-9]{13}");
    private final String id;

    private ScenarioId(String id) {
        this.id = id;
    }

    public static ScenarioId generate() {

        final byte[] bytes;

        try {
            // Generate some Random Data
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.getProperties().store(out, "" + System.currentTimeMillis() + new Random().nextDouble());
            out.flush();

            // XxHash64 hash it
            final byte[] array = out.toByteArray();
            final Slice data = Slices.wrappedBuffer(array);
            final long hash = XxHash64.hash(data);
            bytes = Longs.toBytes(hash);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        // Base32 encode it
        return new ScenarioId(prefix + Base32.encode(bytes).replaceAll("=", "").toLowerCase());
    }

    public static ScenarioId parse(final String id) {
        if (id == null) throw new IllegalArgumentException("id cannot be null");
        if (!format.matcher(id).matches()) throw new IllegalArgumentException("Invalid id format. Expecting " + format.pattern());
        return new ScenarioId(id);
    }

    public String get() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScenarioId that = (ScenarioId) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id;
    }

    public static class Adapter extends XmlAdapter<String, ScenarioId> {

        @Override
        public ScenarioId unmarshal(String v) throws Exception {
            return new ScenarioId(v);
        }

        @Override
        public String marshal(ScenarioId v) throws Exception {
            return v.toString();
        }
    }
}

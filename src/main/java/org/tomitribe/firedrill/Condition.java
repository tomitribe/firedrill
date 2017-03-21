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

import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.Options;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

@Options
@XmlJavaTypeAdapter(Condition.Adapter.class)
@XmlAccessorType(XmlAccessType.FIELD)
public class Condition {

    private final Map<String, Pattern> patterns = new TreeMap<>();

    public Condition(
            @Option("servicePath") Pattern servicePath,
            @Option("uri") Pattern uri,
            @Option("requestBytes") Pattern requestBytes,
            @Option("contentType") Pattern contentType,
            @Option("httpMethod") Pattern httpMethod,
            @Option("serviceName") Pattern serviceName,
            @Option("protocol") Pattern protocol,
            @Option("protocolVersion") Pattern protocolVersion,
            @Option("authResult") Pattern authResult,
            @Option("authErrorCode") Pattern authErrorCode,
            @Option("username") Pattern username,
            @Option("path") Pattern path,
            @Option("grantType") Pattern grantType,
            @Option("applicationName") Pattern applicationName,
            @Option("clientIp") Pattern clientIp,
            @Option("clientId") Pattern clientId,
            @Option("serverIp") Pattern serverIp,
            @Option("serverName") Pattern serverName,
            @Option("authType") Pattern authType,
            @Option("datacenter") Pattern datacenter) {

        servicePath(servicePath);
        uri(uri);
        requestBytes(requestBytes);
        contentType(contentType);
        httpMethod(httpMethod);
        serviceName(serviceName);
        protocol(protocol);
        protocolVersion(protocolVersion);
        authResult(authResult);
        authErrorCode(authErrorCode);
        username(username);
        path(path);
        grantType(grantType);
        applicationName(applicationName);
        clientIp(clientIp);
        clientId(clientId);
        serverIp(serverIp);
        serverName(serverName);
        authType(authType);
        datacenter(datacenter);
    }

    public boolean matches(final Map<String, String> map) {
        for (final Map.Entry<String, Pattern> entry : patterns.entrySet()) {

            final Pattern pattern = entry.getValue();
            final String value = map.get(entry.getKey());

            if (value == null || !pattern.matcher(value).matches()) return false;
        }

        return true;
    }

    public static Condition from(Map<String, Pattern> map) {
        final Condition merged = create();
        merged.patterns.putAll(map);
        return merged;
    }

    /**
     * Currently Crest does not support a class having two constructors
     * So a private no-arg constructor is not possible.
     */
    public static Condition create() {
        return new Condition(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public Map<String, Pattern> getPatterns() {
        return Collections.unmodifiableMap(patterns);
    }

    public Condition merge(Condition condition) {
        final Map<String, Pattern> map = new HashMap<>(patterns);
        map.putAll(condition.patterns);

        final Iterator<Map.Entry<String, Pattern>> iterator = map.entrySet().iterator();

        /**
         * Remove any entries that we count as "null"
         * Effectively those are entries the user has set to empty string
         * This is how we've designed that patterns can be removed
         */
        while (iterator.hasNext()) {
            Map.Entry<String, Pattern> entry = iterator.next();
            if ("".equals(entry.getValue().pattern())) {
                iterator.remove();
            }
        }

        // Crest can't handle a no-arg constructor mixed in with the @Option annotated constructor
        final Condition merged = create();
        merged.patterns.putAll(map);
        return merged;
    }

    public Condition servicePath(Pattern servicePath) {
        return update("servicePath", servicePath);
    }

    public Condition uri(Pattern uri) {
        return update("uri", uri);
    }

    public Condition requestBytes(Pattern requestBytes) {
        return update("requestBytes", requestBytes);
    }

    public Condition contentType(Pattern contentType) {
        return update("contentType", contentType);
    }

    public Condition httpMethod(Pattern httpMethod) {
        return update("httpMethod", httpMethod);
    }

    public Condition serviceName(Pattern serviceName) {
        return update("serviceName", serviceName);
    }

    public Condition protocol(Pattern protocol) {
        return update("protocol", protocol);
    }

    public Condition protocolVersion(Pattern protocolVersion) {
        return update("protocolVersion", protocolVersion);
    }

    public Condition authResult(Pattern authResult) {
        return update("authResult", authResult);
    }

    public Condition authErrorCode(Pattern authErrorCode) {
        return update("authErrorCode", authErrorCode);
    }

    public Condition username(Pattern username) {
        return update("username", username);
    }

    public Condition path(Pattern path) {
        return update("path", path);
    }

    public Condition grantType(Pattern grantType) {
        return update("grantType", grantType);
    }

    public Condition applicationName(Pattern applicationName) {
        return update("applicationName", applicationName);
    }

    public Condition clientIp(Pattern clientIp) {
        return update("clientIp", clientIp);
    }

    public Condition clientId(Pattern clientId) {
        return update("clientId", clientId);
    }

    public Condition serverIp(Pattern serverIp) {
        return update("serverIp", serverIp);
    }

    public Condition serverName(Pattern serverName) {
        return update("serverName", serverName);
    }

    public Condition authType(Pattern authType) {
        return update("authType", authType);
    }

    public Condition datacenter(Pattern datacenter) {
        return update("datacenter", datacenter);
    }

    private Condition update(String key, Pattern pattern) {
        if (pattern != null) {
            patterns.put(key, pattern);
        } else {
            patterns.remove(key);
        }
        return this;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Adapter extends XmlAdapter<Adapter, Condition> {

        final List<When> when = new ArrayList<>();

        @Override
        public Condition unmarshal(Adapter v) throws Exception {
            final Condition condition = create();

            for (final When w : v.when) {
                condition.patterns.put(w.key, Pattern.compile(w.matches));
            }

            return condition;
        }

        @Override
        public Adapter marshal(Condition v) throws Exception {
            final Adapter adapter = new Adapter();
            for (final Map.Entry<String, Pattern> entry : v.patterns.entrySet()) {
                adapter.when.add(new When(entry.getKey(), entry.getValue()));
            }
            return adapter;
        }
    }

    @XmlRootElement
    @XmlAccessorType
    public static class When {

        @XmlAttribute
        private String key;

        @XmlAttribute
        private String matches;

        public When() {
        }

        public When(String key, Pattern matches) {
            this.key = key;
            this.matches = matches.pattern();
        }
    }
}

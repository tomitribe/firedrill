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
        if (servicePath != null) patterns.put("servicePath", servicePath);
        else patterns.remove("servicePath");
        return this;
    }

    public Condition uri(Pattern uri) {
        if (uri != null) patterns.put("uri", uri);
        else patterns.remove("uri");
        return this;
    }

    public Condition requestBytes(Pattern requestBytes) {
        if (requestBytes != null) patterns.put("requestBytes", requestBytes);
        else patterns.remove("requestBytes");
        return this;
    }

    public Condition contentType(Pattern contentType) {
        if (contentType != null) patterns.put("contentType", contentType);
        else patterns.remove("contentType");
        return this;
    }

    public Condition httpMethod(Pattern httpMethod) {
        if (httpMethod != null) patterns.put("httpMethod", httpMethod);
        else patterns.remove("httpMethod");
        return this;
    }

    public Condition serviceName(Pattern serviceName) {
        if (serviceName != null) patterns.put("serviceName", serviceName);
        else patterns.remove("serviceName");
        return this;
    }

    public Condition protocol(Pattern protocol) {
        if (protocol != null) patterns.put("protocol", protocol);
        else patterns.remove("protocol");
        return this;
    }

    public Condition protocolVersion(Pattern protocolVersion) {
        if (protocolVersion != null) patterns.put("protocolVersion", protocolVersion);
        else patterns.remove("protocolVersion");
        return this;
    }

    public Condition authResult(Pattern authResult) {
        if (authResult != null) patterns.put("authResult", authResult);
        else patterns.remove("authResult");
        return this;
    }

    public Condition authErrorCode(Pattern authErrorCode) {
        if (authErrorCode != null) patterns.put("authErrorCode", authErrorCode);
        else patterns.remove("authErrorCode");
        return this;
    }

    public Condition username(Pattern username) {
        if (username != null) patterns.put("username", username);
        else patterns.remove("username");
        return this;
    }

    public Condition path(Pattern path) {
        if (path != null) patterns.put("path", path);
        else patterns.remove("path");
        return this;
    }

    public Condition grantType(Pattern grantType) {
        if (grantType != null) patterns.put("grantType", grantType);
        else patterns.remove("grantType");
        return this;
    }

    public Condition applicationName(Pattern applicationName) {
        if (applicationName != null) patterns.put("applicationName", applicationName);
        else patterns.remove("applicationName");
        return this;
    }

    public Condition clientIp(Pattern clientIp) {
        if (clientIp != null) patterns.put("clientIp", clientIp);
        else patterns.remove("clientIp");
        return this;
    }

    public Condition clientId(Pattern clientId) {
        if (clientId != null) patterns.put("clientId", clientId);
        else patterns.remove("clientId");
        return this;
    }

    public Condition serverIp(Pattern serverIp) {
        if (serverIp != null) patterns.put("serverIp", serverIp);
        else patterns.remove("serverIp");
        return this;
    }

    public Condition serverName(Pattern serverName) {
        if (serverName != null) patterns.put("serverName", serverName);
        else patterns.remove("serverName");
        return this;
    }

    public Condition authType(Pattern authType) {
        if (authType != null) patterns.put("authType", authType);
        else patterns.remove("authType");
        return this;
    }

    public Condition datacenter(Pattern datacenter) {
        if (datacenter != null) patterns.put("datacenter", datacenter);
        else patterns.remove("datacenter");
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

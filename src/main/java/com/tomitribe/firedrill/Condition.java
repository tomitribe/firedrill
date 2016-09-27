/*
 * Tomitribe Confidential
 *
 * Copyright Tomitribe Corporation. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.tomitribe.firedrill;

import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.Options;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

@Options
public class Condition {

    private final Map<String, Pattern> patterns = new TreeMap<>();

    public Condition(
            @Option("username") Pattern username,
            @Option("method") Pattern method,
            @Option("path") Pattern path,
            @Option("grantType") Pattern grantType,
            @Option("applicationName") Pattern applicationName,
            @Option("clientIp") Pattern clientIp,
            @Option("clientId") Pattern clientId,
            @Option("serverIp") Pattern serverIp,
            @Option("serverName") Pattern serverName,
            @Option("authType") Pattern authType,
            @Option("datacenter") Pattern datacenter) {

        if (username != null) patterns.put("username", username);
        if (method != null) patterns.put("method", method);
        if (path != null) patterns.put("path", path);
        if (grantType != null) patterns.put("grantType", grantType);
        if (applicationName != null) patterns.put("applicationName", applicationName);
        if (clientIp != null) patterns.put("clientIp", clientIp);
        if (clientId != null) patterns.put("clientId", clientId);
        if (serverIp != null) patterns.put("serverIp", serverIp);
        if (serverName != null) patterns.put("serverName", serverName);
        if (authType != null) patterns.put("authType", authType);
        if (datacenter != null) patterns.put("datacenter", datacenter);
    }

    public boolean matches(final Map<String, String> map) {
        for (final Map.Entry<String, Pattern> entry : patterns.entrySet()) {

            final Pattern pattern = entry.getValue();
            final String value = map.get(entry.getKey());

            if (value == null || !pattern.matcher(value).matches()) return false;
        }

        return true;
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
        final Condition merged = new Condition(null, null, null, null, null, null, null, null, null, null, null);
        merged.patterns.putAll(map);
        return merged;
    }
}

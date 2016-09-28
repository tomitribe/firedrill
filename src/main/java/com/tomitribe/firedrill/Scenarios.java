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

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;


public class Scenarios<T> {

    final Map<ScenarioId, Scenario<T, T>> buckets = new ConcurrentHashMap<>();

    public Collection<Scenario<T, T>> list() {
        return buckets.values();
    }

    public Scenario<T, T> get(final ScenarioId scenarioId) throws NoSuchElementException {

        final Scenario<T, T> scenario = buckets.get(scenarioId);
        if (scenario == null) {
            throw new NoSuchElementException(scenarioId.get());
        }

        return scenario;
    }

    public Scenario<T, T> add(final Condition condition) {
        final Scenario<T, T> scenario = new Scenario<>(condition);
        buckets.put(scenario.getId(), scenario);
        return scenario;
    }

    public Scenario<T, T> update(ScenarioId scenarioId, Condition condition) throws NoSuchElementException {
        final Scenario<T, T> scenario = get(scenarioId);
        final Scenario<T, T> updated = scenario.update(condition);
        buckets.put(updated.getId(), updated);
        return updated;
    }

    public Scenario<T, T> copy(ScenarioId scenarioId, Condition pattern) throws NoSuchElementException {
        final Scenario<T, T> scenario = get(scenarioId);
        final Scenario<T, T> copy = scenario.copy(pattern);
        buckets.put(copy.getId(), copy);
        return copy;
    }

    public Scenario<T, T> remove(ScenarioId scenarioId) throws NoSuchElementException {
        final Scenario<T, T> scenario = buckets.remove(scenarioId);
        if (scenario == null) {
            throw new NoSuchElementException(scenarioId.get());
        }
        return scenario;
    }
}

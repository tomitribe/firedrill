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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(CompositeFunction.class)
public class Scenarios<T> {

//    @XmlJavaTypeAdapter(Xml.class)
    final Map<ScenarioId, Scenario<T, T>> scenarios = new ConcurrentHashMap<>();

    public Scenarios() {
    }

    public Collection<Scenario<T, T>> list() {
        final ArrayList<Scenario<T, T>> scenarios = new ArrayList<>(this.scenarios.values());
        Collections.sort(scenarios);
        return scenarios;
    }

    public Scenario<T, T> get(final ScenarioId scenarioId) throws NoSuchElementException {

        final Scenario<T, T> scenario = this.scenarios.get(scenarioId);
        if (scenario == null) {
            throw new NoSuchElementException(scenarioId.get());
        }

        return scenario;
    }

    public Scenario<T, T> add(final Condition condition) {
        final Scenario<T, T> scenario = new Scenario<>(condition);
        this.scenarios.put(scenario.getId(), scenario);
        return scenario;
    }

    public Scenario<T, T> update(ScenarioId scenarioId, Condition condition) throws NoSuchElementException {
        final Scenario<T, T> scenario = get(scenarioId);
        final Scenario<T, T> updated = scenario.update(condition);
        this.scenarios.put(updated.getId(), updated);
        return updated;
    }

    public Scenario<T, T> copy(ScenarioId scenarioId, Condition pattern) throws NoSuchElementException {
        final Scenario<T, T> scenario = get(scenarioId);
        final Scenario<T, T> copy = scenario.copy(pattern);
        this.scenarios.put(copy.getId(), copy);
        return copy;
    }

    public Scenario<T, T> remove(ScenarioId scenarioId) throws NoSuchElementException {
        final Scenario<T, T> scenario = this.scenarios.remove(scenarioId);
        if (scenario == null) {
            throw new NoSuchElementException(scenarioId.get());
        }
        return scenario;
    }

    public void add(Scenarios<T> scenarios) {
        this.scenarios.putAll(scenarios.scenarios);
    }

    public void set(Scenarios<T> scenarios) {
        this.scenarios.clear();
        this.scenarios.putAll(scenarios.scenarios);
    }

    @XmlRootElement(name = "scenarios")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Xml extends XmlAdapter<Xml, Scenarios> {

        final List<Scenario> scenario = new ArrayList<>();

        public Xml() {
        }

        public Xml(Scenarios scenarios) {
        }

        @Override
        public Scenarios unmarshal(Xml v) throws Exception {
            final Scenarios scenarios = new Scenarios();
            for (final Scenario s : v.scenario) {
                scenarios.scenarios.put(s.getId(), s);
            }
            return scenarios;
        }

        @Override
        public Xml marshal(Scenarios v) throws Exception {
            return from(v);
        }

        public static Xml from(Scenarios v) {
            final Xml xml = new Xml();
            xml.scenario.addAll(v.scenarios.values());
            Collections.sort(xml.scenario);
            return xml;
        }

        public Scenarios getScenarios() {
            final Scenarios<Object> scenarios = new Scenarios<>();
            for (final Scenario s : scenario) {
                scenarios.scenarios.put(s.getId(), s);
            }
            return scenarios;
        }
    }

    @Override
    public String toString() {
        return "Scenarios{" +
                "scenarios=" + scenarios.size() +
                '}';
    }
}

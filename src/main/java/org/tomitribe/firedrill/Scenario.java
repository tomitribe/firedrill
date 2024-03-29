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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.regex.Pattern;

@XmlJavaTypeAdapter(Scenario.Adapter.class)
public class Scenario<T, R> implements Comparable<Scenario<T, R>>, Function<T, R> {

    /**
     * Many scenarios can match an incoming request
     *
     * We stamp the scenario itself with the date it was created
     * so that the execution order is stable.  The oldest execute
     * first and the newest run last.
     *
     * This makes the newest scenarios effectively have the final
     * word on things like final status code of the response, etc.
     */
    @XmlAttribute
    private final long created;

    /**
     * The scenario id is a generated Base32 hash that the user
     * can use to point to a scenario instance and add/remove functions
     * that could be executed should a request come in and match
     * the scenario's conditions.
     */
    @XmlAttribute
    private final ScenarioId id;

    /**
     * The Conditions is effectively a map of regex Patterns.
     * Should all the patterns match the incoming request, a
     * function is selected and executed in an @ScenarioScope
     * that identifies this exact ScenarioId
     */
    private final Condition condition;

    /**
     * When a scenario is triggered, we randomly select one item
     * from the functions list.  Things we want to happen more frequently
     * should be in the list in greater number.  Things we want to happen
     * less frequently should be in fewer numbers.
     *
     * To have a 25% chance of a 500 and a 75% chance of 200, simply
     * insert 3 identical functions that set the response code to 200
     * and insert 1 function that sets the response code to 500
     */
    private final List<Function<T, R>> functions = new CopyOnWriteArrayList<>();


    final List<Outcome> outcomes = new ArrayList<>();

    /**
     * A random number to aid in selection of the next function
     * when this scenario is triggered
     */
    @XmlTransient
    private final Random random = new Random();

    private Scenario(Scenario<T, R> scenario, Condition condition) {
        this.condition = condition;
        this.id = scenario.id;
        this.created = scenario.created;
        this.functions.addAll(scenario.functions);
    }

    private Scenario(ScenarioId id, Condition condition) {
        this.condition = condition;
        this.id = Optional.ofNullable(id).orElseGet(ScenarioId::generate);
        this.created = System.currentTimeMillis();
    }

    public Scenario(Condition condition) {
        this.condition = condition;
        this.id = ScenarioId.generate();
        this.created = System.currentTimeMillis();
    }

    public Condition getCondition() {
        return condition;
    }

    public ScenarioId getId() {
        return id;
    }

    public long getCreated() {
        return created;
    }

    public Scenario<T, R> copy(Condition condition) {
        final Scenario<T, R> scenario = new Scenario<T, R>(this.condition.merge(condition));
        scenario.functions.addAll(this.functions);
        return scenario;
    }

    public Scenario<T, R> update(Condition condition) {
        return new Scenario(this, this.condition.merge(condition));
    }

    @Override
    public R apply(T t) {
        return select().apply(t);
    }

    public Function<T, R> select() {
        final int i = random.nextInt(functions.size());
        return functions.get(i);
    }

    @Override
    public int compareTo(Scenario<T, R> o) {
        return Long.compare(this.getCreated(), o.getCreated());
    }

    public void add(int count, Function<T, R> function) {
        outcomes.add(new Outcome(count, function));
        for (int i = 0; i < count; i++) {
            functions.add(function);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Scenario<?, ?> scenario = (Scenario<?, ?>) o;

        if (!id.equals(scenario.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Scenario{" +
                "id=" + id +
                '}';
    }

    @XmlRootElement(name = "scenario")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Adapter extends XmlAdapter<Scenario.Adapter, Scenario> {

        @XmlAttribute
        private ScenarioId id = ScenarioId.generate();

        private final List<When> when = new ArrayList<>();
        private final List<Outcome> outcome = new ArrayList<>();

        public Adapter() {
        }

        @Override
        public Scenario unmarshal(Adapter v) throws Exception {
            final Map<String, Pattern> map = new TreeMap<>();

            for (final When w : v.when) {
                map.put(w.key, Pattern.compile(w.matches));
            }

            final Scenario<Object, Object> scenario = new Scenario<>(v.id, Condition.from(map));

            for (final Outcome o : v.outcome) {
                scenario.add(o.getWeight(), o.getFunction());
            }

            return scenario;
        }

        @Override
        public Adapter marshal(Scenario v) throws Exception {
            final Adapter adapter = new Adapter();
            adapter.id = v.getId();
            adapter.outcome.addAll(v.outcomes);

            for (final Map.Entry<String, Pattern> entry : v.getCondition().getPatterns().entrySet()) {
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

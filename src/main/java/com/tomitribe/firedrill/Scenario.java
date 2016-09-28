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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class Scenario<T, R> implements Comparable<Scenario<T, R>> {

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
    private final long created;

    /**
     * The scenario id is a generated Base32 hash that the user
     * can use to point to a scenario instance and add/remove functions
     * that could be executed should a request come in and match
     * the scenario's conditions.
     */
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
    private final List<Function<T, R>> functions = new ArrayList<>();

    /**
     * A random number to aid in selection of the next function
     * when this scenario is triggered
     */
    private final Random random = new Random();

    private Scenario(Scenario<T, R> scenario, Condition condition) {
        this.condition = condition;
        this.id = scenario.id;
        this.created = scenario.created;
        this.functions.addAll(scenario.functions);
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

    public List<Function<T, R>> getFunctions() {
        return Collections.unmodifiableList(functions);
    }

    public Scenario<T, R> copy(Condition condition) {
        final Scenario<T, R> scenario = new Scenario<T, R>(condition);
        scenario.functions.addAll(this.functions);
        return scenario;
    }

    public Scenario<T, R> update(Condition condition) {
        return new Scenario(this, this.condition.merge(condition));
    }

    public Function<T, R> select() {
        final int i = random.nextInt(functions.size());
        return functions.get(i);
    }

    @Override
    public int compareTo(Scenario<T, R> o) {
        return Long.compare(this.getCreated(), o.getCreated());
    }
}

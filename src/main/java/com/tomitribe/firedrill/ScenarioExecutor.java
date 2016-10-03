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

import org.tomitribe.microscoped.core.ScopeContext;

import javax.enterprise.inject.spi.BeanManager;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ScenarioExecutor<T> implements Function<T, T> {

    private final Scenarios<T> scenarios;
    private final Map<String, String> context;
    private final ScopeContext<ScenarioId> scopeContext;

    public ScenarioExecutor(BeanManager beanManager, Scenarios<T> scenarios, Map<String, String> context) {
        this.scenarios = scenarios;
        this.context = context;
        this.scopeContext = (ScopeContext<ScenarioId>) beanManager.getContext(ScenarioScoped.class);
    }

    @Override
    public T apply(T t) {

        final List<Scenario<T, T>> matchingScenarios = scenarios.list().stream()
                .filter(scenario -> scenario.getCondition().matches(context))
                .collect(Collectors.toList());

        for (final Scenario<T, T> scenario : matchingScenarios) {

            // Enter the @ScenarioScoped for this ScenarioId
            final ScenarioId oldId = scopeContext.enter(scenario.getId());

            try {
                // Execute the Scenario's randomly chosen function
                t = scenario.apply(t);
            } finally {

                // Exit the ScenarioScoped
                scopeContext.exit(oldId);
            }
        }

        return t;
    }
}

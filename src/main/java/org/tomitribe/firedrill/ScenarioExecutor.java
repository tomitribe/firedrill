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

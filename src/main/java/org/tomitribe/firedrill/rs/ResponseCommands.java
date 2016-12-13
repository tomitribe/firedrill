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
package org.tomitribe.firedrill.rs;

import org.tomitribe.firedrill.Condition;
import org.tomitribe.firedrill.Scenario;
import org.tomitribe.firedrill.ScenarioId;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Default;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.StreamingOutput;
import org.tomitribe.sheldon.api.CommandListener;
import org.tomitribe.util.PrintString;

import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.PrintStream;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.regex.Pattern;

//@MessageDriven
@Command("scenario")
public class ResponseCommands implements CommandListener {

    @Inject
    private ResponseScenarios scenarios;

    @Command
    public StreamingOutput list() {

        return outputStream -> {
            final PrintStream stream = new PrintStream(outputStream);
            for (final Scenario scenario : scenarios.getScenarios().list()) {
                print(stream, scenario);
            }
        };
    }

    @Command
    public StreamingOutput detail(ScenarioId scenarioId) {
        return outputStream -> {
            final PrintStream out = new PrintStream(outputStream);

            try {
                final Scenario scenario = scenarios.getScenarios().get(scenarioId);
                printScenarioDetails(out, scenario);
            } catch (NoSuchElementException e) {
                out.println("No such scenario " + scenarioId);
            }
        };
    }

    @Command
    public StreamingOutput add(Condition condition) {
        return outputStream -> {
            final Scenario scenario = scenarios.getScenarios().add(condition);
            printScenarioDetails(new PrintStream(outputStream), scenario);
        };
    }

    @Command
    public String update(ScenarioId scenarioId, Condition condition) {
        try {
            final Scenario updated = scenarios.getScenarios().update(scenarioId, condition);

            final PrintString out = new PrintString();
            printScenarioDetails(out, updated);
            return out.toString();
        } catch (NoSuchElementException e) {
            return "No such scenario " + scenarioId;
        }
    }

    @Command
    public String copy(ScenarioId scenarioId, Condition pattern) {
        try {
            final Scenario updated = scenarios.getScenarios().copy(scenarioId, pattern);

            final PrintString out = new PrintString();
            printScenarioDetails(out, updated);
            return out.toString();
        } catch (NoSuchElementException e) {
            return "No such scenario " + scenarioId;
        }
    }

    @Command
    public String remove(ScenarioId scenarioId) {
        try {
            return print(scenarios.getScenarios().remove(scenarioId));
        } catch (NoSuchElementException e) {
            return "No such scenario " + scenarioId;
        }
    }

    @Command
    public String response(ScenarioId scenarioId,
                           @Option("count") @Default("1") int count,
                           Time time,
                           ResponseCode code,
                           Bytes bytes) {

        try {
            final Scenario<Response.ResponseBuilder, Response.ResponseBuilder> scenario = scenarios.getScenarios().get(scenarioId);

            Function<Response.ResponseBuilder, Response.ResponseBuilder> function = Function.identity();

            if (time != null) function = function.andThen(time);
            if (bytes != null) function = function.andThen(bytes);
            if (code != null) function = function.andThen(code);

            scenario.add(count, function);

            return print(scenario);
        } catch (NoSuchElementException e) {
            return "No such scenario " + scenarioId;
        }
    }

    private static String print(Scenario scenario) {
        final PrintString stream = new PrintString();
        print(stream, scenario);
        return stream.toString();
    }

    private static void printScenarioDetails(PrintStream out, Scenario scenario) {
        out.println("Scenario " + scenario.getId());
        out.println();

        final Condition condition = scenario.getCondition();
        for (final Map.Entry<String, Pattern> entry : condition.getPatterns().entrySet()) {
            out.printf("%20s = %s\n", entry.getKey(), entry.getValue());
        }
    }

    private static void print(final PrintStream stream, final Scenario scenario) {
        final ScenarioId id = scenario.getId();
        stream.printf("%20s ", id);
        for (final Map.Entry<String, Pattern> entry : scenario.getCondition().getPatterns().entrySet()) {
            stream.printf("%s=%s  ", entry.getKey(), entry.getValue());
        }
        stream.println();
    }
}

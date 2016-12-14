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
import org.tomitribe.util.Duration;
import org.tomitribe.util.PrintString;
import org.tomitribe.util.Size;

import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.PrintStream;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.regex.Pattern;

@MessageDriven
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
                           @Option("response-code") Integer code,
                           @Option("min-time") Duration minTime,
                           @Option("max-time") Duration maxTime,
                           @Option("min-bytes") Size minBytes,
                           @Option("max-bytes") Size maxBytes) {

        try {
            final Scenario<Response.ResponseBuilder, Response.ResponseBuilder> scenario = scenarios.getScenarios().get(scenarioId);

            Function<Response.ResponseBuilder, Response.ResponseBuilder> function = Function.identity();

            if (minTime != null || maxTime != null) {
                function = function.andThen(new Time(minTime, maxTime));
            }
            if (minBytes != null || maxBytes != null) {
                function = function.andThen(new Bytes(minBytes, maxBytes));
            }
            if (code != null) {
                function = function.andThen(new ResponseCode(code));
            }

            scenario.add(count, function);

            return print(scenario);
        } catch (NoSuchElementException e) {
            return "No such scenario " + scenarioId;
        }
    }

    @Command("disk-fill")
    public String diskfill(ScenarioId scenarioId,
                           @Option("count") @Default("1") int count,
                           @Option("min-bytes") Size minBytes,
                           @Default("10mb") @Option("max-bytes") Size maxBytes) {

        try {
            final Scenario<Response.ResponseBuilder, Response.ResponseBuilder> scenario = scenarios.getScenarios().get(scenarioId);

            Function<Response.ResponseBuilder, Response.ResponseBuilder> function = Function.identity();

            if (minBytes != null || maxBytes != null) {
                function = function.andThen(new DiskFill(minBytes, maxBytes));
            }

            scenario.add(count, function);

            return print(scenario);
        } catch (NoSuchElementException e) {
            return "No such scenario " + scenarioId;
        }
    }

    @Command("memory-fill")
    public String memoryfill(ScenarioId scenarioId,
                           @Option("count") @Default("1") int count,
                           @Option("min-bytes") Size minBytes,
                           @Default("10mb") @Option("max-bytes") Size maxBytes) {

        try {
            final Scenario<Response.ResponseBuilder, Response.ResponseBuilder> scenario = scenarios.getScenarios().get(scenarioId);

            Function<Response.ResponseBuilder, Response.ResponseBuilder> function = Function.identity();

            if (minBytes != null || maxBytes != null) {
                function = function.andThen(new MemoryFill(minBytes, maxBytes));
            }

            scenario.add(count, function);

            return print(scenario);
        } catch (NoSuchElementException e) {
            return "No such scenario " + scenarioId;
        }
    }

    @Command("memory-clear")
    public String memoryclear(ScenarioId scenarioId, @Option("count") @Default("1") int count) {

        try {
            final Scenario<Response.ResponseBuilder, Response.ResponseBuilder> scenario = scenarios.getScenarios().get(scenarioId);

            Function<Response.ResponseBuilder, Response.ResponseBuilder> function = Function.identity();

            function = function.andThen(new MemoryClear());
            scenario.add(count, function);

            return print(scenario);
        } catch (NoSuchElementException e) {
            return "No such scenario " + scenarioId;
        }
    }

    @Command("disk-clear")
    public String diskclear(ScenarioId scenarioId, @Option("count") @Default("1") int count) {

        try {
            final Scenario<Response.ResponseBuilder, Response.ResponseBuilder> scenario = scenarios.getScenarios().get(scenarioId);

            Function<Response.ResponseBuilder, Response.ResponseBuilder> function = Function.identity();

            function = function.andThen(new DiskClear());
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

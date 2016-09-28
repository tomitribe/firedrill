/*
 * Tomitribe Confidential
 *
 * Copyright Tomitribe Corporation. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.tomitribe.firedrill.rs;

import com.tomitribe.firedrill.Condition;
import com.tomitribe.firedrill.Scenario;
import com.tomitribe.firedrill.ScenarioId;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.StreamingOutput;
import org.tomitribe.sheldon.api.CommandListener;
import org.tomitribe.util.PrintString;

import javax.ejb.MessageDriven;
import javax.inject.Inject;
import java.io.PrintStream;
import java.util.Map;
import java.util.NoSuchElementException;
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
            for (final Scenario scenario : scenarios.list()) {
                print(stream, scenario);
            }
        };
    }

    @Command
    public StreamingOutput detail(ScenarioId scenarioId) {
        return outputStream -> {
            final PrintStream out = new PrintStream(outputStream);

            try {
                final Scenario scenario = scenarios.get(scenarioId);
                printScenarioDetails(out, scenario);
            } catch (NoSuchElementException e) {
                out.println("No such scenario " + scenarioId);
            }
        };
    }

    @Command
    public StreamingOutput add(Condition condition) {
        return outputStream -> {
            final Scenario scenario = scenarios.add(condition);
            printScenarioDetails(new PrintStream(outputStream), scenario);
        };
    }

    @Command
    public String update(ScenarioId scenarioId, Condition condition) {
        try {
            final Scenario updated = scenarios.update(scenarioId, condition);

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
            final Scenario updated = scenarios.copy(scenarioId, pattern);

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
            return print(scenarios.remove(scenarioId));
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
        stream.printf("%20s %40s%n", id, scenario);
    }
}

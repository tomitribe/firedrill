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

import org.junit.Test;
import org.tomitribe.firedrill.CompositeFunction;
import org.tomitribe.firedrill.Condition;
import org.tomitribe.firedrill.Outcome;
import org.tomitribe.firedrill.Scenario;
import org.tomitribe.firedrill.Scenarios;
import org.tomitribe.util.IO;
import org.tomitribe.util.PrintString;
import org.tomitribe.util.Size;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.util.Collection;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

public class ScenariosXmlTest {

    @Test
    public void testXml() throws Exception {

        final Scenarios<Response.ResponseBuilder> scenarios = new Scenarios<>();

        final Scenario<Response.ResponseBuilder, Response.ResponseBuilder> scenario = scenarios.add(Condition.create()
                        .username(Pattern.compile(".*user.*"))
                        .httpMethod(Pattern.compile("method.*"))
                        .path(Pattern.compile(".*Path"))
                        .grantType(Pattern.compile("grantType.*"))
                        .applicationName(Pattern.compile("app.*"))
                        .clientIp(Pattern.compile("clientIp.*"))
                        .clientId(Pattern.compile("client id"))
                        .serverIp(Pattern.compile("server[Ii][pP]"))
                        .serverName(Pattern.compile("server Name"))
                        .authType(Pattern.compile("authTyPe"))
                        .datacenter(Pattern.compile("dataCENter"))
        );

        final Bytes bytes = new Bytes(new Size("10kb"), new Size("33mb"));
        final ResponseCode code = new ResponseCode(200);

        scenario.add(100, new CompositeFunction<>(bytes, code));
        scenario.add(12, new CompositeFunction<>(new ResponseCode(500)));

        final PrintString xmlContent = new PrintString();
        final JAXBContext jaxbContext = JAXBContext.newInstance(
                Scenarios.class,
                Scenarios.Xml.class,
                CompositeFunction.class,
                Outcome.class,
                Bytes.class,
                ResponseCode.class);

        {
            final Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(Scenarios.Xml.from(scenarios), xmlContent);

            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<scenarios>\n" +
                    "    <scenario id=\"sc-wye5zlddlp55g\">\n" +
                    "        <when key=\"applicationName\" matches=\"app.*\"/>\n" +
                    "        <when key=\"authType\" matches=\"authTyPe\"/>\n" +
                    "        <when key=\"clientId\" matches=\"client id\"/>\n" +
                    "        <when key=\"clientIp\" matches=\"clientIp.*\"/>\n" +
                    "        <when key=\"datacenter\" matches=\"dataCENter\"/>\n" +
                    "        <when key=\"grantType\" matches=\"grantType.*\"/>\n" +
                    "        <when key=\"httpMethod\" matches=\"method.*\"/>\n" +
                    "        <when key=\"path\" matches=\".*Path\"/>\n" +
                    "        <when key=\"serverIp\" matches=\"server[Ii][pP]\"/>\n" +
                    "        <when key=\"serverName\" matches=\"server Name\"/>\n" +
                    "        <when key=\"username\" matches=\".*user.*\"/>\n" +
                    "        <outcome weight=\"100\">\n" +
                    "            <composite>\n" +
                    "                <bytes min=\"10kb\" max=\"33mb\"/>\n" +
                    "                <responseCode code=\"200\"/>\n" +
                    "            </composite>\n" +
                    "        </outcome>\n" +
                    "        <outcome weight=\"12\">\n" +
                    "            <composite>\n" +
                    "                <responseCode code=\"500\"/>\n" +
                    "            </composite>\n" +
                    "        </outcome>\n" +
                    "    </scenario>\n" +
                    "</scenarios>\n", xmlContent.toString().replace(scenario.getId().toString(), "sc-wye5zlddlp55g"));
            System.out.println(xmlContent);
        }

        { // from xml
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            final Scenarios.Xml unmarshal = (Scenarios.Xml) unmarshaller.unmarshal(IO.read(xmlContent.toString()));
            final Scenarios<Response.ResponseBuilder> read = unmarshal.getScenarios();
            final Collection<Scenario<Response.ResponseBuilder, Response.ResponseBuilder>> list = read.list();
            assertEquals(1, list.size());

            final Scenario<Response.ResponseBuilder, Response.ResponseBuilder> next = list.iterator().next();
            assertEquals(scenario, next);

            final PrintString secondContent = new PrintString();
            final Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(Scenarios.Xml.from(read), secondContent);

            assertEquals(xmlContent.toString(), secondContent.toString());
            System.out.println(unmarshal);
        }
    }
}
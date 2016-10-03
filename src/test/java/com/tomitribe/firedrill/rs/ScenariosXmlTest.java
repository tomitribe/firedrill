package com.tomitribe.firedrill.rs;

import com.tomitribe.firedrill.CompositeFunction;
import com.tomitribe.firedrill.Condition;
import com.tomitribe.firedrill.Outcome;
import com.tomitribe.firedrill.Scenario;
import com.tomitribe.firedrill.Scenarios;
import org.junit.Test;
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

        final Scenario<Response.ResponseBuilder, Response.ResponseBuilder> scenario = scenarios.add(new Condition(
                Pattern.compile(".*user.*"),
                Pattern.compile("method.*"),
                Pattern.compile(".*Path"),
                Pattern.compile("grantType.*"),
                Pattern.compile("app.*"),
                Pattern.compile("clientIp.*"),
                Pattern.compile("client id"),
                Pattern.compile("server[Ii][pP]"),
                Pattern.compile("server Name"),
                Pattern.compile("authTyPe"),
                Pattern.compile("dataCENter")
        ));

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
                    "        <when key=\"method\" matches=\"method.*\"/>\n" +
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
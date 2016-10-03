package com.tomitribe.firedrill.rs;

import com.tomitribe.firedrill.CompositeFunction;
import com.tomitribe.firedrill.Condition;
import com.tomitribe.firedrill.Scenario;
import com.tomitribe.firedrill.ScenarioId;
import com.tomitribe.firedrill.Scenarios;
import org.junit.Test;
import org.tomitribe.util.PrintString;
import org.tomitribe.util.Size;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

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
                Bytes.class,
                CompositeFunction.class,
                ResponseCode.class);

        {
            final Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(scenarios, xmlContent);

            System.out.println(xmlContent);
        }
    }


}
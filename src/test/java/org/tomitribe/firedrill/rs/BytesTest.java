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

import org.tomitribe.firedrill.CompositeFunction;
import org.tomitribe.firedrill.Outcome;
import org.junit.Test;
import org.tomitribe.util.IO;
import org.tomitribe.util.PrintString;
import org.tomitribe.util.Size;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class BytesTest {

    @Test
    public void testXml() throws Exception {

        final Bytes bytes = new Bytes(new Size("10k"), new Size("20mb"));

        final PrintString xmlContent = new PrintString();
        final JAXBContext jaxbContext = JAXBContext.newInstance(Document.class, Bytes.class, ResponseCode.class);

        {
            final Document document = new Document(bytes);
            final Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");
            marshaller.marshal(document, xmlContent);

            System.out.println(xmlContent);
            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                            "<document xmlns:ns2=\"http://tomitribe.com/xml/ns/bytes\">\n" +
                            "    <bytes min=\"10kb\" max=\"20mb\"/>\n" +
                            "    <bytes min=\"10kb\" max=\"20mb\"/>\n" +
                            "    <responseCode code=\"200\"/>\n" +
                            "</document>\n",
                    xmlContent.toString());
        }

        { // from xml
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            final Document object = (Document) unmarshaller.unmarshal(IO.read(xmlContent.toString()));
            assertEquals(3, object.function.size());
            assertEquals(bytes, (Bytes) object.function.get(0));
        }

    }

    @Test
    public void testXml2() throws Exception {

        final Bytes bytes = new Bytes(new Size("10k"), new Size("20mb"));

        final PrintString xmlContent = new PrintString();
        final JAXBContext jaxbContext = JAXBContext.newInstance(
                Document.class,
                Bytes.class,
                CompositeFunction.class,
                Outcome.class,
                ResponseCode.class);

        {
            final Outcome outcome = new Outcome(100, new CompositeFunction<>(bytes));
            final Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");
            marshaller.marshal(outcome, xmlContent);

            System.out.println(xmlContent);
            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                            "<outcome xmlns:ns2=\"http://tomitribe.com/xml/ns/bytes\" weight=\"100\">\n" +
                            "    <composite>\n" +
                            "        <bytes min=\"10kb\" max=\"20mb\"/>\n" +
                            "    </composite>\n" +
                            "</outcome>\n",
                    xmlContent.toString());
        }

        { // from xml
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            final Outcome object = (Outcome) unmarshaller.unmarshal(IO.read(xmlContent.toString()));
            assertEquals(100, object.getWeight());
            final CompositeFunction function = (CompositeFunction) object.getFunction();
            assertEquals(bytes, (Bytes) function.getFunctions().get(0));
        }

    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Document implements Function<Response.ResponseBuilder, Response.ResponseBuilder> {

        @XmlAnyElement(lax = true)
        private final List<Function<Response.ResponseBuilder, Response.ResponseBuilder>> function = new ArrayList<>();

        public Document() {
        }

        public Document(Function<Response.ResponseBuilder, Response.ResponseBuilder> bytes) {
            this.function.add(bytes);
            this.function.add(bytes);
            this.function.add(new ResponseCode(200));
        }

        @Override
        public Response.ResponseBuilder apply(Response.ResponseBuilder responseBuilder) {
            for (final Function<Response.ResponseBuilder, Response.ResponseBuilder> f : function) {
                responseBuilder = f.apply(responseBuilder);
            }
            return responseBuilder;
        }
    }
}
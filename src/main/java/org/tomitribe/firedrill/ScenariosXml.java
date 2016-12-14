/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.tomitribe.firedrill;

import org.tomitribe.firedrill.rs.Bytes;
import org.tomitribe.firedrill.rs.ResponseCode;
import org.tomitribe.util.IO;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.OutputStream;

public class ScenariosXml {


    private JAXBContext jaxbContext;

    public ScenariosXml() throws JAXBException {
        this.jaxbContext = getJaxbContext();
    }


    public Scenarios<Response.ResponseBuilder> unmarshal(OutputStream xmlContent) throws JAXBException {
        return unmarshal(IO.read(xmlContent.toString()));
    }

    public Scenarios<Response.ResponseBuilder> unmarshal(final InputStream read) throws JAXBException {
        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        final Scenarios.Xml unmarshal = (Scenarios.Xml) unmarshaller.unmarshal(read);
        return unmarshal.getScenarios();
    }

    public static JAXBContext getJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(
                Scenarios.class,
                Scenarios.Xml.class,
                CompositeFunction.class,
                Outcome.class,
                Bytes.class,
                ResponseCode.class);
    }

    public void marshal(Scenarios<Response.ResponseBuilder> scenarios, OutputStream xmlContent) throws JAXBException {
        marshal(scenarios, xmlContent, jaxbContext);
    }

    public static void marshal(Scenarios<Response.ResponseBuilder> scenarios, OutputStream xmlContent, JAXBContext jaxbContext) throws JAXBException {
        final Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(Scenarios.Xml.from(scenarios), xmlContent);
    }
}

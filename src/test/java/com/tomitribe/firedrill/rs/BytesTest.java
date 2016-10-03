package com.tomitribe.firedrill.rs;

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
                            "<document>\n" +
                            "    <bytes min=\"10kb\" max=\"20mb\"/>\n" +
                            "    <bytes min=\"10kb\" max=\"20mb\"/>\n" +
                            "    <responseCode>200</responseCode>\n" +
                            "</document>\n",
                    xmlContent.toString());
        }

        { // from xml
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            final Document object = (Document) unmarshaller.unmarshal(IO.read(xmlContent.toString()));
            assertEquals(3, object.function.size());
            assertEquals(bytes, (Bytes) object.function.get(0));
        }

        System.out.println(xmlContent);
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Document implements Function<Response.ResponseBuilder, Response.ResponseBuilder> {

        @XmlAnyElement(lax = true)
        private final List<Function<Response.ResponseBuilder, Response.ResponseBuilder>> function = new ArrayList<>();

        public Document() {
        }

        public Document(Bytes bytes) {
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
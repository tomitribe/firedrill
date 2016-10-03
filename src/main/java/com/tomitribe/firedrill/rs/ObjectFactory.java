/*
 * Tomitribe Confidential
 *
 * Copyright Tomitribe Corporation. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package org.apache.openejb.jee.jpa.unit;


import com.tomitribe.firedrill.rs.Bytes;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.apache.openejb.persistence.xml package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Application_QNAME = new QName("http://tomitribe.com/xml/ns/bytes", "bytes");


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.apache.openejb.persistence.xml
     */
    public ObjectFactory() {
    }

    @XmlElementDecl(namespace = "http://tomitribe.com/xml/ns/bytes", name = "bytes")
    public JAXBElement<Bytes> createBytes(final Bytes value) {
        return new JAXBElement<Bytes>(_Application_QNAME, Bytes.class, null, value);
    }

}

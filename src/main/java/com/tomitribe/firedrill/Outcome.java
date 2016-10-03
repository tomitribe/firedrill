/*
 * Tomitribe Confidential
 *
 * Copyright Tomitribe Corporation. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.tomitribe.firedrill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.function.Function;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Outcome<R, T> {

    @XmlAttribute
    private final int weight;

    @XmlAnyElement(lax = true)
    final Function<R, T> function;

    public Outcome() {
        weight = 0;
        function = null;
    }

    public Outcome(int weight, Function<R, T> function) {
        this.weight = weight;
        this.function = function;
    }

    public int getWeight() {
        return weight;
    }

    public Function<R, T> getFunction() {
        return function;
    }
}

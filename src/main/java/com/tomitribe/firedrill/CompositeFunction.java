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
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@XmlRootElement(name = "composite")
@XmlAccessorType(XmlAccessType.FIELD)
public class CompositeFunction<T> implements Function<T, T> {

    @XmlAnyElement(lax = true)
    private final List<Function<T, T>> functions = new ArrayList<>();

    public CompositeFunction() {
    }

    public CompositeFunction(Function<T, T>... functions) {
        for (final Function<T, T> function : functions) {
            if (function != null) {
                this.functions.add(function);
            }
        }
    }

    @Override
    public T apply(T t) {
        for (final Function<T, T> f : functions) {
            t = f.apply(t);
        }
        return t;
    }

    public List<Function<T, T>> getFunctions() {
        return functions;
    }
}

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
package org.tomitribe.firedrill;

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

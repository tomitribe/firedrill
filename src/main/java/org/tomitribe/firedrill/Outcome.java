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

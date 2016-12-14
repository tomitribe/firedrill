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

import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.Options;
import org.tomitribe.firedrill.songs.Song;
import org.tomitribe.firedrill.songs.SongsBean;
import org.tomitribe.firedrill.util.Chance;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Optional;
import java.util.function.Function;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "song-find")
@Options
public class SongFind implements Function<Response.ResponseBuilder, Response.ResponseBuilder> {

    @XmlAttribute
    private final Integer min;

    @XmlAttribute
    private final Integer max;

    public SongFind(@Option("min") Integer min, @Option("max") Integer max) {
        this.min = Optional.ofNullable(min).orElse(1);
        this.max = Optional.ofNullable(max).orElse(this.min);

        if (min < 0) {
            throw new IllegalStateException(String.format("Min and Max must be zero or more: min='%s' max='%s'", min, max));
        }

        if (min > max) {
            throw new IllegalStateException(String.format("Min cannot be greater than Max: min='%s' max='%s'", min, max));
        }
    }

    @Override
    public Response.ResponseBuilder apply(Response.ResponseBuilder responseBuilder) {
        try {
            final SongsBean songsBean = (SongsBean) new InitialContext().lookup("java:global/twitter/SongsBean");

            long count = Chance.chance.get().range(min, max);

            for (int i = 0; i < count; i++) {
                long id = Chance.chance.get().range(1, 100);
                final Song song = songsBean.findSong(id);
            }

        } catch (NamingException e) {
            e.printStackTrace();
        }

        return responseBuilder;
    }
}

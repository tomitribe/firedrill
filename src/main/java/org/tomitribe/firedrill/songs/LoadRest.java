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
package org.tomitribe.firedrill.songs;

import javax.ejb.EJB;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("load")
public class LoadRest {
    @EJB
    private SongsBean songsBean;

    @POST
    public void load() {
        songsBean.addSong(new Song("Wedding Crashers", "David Dobkin", "Comedy", 7, 2005));
        songsBean.addSong(new Song("Starsky & Hutch", "Todd Phillips", "Action", 6, 2004));
        songsBean.addSong(new Song("Shanghai Knights", "David Dobkin", "Action", 6, 2003));
        songsBean.addSong(new Song("I-Spy", "Betty Thomas", "Adventure", 5, 2002));
        songsBean.addSong(new Song("The Royal Tenenbaums", "Wes Anderson", "Comedy", 8, 2001));
        songsBean.addSong(new Song("Zoolander", "Ben Stiller", "Comedy", 6, 2001));
        songsBean.addSong(new Song("Shanghai Noon", "Tom Dey", "Comedy", 7, 2000));
    }

}

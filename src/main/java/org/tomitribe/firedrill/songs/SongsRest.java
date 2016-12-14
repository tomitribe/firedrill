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
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

@Path("songs")
@Produces({"application/json"})
public class SongsRest {

    @EJB
    private SongsBean service;

    @GET
    @Path("{id}")
    public Song find(@PathParam("id") Long id) {
        return service.findSong(id);
    }

    @GET
    public List<Song> getSongs(@QueryParam("first") Integer first, @QueryParam("max") Integer max,
                               @QueryParam("field") String field, @QueryParam("searchTerm") String searchTerm) {
        return service.getSongs(first, max, field, searchTerm);
    }

    @POST
    @Consumes("application/json")
    public Song addSong(Song song) {
        service.addSong(song);
        return song;
    }

    @PUT
    @Path("{id}")
    @Consumes("application/json")
    public Song editSong(Song song) {
        service.editSong(song);
        return song;
    }

    @DELETE
    @Path("{id}")
    public void deleteSong(@PathParam("id") long id) {
        service.deleteSong(id);
    }

    @GET
    @Path("count")
    public int count(@QueryParam("field") String field, @QueryParam("searchTerm") String searchTerm) {
        return service.count(field, searchTerm);
    }
}

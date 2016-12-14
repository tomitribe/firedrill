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

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import java.util.List;

@Singleton
@Lock(LockType.READ)
public class SongsBean {

    @PersistenceContext(unitName = "song-unit")
    private EntityManager entityManager;

    public Song findSong(final Long id) {
        try {
            return entityManager.find(Song.class, id);

        } catch (final Throwable throwable) {
            throw new RuntimeException("Cannot find song " + id);
        }
    }

    public void addSong(Song song) {
        entityManager.persist(song);
    }

    public void editSong(Song song) {
        entityManager.merge(song);
    }

    public void deleteSong(long id) {
        Song song = entityManager.find(Song.class, id);
        entityManager.remove(song);
    }

    public List<Song> getSongs(Integer firstResult, Integer maxResults, String field, String searchTerm) {
        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Song> cq = qb.createQuery(Song.class);
        Root<Song> root = cq.from(Song.class);
        EntityType<Song> type = entityManager.getMetamodel().entity(Song.class);
        if (field != null && searchTerm != null && !"".equals(field.trim()) && !"".equals(searchTerm.trim())) {
            Path<String> path = root.get(type.getDeclaredSingularAttribute(field.trim(), String.class));
            Predicate condition = qb.like(path, "%" + searchTerm.trim() + "%");
            cq.where(condition);
        }
        TypedQuery<Song> q = entityManager.createQuery(cq);
        if (maxResults != null) {
            q.setMaxResults(maxResults);
        }
        if (firstResult != null) {
            q.setFirstResult(firstResult);
        }
        return q.getResultList();
    }

    public int count(String field, String searchTerm) {
        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        Root<Song> root = cq.from(Song.class);
        EntityType<Song> type = entityManager.getMetamodel().entity(Song.class);
        cq.select(qb.count(root));
        if (field != null && searchTerm != null && !"".equals(field.trim()) && !"".equals(searchTerm.trim())) {
            Path<String> path = root.get(type.getDeclaredSingularAttribute(field.trim(), String.class));
            Predicate condition = qb.like(path, "%" + searchTerm.trim() + "%");
            cq.where(condition);
        }
        return entityManager.createQuery(cq).getSingleResult().intValue();
    }

    public void clean() {
        entityManager.createQuery("delete from Song").executeUpdate();
    }
}

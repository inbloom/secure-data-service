/*
 *
 *  * Copyright 2012 Shared Learning Collaborative, LLC
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.slc.sli.dal.repository;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

import java.util.List;
import java.util.Map;

/**
 * mongodb implementation of the repository interface that provides basic CRUD
 * and field query methods for all object classes.
 *
 * @author pghosh@wgen.net
 *
 */
public class MongoPerfRepository<Entity> implements Repository<Entity> {
    @Override
    public Entity create(String type, Map<String, Object> body) {
        throw new UnsupportedOperationException("MongoPerfRepository.findById not implemented");
    }

    @Override
    public Entity create(String type, Map<String, Object> body, String collectionName) {
        throw new UnsupportedOperationException("MongoPerfRepository.findById not implemented");
    }

    @Override
    public Entity create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName) {
        throw new UnsupportedOperationException("MongoPerfRepository.findById not implemented");
    }

    @Override
    public Entity findById(String collectionName, String id) {
        throw new UnsupportedOperationException("MongoPerfRepository.findById not implemented");
    }

    @Override
    public boolean exists(String collectionName, String id) {
        throw new UnsupportedOperationException("MongoPerfRepository.exists not implemented");
    }

    @Override
    public Entity findOne(String collectionName, NeutralQuery neutralQuery) {
        throw new UnsupportedOperationException("MongoPerfRepository.findOne not implemented");
    }

    @Override
    public Iterable<Entity> findAll(String collectionName) {
        throw new UnsupportedOperationException("MongoPerfRepository.findAll not implemented");
    }

    @Override
    public Iterable<Entity> findAll(String collectionName, NeutralQuery neutralQuery) {
        throw new UnsupportedOperationException("MongoPerfRepository.findAll not implemented");
    }

    @Override
    public Iterable<Entity> findAllByPaths(String collectionName, Map<String, String> paths, NeutralQuery neutralQuery) {
        throw new UnsupportedOperationException("MongoPerfRepository.findAllByPaths not implemented");
    }

    @Override
    public Iterable<String> findAllIds(String collectionName, NeutralQuery neutralQuery) {
        throw new UnsupportedOperationException("MongoPerfRepository.findAllIds not implemented");
    }

    @Override
    public long count(String collectionName, NeutralQuery neutralQuery) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean update(String collection, Entity object) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean delete(String collectionName, String id) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteAll(String collectionName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public CommandResult execute(DBObject command) {
        throw new UnsupportedOperationException("MongoPerfRepository.execute not implemented");
    }

    @Override
    public DBCollection getCollection(String collectionName) {
        throw new UnsupportedOperationException("MongoPerfRepository.findById not implemented");
    }

    @Override
    public List<DBCollection> getCollections(boolean includeSystemCollections) {
        throw new UnsupportedOperationException("MongoPerfRepository.findById not implemented");
    }

    @Override
    public Iterable<Entity> findByPaths(String collectionName, Map<String, String> paths) {
        throw new UnsupportedOperationException("MongoPerfRepository.findById not implemented");
    }

    @Override
    public Iterable<Entity> findByQuery(String collectionName, Query query, int skip, int max) {
        throw new UnsupportedOperationException("MongoPerfRepository.findById not implemented");
    }

    @Override
    public boolean collectionExists(String collection) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void createCollection(String collection) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void ensureIndex(IndexDefinition index, String collection) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setWriteConcern(String writeConcern) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setReferenceCheck(String referenceCheck) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long count(String collectionName, Query query) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

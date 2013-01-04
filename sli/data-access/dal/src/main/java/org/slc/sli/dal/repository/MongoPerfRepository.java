/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.dal.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.DBCollection;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.slc.sli.dal.convert.IdConverter;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * mongodb implementation of the repository interface that provides basic CRUD
 * and field query methods for all object classes.
 *
 * @author pghosh@wgen.net
 *
 */
public class MongoPerfRepository<Entity> implements Repository<Entity> {

    protected MongoTemplate perfDbtemplate;

    protected IdConverter idConverter;

    @Autowired
    @Qualifier("entityKeyEncoder")
    EntityKeyEncoder keyEncoder;

    public IdConverter getIdConverter() {
        return idConverter;
    }

    public void setIdConverter(IdConverter idConverter) {
        this.idConverter = idConverter;
    }

    public MongoTemplate getPerfDbtemplate() {
        return perfDbtemplate;
    }

    public void setPerfDbtemplate(MongoTemplate perfDbtemplate) {
        this.perfDbtemplate = perfDbtemplate;
    }

    private static final int PADDING = 300;

    @Override
    public Entity create(String type, Map<String, Object> body) {
        return create(type, body, type);
    }

    @Override
    public Entity create(String type, Map<String, Object> body, String collectionName) {
        return create(type, body, new HashMap<String, Object>(), collectionName);
    }

    @Override
    public Entity create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName) {
        MongoEntity mongoEntity = new MongoEntity(type, null, body, new HashMap<String, Object>());
        keyEncoder.encodeEntityKey(mongoEntity);
        @SuppressWarnings("unchecked")
        Entity entity = (Entity) mongoEntity;
        perfDbtemplate.insert(entity, collectionName);
        return entity;
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
    public Iterable<Entity> findAll(String collectionName, NeutralQuery neutralQuery) {
        throw new UnsupportedOperationException("MongoPerfRepository.findAll not implemented");
    }

    @Override
    public Iterable<String> findAllIds(String collectionName, NeutralQuery neutralQuery) {
        throw new UnsupportedOperationException("MongoPerfRepository.findAllIds not implemented");
    }

    @Override
    public long count(String collectionName, NeutralQuery neutralQuery) {
        return 0;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean update(String collection, Entity object) {
        return false;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean delete(String collectionName, String id) {
        return false;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteAll(String collectionName, NeutralQuery query) {
        // To change body of implemented methods use File | Settings | File Templates.
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
    public Iterable<Entity> findByQuery(String collectionName, Query query, int skip, int max) {
        throw new UnsupportedOperationException("MongoPerfRepository.findById not implemented");
    }

    @Override
    public boolean collectionExists(String collection) {
        return false;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setWriteConcern(String writeConcern) {
        perfDbtemplate.setWriteConcern(WriteConcern.valueOf(writeConcern));
    }

    @Override
    public void setReferenceCheck(String referenceCheck) {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long count(String collectionName, Query query) {
        return 0;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean doUpdate(String collection, NeutralQuery query, Update update) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean updateWithRetries(String collection, Entity object, int noOfRetries) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Entity> insert(List<Entity> records, String collectionName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Entity createWithRetries(String type, String id, Map<String, Object> body, Map<String, Object> metaData,
            String collectionName, int noOfRetries) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean patch(String type, String collectionName, String id, Map<String, Object> newValues) {
        throw new UnsupportedOperationException("MongoPerfRepository.patch not implemented");
    }

    @Override
    public WriteResult updateMulti(NeutralQuery query, Map<String, Object> update, String entityReferenced) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Entity findAndUpdate(String collectionName, NeutralQuery neutralQuery, Update update) {
        return null;
    }
}

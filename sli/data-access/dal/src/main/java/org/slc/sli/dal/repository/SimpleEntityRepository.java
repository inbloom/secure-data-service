/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import java.util.List;
import java.util.Map;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

/**
 * @author ifaybyshev
 *
 */
//@Component
public class SimpleEntityRepository implements Repository<Entity> {

    @Override
    public Entity create(String type, Map<String, Object> body) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Entity create(String type, Map<String, Object> body, String collectionName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Entity create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Entity findById(String collectionName, String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean exists(String collectionName, String id) {
        return true;
    }

    @Override
    public Entity findOne(String collectionName, NeutralQuery neutralQuery) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<Entity> findAll(String collectionName, NeutralQuery neutralQuery) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<String> findAllIds(String collectionName, NeutralQuery neutralQuery) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long count(String collectionName, NeutralQuery neutralQuery) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean update(String collection, Entity object) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean delete(String collectionName, String id) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void deleteAll(String collectionName, NeutralQuery q) {
        // TODO Auto-generated method stub

    }

    @Override
    public DBCollection getCollection(String collectionName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<Entity> findByQuery(String collectionName, Query query, int skip, int max) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean collectionExists(String collection) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setWriteConcern(String writeConcern) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setReferenceCheck(String referenceCheck) {
        // TODO Auto-generated method stub

    }

    @Override
    public long count(String collectionName, Query query) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<DBCollection> getCollections(boolean includeSystem) {
        return null;
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
        return null;
    }

    @Override
    public boolean patch(String type, String collectionName, String id, Map<String, Object> newValues) {
        return false;
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

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

package org.slc.sli.api.service;

import java.util.List;
import java.util.Map;

import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Mock entity repository for testing purposes
 *
 * @author pghosh
 *
 */
@Component("performanceRepo")
public class MockPerfRepo implements Repository<Entity> {
    @Override
    public Entity create(String type, Map<String, Object> body) {
        return null;
    }

    @Override
    public Entity create(String type, Map<String, Object> body, String collectionName) {
        return null;
    }

    @Override
    public Entity create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName) {
        return null;
    }

    @Override
    public Entity findById(String collectionName, String id) {
        return null;
    }

    @Override
    public boolean exists(String collectionName, String id) {
        return false;
    }

    @Override
    public Entity findOne(String collectionName, NeutralQuery neutralQuery) {
        return null;
    }

    @Override
    public Iterable<Entity> findAll(String collectionName) {
        return null;
    }

    @Override
    public Iterable<Entity> findAll(String collectionName, NeutralQuery neutralQuery) {
        return null;
    }

    @Override
    public Iterable<Entity> findAllByPaths(String collectionName, Map<String, String> paths, NeutralQuery neutralQuery) {
        return null;
    }

    @Override
    public Iterable<String> findAllIds(String collectionName, NeutralQuery neutralQuery) {
        return null;
    }

    @Override
    public long count(String collectionName, NeutralQuery neutralQuery) {
        return 0;
    }

    @Override
    public boolean update(String collection, Entity object) {
        return false;
    }

    @Override
    public boolean delete(String collectionName, String id) {
        return false;
    }

    @Override
    public void deleteAll(String collectionName) {

    }

    @Override
    public CommandResult execute(DBObject command) {
        return null;
    }

    @Override
    public DBCollection getCollection(String collectionName) {
        return null;
    }

    @Override
    public List<DBCollection> getCollections(boolean includeSystemCollections) {
        return null;
    }

    @Override
    public Iterable<Entity> findByPaths(String collectionName, Map<String, String> paths) {
        return null;
    }

    @Override
    public Iterable<Entity> findByQuery(String collectionName, Query query, int skip, int max) {
        return null;
    }

    @Override
    public boolean collectionExists(String collection) {
        return false;
    }

    @Override
    public void createCollection(String collection) {

    }

    @Override
    public void setWriteConcern(String writeConcern) {

    }

    @Override
    public void setReferenceCheck(String referenceCheck) {

    }

    @Override
    public long count(String collectionName, Query query) {
        return 0;
    }

    @Override
    public boolean doUpdate(String collection, String id, Update update) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean doUpdate(String collection, NeutralQuery query, Update update) {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public Entity createWithRetries(String type, Map<String, Object> body, Map<String, Object> metaData,
            String collectionName, int noOfRetries) {
        return null;
    }


    @Override
    public boolean updateWithRetries(String collection, Entity object, int noOfRetries) {
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
}

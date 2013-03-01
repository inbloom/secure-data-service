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

package org.slc.sli.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

/**
 * Mock entity repository for testing purposes
 *
 * @author nbrown
 *
 */
@Component("validationRepo")
public class DummyEntityRepository implements Repository<Entity> {

    private Map<String, Map<String, Entity>> entities = new HashMap<String, Map<String, Entity>>();
    private boolean existsAlwaysTrue;

    public DummyEntityRepository() {
        this(false);
    }

    public DummyEntityRepository(boolean existsAlwaysTrue) {
        this.existsAlwaysTrue = existsAlwaysTrue;
    }

    public void clean() {
        entities = new HashMap<String, Map<String, Entity>>();
    }

    public void addCollection(String collection) {
        if (!entities.containsKey(collection)) {
            entities.put(collection, new HashMap<String, Entity>());
        }
    }

    public void addEntity(String collection, String id, Entity entity) {
        if (!entities.containsKey(collection)) {
            entities.put(collection, new HashMap<String, Entity>());
        }
        entities.get(collection).put(id, entity);
    }


    @Override
    public boolean collectionExists(String collection) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Iterable<Entity> findAll(String collectionName, NeutralQuery queryParameters) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Entity findById(String collectioName, String id) {
        Map<String, Entity> collection = entities.get(collectioName);
        return collection.get(id);
    }

    @Override
    public boolean update(String collection, Entity entity, boolean isSuperdoc) {
        addEntity(collection, entity.getEntityId(), entity);
        return true;
    }

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
    public boolean delete(String collectionName, String id) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void deleteAll(String collectionName, NeutralQuery query) {
        // TODO Auto-generated method stub

    }

    @Override
    public long count(String collectionName, NeutralQuery query) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Entity findOne(String collectionName, NeutralQuery query) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<String> findAllIds(String collectionName, NeutralQuery query) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DBCollection getCollection(String collectionName) {
        return null;
    }

    @Override
    public Iterable<Entity> findByQuery(String collectionName, Query query, int skip, int max) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean exists(String collectionName, String id) {
        if (existsAlwaysTrue) {
            return true;
        }
        Map<String, Entity> collection = entities.get(collectionName);
        if (collection.get(id) == null) {
            return false;
        }
        return true;
    }

    @Override
    public void setWriteConcern(String writeConcern) {
        // TODO Auto-generated method stub
    }

    @Override
    public long count(String collectionName, Query query) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setReferenceCheck(String referenceCheck) {
        // TODO Auto-generated method stub
    }

    @Override
    public List<DBCollection> getCollections(boolean includeSystemCollections) {
        // TODO Auto-generated method stub
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

    public void addEntity(String collectionName, Entity entity) {
        if (entities.get(collectionName) != null) {
            entities.get(collectionName).put(entity.getEntityId(), entity);
        } else {
            Map<String, Entity> map = new HashMap<String, Entity>();
            map.put(entity.getEntityId(), entity);
            entities.put(collectionName, map);
        }
    }

    @Override
    public Entity findAndUpdate(String collectionName, NeutralQuery neutralQuery, Update update) {
        return null;
    }
}

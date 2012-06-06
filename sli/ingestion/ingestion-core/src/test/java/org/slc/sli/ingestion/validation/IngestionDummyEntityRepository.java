package org.slc.sli.ingestion.validation;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Mock entity repository for testing purposes
 *
 * @author nbrown
 *
 */
@Component
public class IngestionDummyEntityRepository implements Repository<Entity> {

    @Override
    public boolean collectionExists(String collection) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void createCollection(String collection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void ensureIndex(IndexDefinition index, String collection) {
        // TODO Auto-generated method stub

    }

    private Map<String, Map<String, Entity>> entities = new HashMap<String, Map<String, Entity>>();

    public void addEntity(String collection, String id, Entity entity) {
        if (!entities.containsKey(collection)) {
            entities.put(collection, new HashMap<String, Entity>());
        }
        entities.get(collection).put(id, entity);
    }

    @Override
    public Entity findById(String collectioName, String id) {
        Map<String, Entity> collection = entities.get(collectioName);
        return collection.get(id);
    }

    @Override
    public Iterable<Entity> findAll(String collectioName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean update(String collection, Entity entity) {
        addEntity(collection, entity.getEntityId(), entity);
        return true;
    }

    @Override
    public Entity create(String type, Map<String, Object> body) {
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
    public void deleteAll(String collectionName) {
        // TODO Auto-generated method stub

    }

    @Override
    public Iterable<Entity> findAllByPaths(String collectionName, Map<String, String> paths, NeutralQuery neutralQuery) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<Entity> findAll(String collectionName, NeutralQuery query) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<String> findAllIds(String collectionName, NeutralQuery query) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Entity findOne(String collectionName, NeutralQuery query) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long count(String collectionName, NeutralQuery query) {
        // TODO Auto-generated method stub
        return 0;
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
    public Iterable<Entity> findByPaths(String collectionName, Map<String, String> paths) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<Entity> findByQuery(String collectionName, Query query, int skip, int max) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean exists(String collectionName, String id) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void setWriteConcern(String writeConcern) {
        // TODO Auto-generated method stub

    }

}

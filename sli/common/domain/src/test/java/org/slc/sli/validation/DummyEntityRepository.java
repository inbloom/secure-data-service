package org.slc.sli.validation;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;


import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.NeutralQuery;

/**
 * Mock entity repository for testing purposes
 * 
 * @author nbrown
 * 
 */
@Component
public class DummyEntityRepository implements Repository<Entity> {
    
    private Map<String, Map<String, Entity>> entities = new HashMap<String, Map<String, Entity>>();
    
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
    public Iterable<Entity> findAll(String collectioName) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public boolean update(String collection, Entity entity) {
        // TODO Auto-generated method stub
        return false;
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
    public void deleteAll(String collectionName) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public Iterable<Entity> findAllByPaths(String collectionName, Map<String, String> paths, NeutralQuery neutralQuery) {
        // TODO Auto-generated method stub
        return null;
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

    public CommandResult execute(DBObject command) {
        return null;
    }

    @Override
    public DBCollection getCollection(String collectionName) {
        return null;
    }
    
}

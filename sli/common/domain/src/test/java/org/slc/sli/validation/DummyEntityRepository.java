package org.slc.sli.validation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;

/**
 * Mock entity repository for testing purposes
 * 
 * @author nbrown
 * 
 */
@Component
public class DummyEntityRepository implements EntityRepository {
    
    private Map<String, Map<String, Entity>> entities = new HashMap<String, Map<String, Entity>>();
    
    public void addEntity(String collection, String id, Entity entity) {
        if (!entities.containsKey(collection)) {
            entities.put(collection, new HashMap<String, Entity>());
        }
        entities.get(collection).put(id, entity);
    }

    @Override
    public Entity find(String collectionName, String id, String includeFields, String excludeFields) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Entity find(String collectioName, String id) {
        Map<String, Entity> collection = entities.get(collectioName);
        return collection.get(id);
    }
    
    @Override
    public Iterable<Entity> findAll(String collectionName, int skip, int max) {
        // TODO Auto-generated method stub
        return null;
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
    public Iterable<Entity> findByFields(String collectionName, Map<String, String> fields, int skip, int max) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Iterable<Entity> findByPaths(String collectionName, Map<String, String> paths, int skip, int max) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Iterable<Entity> findByFields(String collectionName, Map<String, String> fields) {
        // TODO Auto-generated method stub
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
    public Iterable<String> findIdsByQuery(String collectionName, Query query, int skip, int max) {
        // TODO Auto-generated method stub
        return null;
    }
    
}

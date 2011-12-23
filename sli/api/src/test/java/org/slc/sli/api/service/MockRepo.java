package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

/**
 * Mock implementation of the EntityRepository for unit testing.
 * 
 */
@Component
public class MockRepo implements EntityRepository {
    
    private Map<String, Map<String, Entity>> repo = new HashMap<String, Map<String, Entity>>();
    
    AtomicLong nextID = new AtomicLong();
    
    public MockRepo() {
        repo.put("student", new LinkedHashMap<String, Entity>());
        repo.put("school", new LinkedHashMap<String, Entity>());
        repo.put("roles", new LinkedHashMap<String, Entity>());
        repo.put("studentschoolassociation", new LinkedHashMap<String, Entity>());
        repo.put("teacher", new LinkedHashMap<String, Entity>());
    }
    
    protected Map<String, Map<String, Entity>> getRepo() {
        return repo;
    }
    
    protected void setRepo(Map<String, Map<String, Entity>> repo) {
        this.repo = repo;
    }
    
    @Override
    public Entity find(String entityType, String id) {
        return repo.get(entityType).get(id);
    }
    
    @Override
    public Iterable<Entity> findAll(String entityType, int skip, int max) {
        List<Entity> all = new ArrayList<Entity>(repo.get(entityType).values());
        return all.subList(skip, (Math.min(skip + max, all.size())));
    }
    
    @Override
    public void update(String type, Entity entity) {
        repo.get(type).put(entity.getEntityId(), entity);
    }
    
    @Override
    public Entity create(String type, Map<String, Object> body) {
        Entity newEntity = new MongoEntity(type, Long.toString(nextID.getAndIncrement()), body, null);
        update(type, newEntity);
        return newEntity;
    }
    
    @Override
    public void delete(String entityType, String id) {
        repo.get(entityType).remove(id);
    }
    
    @Override
    public Iterable<Entity> findByFields(String entityType, Map<String, String> fields, int skip, int max) {
        List<Entity> toReturn = new ArrayList<Entity>();
        
        if (repo.containsKey(entityType)) {
            List<Entity> all = new ArrayList<Entity>(repo.get(entityType).values());
            for (Entity entity : all) {
                if (matchesFields(entity, fields)) {
                    toReturn.add(entity);
                }
            }
        }
        return toReturn.subList(skip, (Math.min(skip + max, toReturn.size())));
    }
    
    private boolean matchesFields(Entity entity, Map<String, String> fields) {
        for (Map.Entry<String, String> field : fields.entrySet()) {
            Object value = entity.getBody().get(field.getKey());
            if (value == null || !value.toString().equals(field.getValue())) {
                return false;
            }
        }
        return true;
        
    }
    
    @Override
    public void deleteAll(String entityType) {
        Map<String, Entity> repository = repo.get(entityType);
        if (repository != null) {
            repository.clear();
        }
        
    }
    
    @Override
    public Iterable<Entity> findAll(String entityType) {
        List<Entity> all = new ArrayList<Entity>(repo.get(entityType).values());
        return all;
    }
    
    @Override
    public Iterable<Entity> findByFields(String entityType, Map<String, String> fields) {
        List<Entity> all = new ArrayList<Entity>(repo.get(entityType).values());
        List<Entity> toReturn = new ArrayList<Entity>();
        for (Entity entity : all) {
            if (matchesFields(entity, fields)) {
                toReturn.add(entity);
            }
        }
        return toReturn;
    }
    
}

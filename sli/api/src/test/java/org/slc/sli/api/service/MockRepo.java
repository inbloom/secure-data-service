package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.springframework.stereotype.Component;

@Component
public class MockRepo implements EntityRepository {
    
    private Map<String, Map<String, Entity>> repo = new HashMap<String, Map<String, Entity>>();
    
    AtomicLong nextID = new AtomicLong();
    
    public MockRepo() {
        repo.put("student", new LinkedHashMap<String, Entity>());
        repo.put("school", new LinkedHashMap<String, Entity>());
        repo.put("studentEnrollment", new LinkedHashMap<String, Entity>());
        repo.put("schoolEnrollment", new LinkedHashMap<String, Entity>());
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
    public Iterable<Entity> finalAll(String entityType, int skip, int max) {
        List<Entity> all = new ArrayList<Entity>(repo.get(entityType).values());
        return all.subList(skip, skip + max);
    }
    
    @Override
    public void update(Entity entity) {
        repo.get(entity.getType()).put(entity.getId(), entity);
    }
    
    @Override
    public Entity create(Entity entity) {
        Entity newEntity = new MongoEntity(entity.getType(), Long.toString(nextID.getAndIncrement()), entity.getBody(),
                null);
        update(newEntity);
        return newEntity;
    }
    
    @Override
    public void delete(Entity entity) {
        repo.get(entity.getType()).remove(entity.getId());
        
    }
    
    @Override
    public void delete(String entityType, String id) {
        repo.get(entityType).remove(id);
    }
    
    @Override
    public Iterable<Entity> findByFields(String entityType, Map<String, String> fields, int skip, int max) {
        // TODO Auto-generated method stub
        return null;
    }
    
}

package org.slc.sli.dal.repository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slc.sli.dal.convert.IdConverter;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

/**
 * mongodb implementation of the entity repository interface that provides basic
 * CRUD and field query methods for entities including core entities and
 * association entities
 * 
 * @author Dong Liu dliu@wgen.net
 * 
 */

public class MongoEntityRepository implements EntityRepository {
    private static final Logger LOG = LoggerFactory.getLogger(MongoEntityRepository.class);
    
    @Autowired
    private MongoTemplate template;
    
    @Autowired
    private IdConverter idConverter;
    
    @Override
    public Entity find(String entityType, String id) {
        Object databaseId = idConverter.toDatabaseId(id);
        LOG.debug("find a entity in collection {} with id {}", new Object[] { entityType, id });
        return template.findById(databaseId, MongoEntity.class, entityType);
    }
    
    @Override
    public Iterable<Entity> findAll(String entityType, int skip, int max) {
        
        List<Entity> entities = new LinkedList<Entity>();
        List<MongoEntity> results = template.find(new Query().skip(skip).limit(max), MongoEntity.class, entityType);
        logResults(entityType, results);
        entities.addAll(results);
        return entities;
    }
    
    @Override
    public void update(String collection, Entity entity) {
        Assert.notNull(entity, "The given entity must not be null!");
        String id = entity.getEntityId();
        if (id.equals(""))
            return;
        
        Entity found = template.findOne(new Query(Criteria.where("_id").is(idConverter.toDatabaseId(id))),
                MongoEntity.class, collection);
        if (found != null)
            template.save(entity, collection);
        LOG.info("update a entity in collection {} with id {}", new Object[] { collection, id });
    }
    
    @Override
    public Entity create(String type, Map<String, Object> body) {
        return create(type, body, type);
    }
    
    @Override
    public Entity create(String type, Map<String, Object> body, String collectionName) {
        Assert.notNull(body, "The given entity must not be null!");
        Entity entity = new MongoEntity(type, null, body, new HashMap<String, Object>());
        template.save(entity, collectionName);
        LOG.info(" create a entity in collection {} with id {}", new Object[] { collectionName, entity.getEntityId() });
        return entity;
    }
    
    public void delete(String entityType, String id) {
        if (id.equals(""))
            return;
        template.remove(new Query(Criteria.where("_id").is(idConverter.toDatabaseId(id))), entityType);
        LOG.info("delete a entity in collection {} with id {}", new Object[] { entityType, id });
    }
    
    @Override
    public Iterable<Entity> findByFields(String entityType, Map<String, String> fields, int skip, int max) {
        Query query = new Query();
        query.skip(skip).limit(max);
        List<MongoEntity> results = template.find(addSearchFieldsToQuery(query, fields), MongoEntity.class, entityType);
        logResults(entityType, results);
        return new LinkedList<Entity>(results);
    }
    
    @Override
    public void deleteAll(String entityType) {
        template.remove(new Query(), entityType);
        LOG.info("delete all entities in collection {}", entityType);
    }
    
    @Override
    public Iterable<Entity> findAll(String entityType) {
        List<Entity> entities = new LinkedList<Entity>();
        List<MongoEntity> results = template.find(new Query(), MongoEntity.class, entityType);
        logResults(entityType, results);
        entities.addAll(results);
        return entities;
    }
    
    @Override
    public Iterable<Entity> findByFields(String entityType, Map<String, String> fields) {
        Query query = new Query();
        List<MongoEntity> results = template.find(addSearchFieldsToQuery(query, fields), MongoEntity.class, entityType);
        logResults(entityType, results);
        return new LinkedList<Entity>(results);
    }
    
    private Query addSearchFieldsToQuery(Query query, Map<String, String> searchFields) {
        for (Map.Entry<String, String> field : searchFields.entrySet()) {
            Criteria criteria = Criteria.where("body." + field.getKey()).is(field.getValue());
            query.addCriteria(criteria);
        }
        return query;
    }
    
    @SuppressWarnings("rawtypes")
    private void logResults(String entityType, List results) {
        if (results == null)
            LOG.info("find entities in collection {} with total numbers is {}", new Object[] { entityType, 0 });
        else
            LOG.info("find entities in collection {} with total numbers is {}",
                    new Object[] { entityType, results.size() });
        
    }
    
    @Override
    public Iterable<Entity> findByFields(String entityType, Query query, int skip, int max) {
        if (query == null)
            query = new Query();
        query.skip(skip).limit(max);
        List<MongoEntity> results = template.find(query, MongoEntity.class, entityType);
        logResults(entityType, results);
        return new LinkedList<Entity>(results);
    }
    
    @Override
    public boolean matchQuery(String entityType, String id, Query query) {
        boolean match = false;
        if (query != null) {
            List<MongoEntity> results = template.find(query, MongoEntity.class, entityType);
            for (MongoEntity entity : results) {
                if (entity.getEntityId().equals(id))
                    match = true;
            }
        }
        return match;
    }
}

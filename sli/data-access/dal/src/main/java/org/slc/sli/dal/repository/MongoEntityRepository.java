package org.slc.sli.dal.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.WriteResult;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import org.slc.sli.dal.convert.IdConverter;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.EntityRepository;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.util.datetime.DateTimeUtil;
import org.slc.sli.validation.EntityValidator;

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
    
    @Autowired
    private EntityValidator validator;
    
    @Override
    public Entity find(String collectionName, String id) {
        return this.find(collectionName, id, null, null);
    }

    @Override
    public Entity find(String collectionName, String id, String includeFields, String excludeFields) {
        Object databaseId = idConverter.toDatabaseId(id);
        if (databaseId == null) {
            LOG.debug("Unable to process id {}", new Object[] { id });
            return null;
        }
        
        Query query = new Query(Criteria.where("_id").is(databaseId));
        
        if (includeFields != null) {
            for (String includeField : includeFields.split(",")) {
                LOG.debug("Including field " + includeField + " in resulting body");
                query.fields().include("body." + includeField);
            }
        } else if (excludeFields != null) {
            for (String excludeField : excludeFields.split(",")) {
                LOG.debug("Excluding field " + excludeField + " from resulting body");
                query.fields().exclude("body." + excludeField);
            }
        }
        
        LOG.debug("Finding in collection {} for ID {}", new Object[] { collectionName, id });
        return template.findOne(query, Entity.class, collectionName);
    }
    
    @Override
    public Iterable<Entity> findAll(String collectionName, int skip, int max) {
        List<Entity> results = template.find(new Query().skip(skip).limit(max), Entity.class, collectionName);
        logResults(collectionName, results);
        return results;
    }
    
    @Override
    public boolean update(String collection, Entity entity) {
        Assert.notNull(entity, "The given entity must not be null!");
        String id = entity.getEntityId();
        if (id.equals(""))
            return false;
        validator.validate(entity);
        
        updateTimestamp(entity);
        
        Entity found = template.findOne(new Query(Criteria.where("_id").is(idConverter.toDatabaseId(id))),
                Entity.class, collection);
        if (found != null) {
            template.save(entity, collection);
        }
        WriteResult result = template.updateFirst(new Query(Criteria.where("_id").is(idConverter.toDatabaseId(id))),
                new Update().set("body", entity.getBody()), collection);
        LOG.info("update a entity in collection {} with id {}", new Object[] { collection, id });
        return result.getN() == 1;
    }
    
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
        Assert.notNull(body, "The given entity must not be null!");
        Entity entity = new MongoEntity(type, null, body, metaData);
        validator.validate(entity);
        
        addTimestamps(entity);
        
        template.save(entity, collectionName);
        LOG.info(" create a entity in collection {} with id {}", new Object[] { collectionName, entity.getEntityId() });
        return entity;
    }
    
    /** Add the created and updated timestamp to the document metadata. */
    private void addTimestamps(Entity entity) {
        // String now = DateTimeUtil.getNowInUTC();
        Date now = DateTimeUtil.getNowInUTC();
        
        Map<String, Object> metaData = entity.getMetaData();
        metaData.put(EntityMetadataKey.CREATED.getKey(), now);
        metaData.put(EntityMetadataKey.UPDATED.getKey(), now);
    }
    
    /** Update the updated timestamp on the document metadata. */
    private void updateTimestamp(Entity entity) {
        Date now = DateTimeUtil.getNowInUTC();
        entity.getMetaData().put(EntityMetadataKey.UPDATED.getKey(), now);
    }
    
    @Override
    public boolean delete(String collectionName, String id) {
        if (id.equals(""))
            return false;
        Entity deleted = template.findAndRemove(new Query(Criteria.where("_id").is(idConverter.toDatabaseId(id))),
                Entity.class, collectionName);
        LOG.info("delete a entity in collection {} with id {}", new Object[] { collectionName, id });
        return deleted != null;
    }
    
    @Override
    public Iterable<Entity> findByFields(String collectionName, Map<String, String> fields, int skip, int max) {
        return findByPaths(collectionName, convertBodyToPaths(fields), skip, max);
    }
    
    @Override
    public Iterable<Entity> findByPaths(String collectionName, Map<String, String> paths, int skip, int max) {
        Query query = new Query();
        
        return findByQuery(collectionName, addSearchPathsToQuery(query, paths), skip, max);
    }
    
    @Override
    public void deleteAll(String collectionName) {
        template.remove(new Query(), collectionName);
        LOG.info("delete all entities in collection {}", collectionName);
    }
    
    @Override
    public Iterable<Entity> findAll(String collectionName) {
        return findByQuery(collectionName, new Query());
    }
    
    @Override
    public Iterable<Entity> findByFields(String collectionName, Map<String, String> fields) {
        return findByPaths(collectionName, convertBodyToPaths(fields));
    }
    
    @Override
    public Iterable<Entity> findByPaths(String collectionName, Map<String, String> paths) {
        Query query = new Query();
        
        return findByQuery(collectionName, addSearchPathsToQuery(query, paths));
    }
    
    @Override
    public Iterable<Entity> findByQuery(String collectionName, Query query, int skip, int max) {
        if (query == null)
            query = new Query();
        
        query.skip(skip).limit(max);
        
        return findByQuery(collectionName, query);
    }
    
    protected Iterable<Entity> findByQuery(String collectionName, Query query) {
        List<Entity> results = template.find(query, Entity.class, collectionName);
        logResults(collectionName, results);
        return results;
    }
    
    @Override
    public boolean matchQuery(String collectionName, String id, Query query) {
        boolean match = false;
        if (query != null) {
            List<Entity> results = template.find(query, Entity.class, collectionName);
            for (Entity entity : results) {
                if (entity.getEntityId().equals(id))
                    match = true;
            }
        }
        return match;
    }
    
    @Override
    public Iterable<String> findIdsByQuery(String collectionName, Query query, int skip, int max) {
        if (query == null) {
            query = new Query();
        }
        query.fields().include("_id");
        List<String> ids = new ArrayList<String>();
        for (Entity e : findByQuery(collectionName, query, skip, max)) {
            ids.add(e.getEntityId());
        }
        return ids;
    }
    
    private Query addSearchPathsToQuery(Query query, Map<String, String> searchPaths) {
        for (Map.Entry<String, String> field : searchPaths.entrySet()) {
            Criteria criteria = Criteria.where(field.getKey()).is(field.getValue());
            query.addCriteria(criteria);
        }
        
        return query;
    }
    
    private void logResults(String collectioName, List<Entity> results) {
        if (results == null) {
            LOG.debug("find entities in collection {} with total numbers is {}", new Object[] { collectioName, 0 });
        } else {
            LOG.debug("find entities in collection {} with total numbers is {}",
                    new Object[] { collectioName, results.size() });
        }
        
    }
    
    private Map<String, String> convertBodyToPaths(Map<String, String> body) {
        Map<String, String> paths = new HashMap<String, String>();
        for (Map.Entry<String, String> field : body.entrySet()) {
            paths.put("body." + field.getKey(), field.getValue());
        }
        
        return paths;
    }
}

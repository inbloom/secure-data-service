package org.slc.sli.dal.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.mongodb.DBCollection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

import org.slc.sli.dal.convert.IdConverter;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.EntityRepository;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.util.datetime.DateTimeUtil;
import org.slc.sli.validation.EntityValidator;

/**
 * mongodb implementation of the entity repository interface that provides basic
 * CRUD and field query methods for entities including core entities and
 * association entities
 * 
 * @author Dong Liu dliu@wgen.net
 */

public class MongoEntityRepository implements EntityRepository {
    private static final Logger LOG = LoggerFactory.getLogger(MongoEntityRepository.class);
    
    private MongoTemplate template;
    
    /**
     * Used for Spring bean property injection.
     * 
     * @author Thomas Shewchuk tshewchuk@wgen.net 2/28/2012 (PI3 US1226)
     */
    public void setTemplate(MongoTemplate template) {
        this.template = template;
    }
    
    @Autowired
    private IdConverter idConverter;
    
    @Autowired
    private EntityValidator validator;
    
    @Autowired
    private MongoQueryConverter queryConverter;
    
    @Override
    public Entity find(String collectionName, String id) {
        Object databaseId = idConverter.toDatabaseId(id);
        LOG.debug("find a entity in collection {} with id {}", new Object[] { collectionName, id });
        try {
            return template.findById(databaseId, MongoEntity.class, collectionName);
        } catch (Exception e) {
            LOG.error("Exception occurred", e);
            return null;
        }
    }
    
    @Override
    public Entity find(String collectionName, NeutralQuery neutralQuery) {
        
        //convert the neutral query into a mongo query
        Query mongoQuery = this.queryConverter.convert(collectionName, neutralQuery);
        
        // find and return an entity
        return template.findOne(mongoQuery, Entity.class, collectionName);
    }
    
    @Override
    public Iterable<Entity> findAll(String collectionName, NeutralQuery neutralQuery) {
        
        //convert the neutral query into a mongo query
        Query mongoQuery = this.queryConverter.convert(collectionName, neutralQuery);
        
        // find and return an entity
        return template.find(mongoQuery, Entity.class, collectionName);
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
        LOG.info("update a entity in collection {} with id {}", new Object[] { collection, id });
        return found != null ? true : false;
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
    public Iterable<Entity> findByPaths(String collectionName, Map<String, String> paths, NeutralQuery neutralQuery) {
        Query mongoQuery = this.queryConverter.convert(collectionName, neutralQuery);
        
        for (Map.Entry<String, String> field : paths.entrySet()) {
            mongoQuery.addCriteria(Criteria.where(field.getKey()).is(field.getValue()));
        }
        
        // find and return an entity
        return template.find(mongoQuery, Entity.class, collectionName);
    }
    
    @Override
    public void deleteAll(String collectionName) {
        template.remove(new Query(), collectionName);
        LOG.info("delete all entities in collection {}", collectionName);
    }
    
    @Override
    public Iterable<Entity> findAll(String collectionName) {
        return findAll(collectionName, null);
    }
    
    @Override
    public Iterable<Entity> findByPaths(String collectionName, Map<String, String> paths) {
        return findByPaths(collectionName, paths, null);
    }
    
    @Override
    public Entity findOne(String collectionName, NeutralQuery neutralQuery) {
        Query mongoQuery = this.queryConverter.convert(collectionName, neutralQuery);
        Entity entity = this.template.findOne(mongoQuery, Entity.class, collectionName);
        logResults(collectionName, Arrays.asList(entity));
        return entity;
    }
    
    @Override
    public long count(String collectionName, NeutralQuery neutralQuery) {
        DBCollection collection = template.getCollection(collectionName);
        if (collection == null) {
            return 0;
        }
        return collection.count(this.queryConverter.convert(collectionName, neutralQuery).getQueryObject());
    }
    
    private void logResults(String collectioName, List<Entity> results) {
        if (results == null) {
            LOG.debug("find entities in collection {} with total numbers is {}", new Object[] { collectioName, 0 });
        } else {
            LOG.debug("find entities in collection {} with total numbers is {}",
                    new Object[] { collectioName, results.size() });
        }
        
    }
    
    @Override
    public Iterable<String> findAllIds(String collectionName, NeutralQuery neutralQuery) {
        if (neutralQuery == null) {
            neutralQuery = new NeutralQuery();
        }
        neutralQuery.setIncludeFields("_id");
        
        List<String> ids = new ArrayList<String>();
        for (Entity e : findAll(collectionName, neutralQuery)) {
            ids.add(e.getEntityId());
        }
        return ids;
    }
}

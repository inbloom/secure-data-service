package org.slc.sli.datastore.mongo;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.mongodb.DBCollection;
import com.mongodb.MongoException;

import org.slc.sli.model.DeterministicUUID;
import org.slc.sli.model.LifecycleManager;
import org.slc.sli.model.ModelEntity;
import org.slc.sli.model.ModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * Define a simple Mongo DB Repository which supports both schema and data access methods.
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */

@Component
public class SimpleMongoRepository {
    
    // Logging
    private static final Logger log = LoggerFactory.getLogger(SimpleMongoRepository.class);    
    
    
    // Constants
    public static final int DEFAULT_PAGE_SIZE = 100;
    
    
    // Attributes
    
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private LifecycleManager lifecycleManager;
    
    
    // Constructors
    public SimpleMongoRepository() {        
    }
    
    
    // Methods
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    public MongoTemplate getMongoTemplate() {
        return this.mongoTemplate;
    }
    
    public void setLifecycleManager(LifecycleManager lifecycleManager) {
        this.lifecycleManager = lifecycleManager;
    }
    
    public LifecycleManager getLifecycleManager() {
        return this.lifecycleManager;
    }
    
    public long count(final String collectionName) { 
        return count(collectionName, new Query());
    }
    
    public long count(final String collectionName, final Query query) { 
        long collectionCount = this.getMongoTemplate().execute(collectionName, new CollectionCallback<Long>() {
            public Long doInCollection(DBCollection collection) throws MongoException, DataAccessException {
                return collection.count(query.getQueryObject());
            }
            });

        return collectionCount;
    }

    public ModelEntity save(String principle, String collectionName, ModelEntity entity) throws ModelException {

        // Generate Deterministic UUIDs
        Object existingId = entity.getId();
        if (!((existingId instanceof String) && (((String)existingId).length() > 0))) {
            DeterministicUUID.generate(entity);        
        }

        // Lifecycle State Machine
        boolean alreadyExists = lifecycleManager.install(entity);
                
        if (!alreadyExists) {
          return this.create(principle, collectionName, entity);
        } else {
          return this.update(principle, collectionName, entity);
        }
    }
    
    public ModelEntity create(String principle, String collectionName, ModelEntity entity) throws ModelException {
        
        // Setup Metadata
        Timestamp createdOn = new Timestamp(Calendar.getInstance().getTimeInMillis());
        entity.getMetaData().put("createdBy", principle);
        entity.getMetaData().put("createdOn", createdOn.toString());
        
        // Generate Deterministic UUIDs
        Object existingId = entity.getId();
        if (!((existingId instanceof String) && (((String)existingId).length() > 0))) {
            DeterministicUUID.generate(entity);        
        }

//        log.info("Creating: " + entity.toString());
            
        MongoEntity mongoEntity = new MongoEntity(entity);
        this.getMongoTemplate().insert(mongoEntity, collectionName);

        return entity;
    }
    
    public ModelEntity update(String principle, String collectionName, ModelEntity entity) throws ModelException {
        
        // Setup Metadata
        Timestamp updatedOn = new Timestamp(Calendar.getInstance().getTimeInMillis());
        entity.getMetaData().put("updatedBy", principle);
        entity.getMetaData().put("updatedOn", updatedOn.toString());
        
        // Generate Deterministic UUIDs
        Object existingId = entity.getId();
        if (!((existingId instanceof String) && (((String)existingId).length() > 0))) {
            DeterministicUUID.generate(entity);        
        }

//        log.info("Updating: " + entity.toString());
        
        // Convert UUID to Mongo ID
        Object mongoId = (new MongoIdConverter()).toDatabaseId(entity.getId().toString());
        
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is(mongoId);
        query.addCriteria(criteria);
        
        // Update Current Body Fields
        Update update = new Update();
        Map<String, Object> bodyMap = entity.getBody();
        for (Object key : bodyMap.keySet()) {            
            update.set("body." + key, bodyMap.get(key));
        }
        
        // Update Lifecycle
        update.set("lifecycle", entity.getLifecycle().toMap());
        
        // Update Metadata
        update.set("metadata", entity.getMetaData());
        
        this.getMongoTemplate().updateFirst(query, update, collectionName);

        return entity;
    }
    
    public void delete(String principle, String collectionName, ModelEntity entity) throws ModelException {
        this.delete(principle, collectionName, (String)entity.getId());
    }
    
    public void delete(String principle, String collectionName, String uuid) throws ModelException {
        
//        log.info("Deleting Entity With Id: " + uuid);
        
        // Convert UUID to Mongo ID
        Object mongoId = (new MongoIdConverter()).toDatabaseId(uuid);
        
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is(mongoId);
        query.addCriteria(criteria);
        
        this.getMongoTemplate().remove(query, collectionName);
    }
    
    public List<ModelEntity> findAll(String collectionName) throws ModelException {  
        
        return this.find(collectionName, new Query());
    }
    
    public List<ModelEntity> find(String collectionName, Query query) throws ModelException {  
        
        query.limit(DEFAULT_PAGE_SIZE);
        List entityList = this.getMongoTemplate().find(query, MongoEntity.class, collectionName);
        
        return (List<ModelEntity>)entityList;
    }
    
    public ModelEntity findById(String collectionName, String uuid) throws ModelException {  
        
//        log.info("Finding Entity With Id: " + uuid);
        
        // Convert UUID to Mongo ID
        Object mongoId = (new MongoIdConverter()).toDatabaseId(uuid);
        
        ModelEntity entity = (ModelEntity)this.getMongoTemplate().findById(mongoId, MongoEntity.class, collectionName);
        
//        log.info("Found: " + entity);
        
        return entity;
    }
    
    public List<ModelEntity> findWhereAttributeStartsWith(String collectionName, String attributeName, String startsWith) throws ModelException {  
        
        // Setup RegEx Query Criteria
        Query query = Query.query( new Criteria(attributeName).regex( "^" + startsWith ));
        
        return this.find(collectionName, query);
    }
    
    public void purge(String principle, final String collectionName) throws ModelException {    
        purge(principle, collectionName, new Query());
    }
    
    public void purge(String principle, final String collectionName, final Query query) throws ModelException { 
        this.getMongoTemplate().remove(query, collectionName);
    }
    
}

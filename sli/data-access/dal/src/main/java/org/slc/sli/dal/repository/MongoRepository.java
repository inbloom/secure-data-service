package org.slc.sli.dal.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slc.sli.dal.convert.IdConverter;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;


/**
 * mongodb implementation of the repository interface that provides basic CRUD
 * and field query methods for all object classes.
 * 
 * @author Thomas Shewchuk tshewchuk@wgen.net 3/2/2012 (PI3 US1226)
 * 
 */

public abstract class MongoRepository<T> implements Repository<T> {
    protected static final Logger LOG = LoggerFactory
            .getLogger(MongoRepository.class);

    protected MongoTemplate template;

    protected IdConverter idConverter;

    @Autowired
    private MongoQueryConverter queryConverter;

    public void setTemplate(MongoTemplate template) {
        this.template = template;
    }

    public MongoTemplate getTemplate() {
        return template;
    }

    public void setidConverter(IdConverter idConverter) {
        this.idConverter = idConverter;
    }

    @Override
    public T create(String type, Map<String, Object> body) {
        return create(type, body, type);
    }

    @Override
    public T create(String type, Map<String, Object> body, String collectionName) {
        return create(type, body, new HashMap<String, Object>(), collectionName);
    }

    @Override
    public abstract T create(String type, Map<String, Object> body,
            Map<String, Object> metaData, String collectionName);

    public T create(T record, String collectionName) {
        template.save(record, collectionName);
        LOG.debug(" create a record in collection {} with id {}", new Object[] {
                collectionName, getRecordId(record) });

        return record;
    }

    @Override
    public T findById(String collectionName, String id) {
        Object databaseId = idConverter.toDatabaseId(id);
        LOG.debug("find a record in collection {} with id {}", new Object[] {
                collectionName, id });
        try {
            return template.findById(databaseId, getRecordClass(),
                    collectionName);
        } catch (Exception e) {
            LOG.error("Exception occurred", e);
            return null;
        }
    }

    @Override
    public boolean exists(String collectionName, String id) {
        Object databaseId = idConverter.toDatabaseId(id);
        LOG.debug("find a record in collection {} with id {}", new Object[] {
                collectionName, id });
        try {
            return template.getCollection(collectionName).getCount(
                    new BasicDBObject("_id", databaseId)) != 0L;
        } catch (Exception e) {
            LOG.error("Exception occurred", e);
            return false;
        }
    }

    @Override
    public T findOne(String collectionName, NeutralQuery neutralQuery) {

        // convert the neutral query into a mongo query
        Query mongoQuery = this.queryConverter.convert(collectionName,
                neutralQuery);

        // find and return an entity
        return template.findOne(mongoQuery, getRecordClass(), collectionName);
    }

    @Override
    public Iterable<T> findAll(String collectionName) {
        return findAll(collectionName, new NeutralQuery());
    }

    @Override
    public Iterable<T> findAll(String collectionName, NeutralQuery neutralQuery) {

        // convert the neutral query into a mongo query
        Query mongoQuery = this.queryConverter.convert(collectionName,
                neutralQuery);

        // find and return an instance
        return template.find(mongoQuery, getRecordClass(), collectionName);
    }

    @Override
    public Iterable<String> findAllIds(String collectionName,
            NeutralQuery neutralQuery) {
        if (neutralQuery == null) {
            neutralQuery = new NeutralQuery();
        }
        neutralQuery.setIncludeFields("_id");

        List<String> ids = new ArrayList<String>();
        for (T t : findAll(collectionName, neutralQuery)) {
            ids.add(this.getRecordId(t));
        }
        return ids;
    }

    @Override
    public Iterable<T> findAllByPaths(String collectionName,
            Map<String, String> paths, NeutralQuery neutralQuery) {
        Query mongoQuery = this.queryConverter.convert(collectionName,
                neutralQuery);

        for (Map.Entry<String, String> field : paths.entrySet()) {
            mongoQuery.addCriteria(Criteria.where(field.getKey()).is(
                    field.getValue()));
        }

        // find and return an entity
        return template.find(mongoQuery, getRecordClass(), collectionName);
    }

    @Override
    public long count(String collectionName, NeutralQuery neutralQuery) {
        DBCollection collection = template.getCollection(collectionName);
        if (collection == null) {
            return 0;
        }
        return collection.count(this.queryConverter.convert(collectionName,
                neutralQuery).getQueryObject());
    }

    @Override
    public DBCollection getCollection(String collectionName) {
        return template.getCollection(collectionName);
    }

    @Override
    public abstract boolean update(String collection, T record);

    /**
     * Updates the document inside of Mongo. MongoTemplate will upsert the given
     * document, however since we are specifying IDs in the DAL instead of
     * letting Mongo create the document IDs, this method will check for the
     * existence of a document ID before saving the document.
     * 
     * @param collection
     * @param record
     * @param body
     * @return True if the document was saved
     */
    public boolean update(String collection, T record, Map<String, Object> body) {
        Assert.notNull(record, "The given record must not be null!");
        String id = getRecordId(record);
        if (StringUtils.isEmpty(id)) {
            return false;
        }

        Query query = getUpdateQuery(record);
        T encryptedRecord = getEncryptedRecord(record);
        Update update = getUpdateCommand(encryptedRecord);

        //attempt update
        WriteResult result = template.updateFirst(query, update, collection);
        //if no records were updated, try insert
        //insert goes through the encryption pipeline, so use the unencrypted record
        if (result.getN() == 0) {
            template.insert(record, collection);
        }

        return true;
    }
    
    public WriteResult update(NeutralQuery query, Map<String, Object> update, String collectionName) {
        Query convertedQuery = this.queryConverter.convert(collectionName, query);
        Update convertedUpdate = new Update();
        
        for (Map.Entry<String, Object> entry : update.entrySet()) {
            String operation = entry.getKey();
            @SuppressWarnings("unchecked")
            Map<String, Object> operands = (Map<String, Object>) entry.getValue();
            
            if (operation.equals("push")) {
                for (Map.Entry<String, Object> fieldValues : operands.entrySet()) {
                    convertedUpdate.push(fieldValues.getKey(), fieldValues.getValue());
                }
            } else if (operation.equals("pushAll")) {
                for (Map.Entry<String, Object> fieldValues : operands.entrySet()) {
                    convertedUpdate.pushAll(fieldValues.getKey(), (Object[]) fieldValues.getValue());
                }
            }
        }
        
        return template.updateFirst(convertedQuery, convertedUpdate, collectionName);
    }

    protected abstract Query getUpdateQuery(T entity);

    protected abstract T getEncryptedRecord(T entity);

    protected abstract Update getUpdateCommand(T entity);

    @Override
    public CommandResult execute(DBObject command) {
        return template.executeCommand(command);
    }

    @Override
    public boolean delete(String collectionName, String id) {
        if (id.equals("")) {
            return false;
        }
        T deleted = template.findAndRemove(
                new Query(Criteria.where("_id")
                        .is(idConverter.toDatabaseId(id))), getRecordClass(),
                collectionName);
        LOG.debug("delete a entity in collection {} with id {}", new Object[] {
                collectionName, id });
        return deleted != null;
    }

    @Override
    public void deleteAll(String collectionName) {
        template.getCollection(collectionName).remove(new BasicDBObject());
        LOG.debug("delete all objects in collection {}", collectionName);
    }

    protected void logResults(String collectioName, List<T> results) {
        if (results == null) {
            LOG.debug("find objects in collection {} with total numbers is {}",
                    new Object[] { collectioName, 0 });
        } else {
            LOG.debug("find objects in collection {} with total numbers is {}",
                    new Object[] { collectioName, results.size() });
        }
    }

    protected abstract String getRecordId(T record);

    protected abstract Class<T> getRecordClass();

    @Override
    @Deprecated
    public Iterable<T> findByPaths(String collectionName,
            Map<String, String> paths) {
        Query query = new Query();

        return findByQuery(collectionName, addSearchPathsToQuery(query, paths));
    }

    @Deprecated
    protected Iterable<T> findByQuery(String collectionName, Query query) {
        List<T> results = template
                .find(query, getRecordClass(), collectionName);
        logResults(collectionName, results);
        return results;
    }

    @Override
    @Deprecated
    public Iterable<T> findByQuery(String collectionName, Query query,
            int skip, int max) {
        if (query == null) {
            query = new Query();
        }

        query.skip(skip).limit(max);

        return findByQuery(collectionName, query);
    }

    @Deprecated
    private Query addSearchPathsToQuery(Query query,
            Map<String, String> searchPaths) {
        for (Map.Entry<String, String> field : searchPaths.entrySet()) {
            Criteria criteria = Criteria.where(field.getKey()).is(
                    field.getValue());
            query.addCriteria(criteria);
        }

        return query;
    }

    @Override
    /**The existing collections have been cached
     * to avoid unnecessary DB queries.
     *
     */
    public boolean collectionExists(String collection) {

        return template.collectionExists(collection);
    }

    @Override
    /**
     * This function assumes the collection does not exists
     *
     * @author tke
     */
    public void createCollection(String collection) {
        template.createCollection(collection);
    }

    @Override
    public void ensureIndex(IndexDefinition index, String collection) {

        //TODO - This needs refactoring: template.getDb() is an expensive operations
        // Mongo indexes names(including collection name and namespace) are limited to 128 characters.
        String nsName = (String) index.getIndexOptions().get("name") + collection + "." + template.getDb().getName();
        
        // Verify the length of the name is ready
        if (nsName.length() >= 128) {
            LOG.error("ns and name exceeds 128 characters, failed to create index");
            return;
        }
        
        template.ensureIndex(index, collection);

    }
}

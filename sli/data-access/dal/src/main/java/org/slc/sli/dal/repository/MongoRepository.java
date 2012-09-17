/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.dal.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import org.slc.sli.dal.TenantContext;
import org.slc.sli.dal.convert.IdConverter;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * mongodb implementation of the repository interface that provides basic CRUD
 * and field query methods for all object classes.
 *
 * @author Thomas Shewchuk tshewchuk@wgen.net 3/2/2012 (PI3 US1226)
 *
 */
public abstract class MongoRepository<T> implements Repository<T> {
    protected static final Logger LOG = LoggerFactory.getLogger(MongoRepository.class);

    protected MongoTemplate template;

    protected IdConverter idConverter;

    @Autowired
    private MongoQueryConverter queryConverter;

    private static final String[] COLLECTIONS_EXCLUDED = { "tenant", "userSession", "userAccount", "roles",
            "application", "tenantJobLock" };
    protected static final Set<String> NOT_BY_TENANT = new HashSet<String>(Arrays.asList(COLLECTIONS_EXCLUDED));

    /**
     * The purpose of this method is to add the default parameters to a neutral query. At inception,
     * this method
     * add the Tenant ID to a neutral query.
     *
     * @param query
     *            The query returned is the same as the query passed.
     * @return
     *         The modified neutral query
     */
    protected NeutralQuery addDefaultQueryParams(NeutralQuery query, String collectionName) {
        if (query == null) {
            query = new NeutralQuery();
        }

        if (!template.getDb().getName().equalsIgnoreCase("SLI")) {
            return query;
        }
        // Add tenant ID
        if (!NOT_BY_TENANT.contains(collectionName)) {
            String tenantId = TenantContext.getTenantId();
            // We decided that if tenantId is null then we will query on blank string.
            // This may need to be revisited.
            if (tenantId == null) {
                return query;
            }

            // make sure a criterion for tenantId has not already been added to this query
            boolean addCrit = true;
            List<NeutralCriteria> criteria = query.getCriteria();
            if (criteria != null) {
                ListIterator<NeutralCriteria> li = criteria.listIterator();
                while (li.hasNext()) {
                    if ("metaData.tenantId".equalsIgnoreCase(li.next().getKey())) {
                        addCrit = false;
                        break;
                    }
                }
            }
            // add the tenant ID if it's not already there
            if (addCrit) {
                query.prependCriteria(new NeutralCriteria("metaData.tenantId", "=", tenantId, false));
            }
        }
        return query;
    }

    /**
     * Constructs a Criteria for tenantId. Will return null if collectionName is not restricted by
     * tenantId.
     *
     * @param collectionName
     *            The collection to which the Criteria is to be applied.
     * @return null if the collection is not restricted. Otherwise a Criteria that restricts by
     *         tenant id.
     */
    protected Criteria createTenantCriteria(String collectionName) {
        if (NOT_BY_TENANT.contains(collectionName)) {
            return null;
        }
        String tenantId = TenantContext.getTenantId();

        // We decided that if tenantId is null then we will query on blank string.
        // This may need to be revisited.
        if (tenantId == null) {
            return null;
        }
        Criteria c = new Criteria("metaData.tenantId");
        c.is(tenantId);
        return c;
    }

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
    public abstract T create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName);

    // DE719 -- Not sure how to handle this, since it is using Generics. We
    // will not know until compileTime, what the object will be.
    public T create(T record, String collectionName) {
        template.insert(record, collectionName);
        LOG.debug(" create a record in collection {} with id {}", new Object[] { collectionName, getRecordId(record) });
        return record;
    }

    /**
     * Makes call to mongo template insert() function, and not save (which performs upsert).
     *
     * @param record
     *            Database record to be inserted.
     * @param collectionName
     *            Name of collection to insert record in.
     * @return Successfully inserted record.
     */
    public T insert(T record, String collectionName) {
        template.insert(record, collectionName);
        LOG.debug("Insert a record in collection {} with id {}", new Object[] { collectionName, getRecordId(record) });
        return record;
    }

    /**
     * Makes call to mongo template insert() function, and not save (which performs upsert).
     * Leverages batch insert functionality.
     *
     * @param records Database records to be inserted.
     * @param collectionName Name of collection to insert record in.
     * @return Successfully inserted record.
     */
    @Override
    public List<T> insert(List<T> records, String collectionName) {
        template.insert(records, collectionName);
        LOG.debug("Insert {} records into collection: {}", new Object[] {records.size(), collectionName});
        return records;
    }

    @Override
    public T findById(String collectionName, String id) {
        Object databaseId = idConverter.toDatabaseId(id);
        LOG.debug("find a record in collection {} with id {}", new Object[] { collectionName, id });

        // Enforcing the tenantId query. The rationale for this is all CRUD
        // Operations should be restricted based on tenant.
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL, databaseId));
        this.addDefaultQueryParams(neutralQuery, collectionName);

        // convert the neutral query into a mongo query
        Query mongoQuery = this.queryConverter.convert(collectionName, neutralQuery);

        try {
            return template.findOne(mongoQuery, getRecordClass(), collectionName);
        } catch (Exception e) {
            LOG.error("Exception occurred", e);
            return null;
        }
    }

    @Override
    public boolean exists(String collectionName, String id) {
        Object databaseId = idConverter.toDatabaseId(id);
        LOG.debug("find a record in collection {} with id {}", new Object[] { collectionName, id });

        // We need to restrict counts by tenantId as well. So if the tenantId
        // exist, then we append.
        try {
            String tenantId = TenantContext.getTenantId();
            BasicDBObject obj = null;

            if (tenantId != null && !NOT_BY_TENANT.contains(collectionName)) {

                obj = new BasicDBObject("metaData.tenantId", tenantId);
                obj.append("_id", databaseId);
            } else {
                obj = new BasicDBObject("_id", databaseId);
            }

            return template.getCollection(collectionName).getCount(obj) != 0L;
        } catch (Exception e) {
            LOG.error("Exception occurred", e);
            return false;
        }
    }

    @Override
    public T findOne(String collectionName, NeutralQuery neutralQuery) {

        // Enforcing the tenantId query. The rationale for this is all CRUD
        // Operations should be restricted based on tenant.
        this.addDefaultQueryParams(neutralQuery, collectionName);

        // convert the neutral query into a mongo query
        Query mongoQuery = this.queryConverter.convert(collectionName, neutralQuery);

        // find and return an entity
        return template.findOne(mongoQuery, getRecordClass(), collectionName);
    }

    public T findOne(String collectionName, Query query) {

        // find and return an entity
        return template.findOne(query, getRecordClass(), collectionName);
    }

    @Override
    public Iterable<T> findAll(String collectionName) {
        // Enforcing the tenantId query. The rationale for this is all CRUD
        // Operations should be restricted based on tenant.
        NeutralQuery neutralQuery = new NeutralQuery();
        this.addDefaultQueryParams(neutralQuery, collectionName);

        return findAll(collectionName, neutralQuery);
    }

    @Override
    public Iterable<T> findAll(String collectionName, NeutralQuery neutralQuery) {

        // Enforcing the tenantId query. The rationale for this is all CRUD
        // Operations should be restricted based on tenant.
        this.addDefaultQueryParams(neutralQuery, collectionName);

        // convert the neutral query into a mongo query
        Query mongoQuery = this.queryConverter.convert(collectionName, neutralQuery);

        // find and return an instance
        return template.find(mongoQuery, getRecordClass(), collectionName);
    }

    @Override
    public Iterable<String> findAllIds(String collectionName, NeutralQuery neutralQuery) {
        if (neutralQuery == null) {
            neutralQuery = new NeutralQuery();
        }
        neutralQuery.setIncludeFieldString("_id");

        // Enforcing the tenantId query. The rationale for this is all CRUD
        // Operations should be restricted based on tenant.
        this.addDefaultQueryParams(neutralQuery, collectionName);

        List<String> ids = new ArrayList<String>();
        for (T t : findAll(collectionName, neutralQuery)) {
            ids.add(this.getRecordId(t));
        }
        return ids;
    }

    @Override
    public Iterable<T> findAllByPaths(String collectionName, Map<String, String> paths, NeutralQuery neutralQuery) {

        // Enforcing the tenantId query. The rationale for this is all CRUD
        // Operations should be restricted based on tenant.
        this.addDefaultQueryParams(neutralQuery, collectionName);
        Query mongoQuery = this.queryConverter.convert(collectionName, neutralQuery);

        for (Map.Entry<String, String> field : paths.entrySet()) {
            mongoQuery.addCriteria(Criteria.where(field.getKey()).is(field.getValue()));
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
        this.addDefaultQueryParams(neutralQuery, collectionName);
        return collection.count(this.queryConverter.convert(collectionName, neutralQuery).getQueryObject());
    }

    @Override
    public long count(String collectionName, Query query) {
        DBCollection collection = template.getCollection(collectionName);
        if (collection == null) {
            return 0;
        }
        return collection.count(query.getQueryObject());
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
        Criteria crit = this.createTenantCriteria(collection);
        if (crit != null) {
            query.addCriteria(crit);
        }

        T encryptedRecord = getEncryptedRecord(record);
        Update update = getUpdateCommand(encryptedRecord);

        // attempt update
        WriteResult result = template.updateFirst(query, update, collection);
        // if no records were updated, try insert
        // insert goes through the encryption pipeline, so use the unencrypted record
        if (result.getN() == 0) {
            template.insert(record, collection);
        }

        return true;
    }

    public WriteResult updateFirst(NeutralQuery query, Map<String, Object> update, String collectionName) {
        // Enforcing the tenantId query. The rationale for this is all CRUD
        // Operations should be restricted based on tenant.
        this.addDefaultQueryParams(query, collectionName);

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
            } else if (operation.equals("addToSet")) {
                for (Map.Entry<String, Object> fieldValues : operands.entrySet()) {
                    convertedUpdate.addToSet(fieldValues.getKey(), fieldValues.getValue());
                }
            }
        }
        return template.updateFirst(convertedQuery, convertedUpdate, collectionName);
    }

    public WriteResult updateMulti(NeutralQuery query, Map<String, Object> update, String collectionName) {
        // Enforcing the tenantId query. The rationale for this is all CRUD
        // Operations should be restricted based on tenant.
        this.addDefaultQueryParams(query, collectionName);

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
            } else if (operation.equals("addToSet")) {
                for (Map.Entry<String, Object> fieldValues : operands.entrySet()) {
                    convertedUpdate.addToSet(fieldValues.getKey(), fieldValues.getValue());
                }
            }
        }
        return template.updateMulti(convertedQuery, convertedUpdate, collectionName);
    }

    @Override
    public boolean doUpdate(String collection, String id, Update update) {
        return template.updateFirst(Query.query(new Criteria("_id").is(id)), update, collection).getLastError().ok();
    }

    @Override
    public boolean doUpdate(String collection, NeutralQuery query, Update update) {
        return template.updateFirst(queryConverter.convert(collection, query), update, collection).getLastError().ok();
    }

    protected abstract Query getUpdateQuery(T entity);

    protected abstract T getEncryptedRecord(T entity);

    protected abstract Update getUpdateCommand(T entity);

    @Override
    public CommandResult execute(DBObject command) {
        // Due to security concerns, we are not going to support this method
        throw new UnsupportedOperationException();
        // return template.executeCommand(command);
    }

    @Override
    public boolean delete(String collectionName, String id) {
        if (id.equals("")) {
            return false;
        }

        Query query = null;
        Criteria idCrit = Criteria.where("_id").is(idConverter.toDatabaseId(id));
        Criteria tenantCrit = createTenantCriteria(collectionName);

        if (tenantCrit != null) {
            query = new Query(tenantCrit);
            query.addCriteria(idCrit);
        } else {
            query = new Query(idCrit);
        }

        T deleted = template.findAndRemove(query, getRecordClass(), collectionName);
        LOG.debug("delete a entity in collection {} with id {}", new Object[] { collectionName, id });
        return deleted != null;
    }

    @Override
    public void deleteAll(String collectionName) {
        // We decided that if TenantId is null, then we will search on blank.
        // This option may need to be revisted.
        String tenantId = TenantContext.getTenantId();
        BasicDBObject obj = null;

        if (tenantId != null && !NOT_BY_TENANT.contains(collectionName)) {
            obj = new BasicDBObject("metaData.tenantId", tenantId);
        } else {
            obj = new BasicDBObject();
        }

        template.getCollection(collectionName).remove(obj);
        LOG.debug("delete all objects in collection {}", collectionName);
    }

    protected void logResults(String collectioName, List<T> results) {
        if (results == null) {
            LOG.debug("find objects in collection {} with total numbers is {}", new Object[] { collectioName, 0 });
        } else {
            LOG.debug("find objects in collection {} with total numbers is {}",
                    new Object[] { collectioName, results.size() });
        }
    }

    protected abstract String getRecordId(T record);

    protected abstract Class<T> getRecordClass();

    @Override
    @Deprecated
    /**
     * @Deprecated
     * "This is a deprecated method that should only be used by the ingestion ID Normalization code.
     * It is not tenant-safe meaning clients of this method must include tenantId in the metaData block"
     */
    public Iterable<T> findByPaths(String collectionName, Map<String, String> paths) {
        NeutralQuery neutralQuery = new NeutralQuery();
        Query query = this.queryConverter.convert(collectionName, neutralQuery);
        return findByQuery(collectionName, addSearchPathsToQuery(query, paths));
    }

    @Deprecated
    protected Iterable<T> findByQuery(String collectionName, Query query) {
        List<T> results = template.find(query, getRecordClass(), collectionName);
        logResults(collectionName, results);
        return results;
    }

    /**
     * @Deprecated
     *             "This is a deprecated method that should only be used by the ingestion ID
     *             Normalization code.
     *             It is not tenant-safe meaning clients of this method must include tenantId in the
     *             metaData block"
     */
    @Override
    @Deprecated
    public Iterable<T> findByQuery(String collectionName, Query query, int skip, int max) {

        if (query == null) {
            query = new Query();
        }

        query.skip(skip).limit(max);

        return findByQuery(collectionName, query);
    }

    @Deprecated
    private Query addSearchPathsToQuery(Query query, Map<String, String> searchPaths) {
        for (Map.Entry<String, String> field : searchPaths.entrySet()) {
            Criteria criteria = Criteria.where(field.getKey()).is(field.getValue());
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

        // TODO - This needs refactoring: template.getDb() is an expensive operations
        // Mongo indexes names(including collection name and namespace) are limited to 128
        // characters.
        String nsName = (String) index.getIndexOptions().get("name") + collection + "." + template.getDb().getName();

        // Verify the length of the name is ready
        if (nsName.length() >= 128) {
            LOG.error("ns and name exceeds 128 characters, failed to create index");
            return;
        }
        template.ensureIndex(index, collection);

        LOG.info("Success!  Index for {} has been created, details {} ", collection, index);
    }

    @Override
    public boolean patch(String type, String collectionName, String id, Map<String, Object> newValues) {

        if (id.equals("")) {
            return false;
        }

        // prepare to find desired record to be patched
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(idConverter.toDatabaseId(id)));
        query.addCriteria(createTenantCriteria(collectionName));

        //prepare update operation for record to be patched
        Update update = new Update();
        for (Entry<String, Object> patch : newValues.entrySet()) {
            update.set("body." + patch.getKey(), patch.getValue());
        }

        WriteResult result = template.updateFirst(query, update, collectionName);

        return (result.getN() == 1);
    }

    /**
     * Sets the write concern of the template. Support options defined in Mongo's WriteConcern
     * class.
     *
     * @see com.mongodb.WriteConcern
     */
    @Override
    public void setWriteConcern(String writeConcern) {
        try {
            WriteConcern concern = WriteConcern.valueOf(writeConcern);
            template.setWriteConcern(concern);
        } catch (RuntimeException ex) {
            LOG.warn("Unknown write concern", writeConcern);
            // When in doubt, play it (Replicas) safe.
            template.setWriteConcern(WriteConcern.REPLICAS_SAFE);
        }
    }

    @Override
    public List<DBCollection> getCollections(boolean includeSystemCollections) {
        List<DBCollection> collections = new ArrayList<DBCollection>();

        for (String name : getTemplate().getCollectionNames()) {

            if (!includeSystemCollections && name.startsWith("system.")) {
                continue;
            }
            collections.add(getTemplate().getCollection(name));
        }
        return collections;
    }
}

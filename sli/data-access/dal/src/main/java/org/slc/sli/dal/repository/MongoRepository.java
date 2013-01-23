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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import org.slc.sli.common.util.tenantdb.TenantContext;
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

    /**
     * Collections that are not specific for a tenant.
     * Includes the likes of 'realm', 'application', 'userSession', etc.
     */
    private Set<String> tenantAgnosticCollections;

    MongoQueryConverter getQueryConverter() {
        return queryConverter;
    }

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
    protected NeutralQuery addDefaultQueryParams(NeutralQuery origQuery, String collectionName) {
        NeutralQuery query = origQuery == null ? new NeutralQuery() : origQuery;

        // TODO: this is assuming that the staging db is the only non-sli db. remove all of this
        // eventually.
        if (template.getDb().getName().equalsIgnoreCase("is")) {
            return query;
        }

        // Add tenant ID
        if (!isTenantAgnostic(collectionName)) {
            String tenantId = TenantContext.getTenantId();
            // We decided that if tenantId is null then we will query on blank string.
            // This may need to be revisited.
            if (tenantId == null) {
                return query;
            }

            // make sure a criterion for tenantId has not already been added to this query
            // boolean addCrit = true;
            // List<NeutralCriteria> criteria = query.getCriteria();
            // if (criteria != null) {
            // ListIterator<NeutralCriteria> li = criteria.listIterator();
            // while (li.hasNext()) {
            // if ("metaData.tenantId".equalsIgnoreCase(li.next().getKey())) {
            // addCrit = false;
            // break;
            // }
            // }
            // }
            // // add the tenant ID if it's not already there
            // if (addCrit) {
            // query.prependCriteria(new NeutralCriteria("metaData.tenantId", "=", tenantId,
            // false));
            // }
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
        if (isTenantAgnostic(collectionName)) {
            return null;
        }
        String tenantId = TenantContext.getTenantId();

        // We decided that if tenantId is null then we will query on blank string.
        // This may need to be revisited.
        if (tenantId == null) {
            return null;
        }
        return new Criteria();
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
        guideIfTenantAgnostic(collectionName);
        template.insert(record, collectionName);
        LOG.debug("Insert a record in collection {} with id {}", new Object[] { collectionName, getRecordId(record) });
        return record;
    }

    /**
     * Makes call to mongo template insert() function, and not save (which performs upsert).
     * Leverages batch insert functionality.
     *
     * @param records
     *            Database records to be inserted.
     * @param collectionName
     *            Name of collection to insert record in.
     * @return Successfully inserted record.
     */
    @Override
    public List<T> insert(List<T> records, String collectionName) {
        guideIfTenantAgnostic(collectionName);
        template.insert(records, collectionName);
        LOG.debug("Insert {} records into collection: {}", new Object[] { records.size(), collectionName });
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
            return findOne(collectionName, mongoQuery);
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
            BasicDBObject obj = new BasicDBObject("_id", databaseId);

            return getCollection(collectionName).getCount(obj) != 0L;
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
        return findOne(collectionName, mongoQuery);
    }

    public T findOne(String collectionName, Query query) {
        guideIfTenantAgnostic(collectionName);
        return template.findOne(query, getRecordClass(), collectionName);
    }

    @Override
    public Iterable<T> findAll(String collectionName, NeutralQuery origNeutralQuery) {

        NeutralQuery neutralQuery = origNeutralQuery == null ? new NeutralQuery() : origNeutralQuery;

        // Enforcing the tenantId query. The rationale for this is all CRUD
        // Operations should be restricted based on tenant.
        this.addDefaultQueryParams(neutralQuery, collectionName);

        // convert the neutral query into a mongo query
        Query mongoQuery = this.queryConverter.convert(collectionName, neutralQuery);

        // always call guideIfTenantAgnostic - this sets threadlocal flag
        if (!guideIfTenantAgnostic(collectionName) && TenantContext.getTenantId() == null) {

            return findAllAcrossTenants(collectionName, mongoQuery);
        } else {
            // find and return an instance
            return findAll(mongoQuery, collectionName);
        }
    }

    private Iterable<T> findAll(Query query, String collectionName) {
        guideIfTenantAgnostic(collectionName);
        return template.find(query, getRecordClass(), collectionName);
    }

    protected abstract Iterable<T> findAllAcrossTenants(String collectionName, Query mongoQuery);

    @Override
    public Iterable<String> findAllIds(String collectionName, NeutralQuery origNeutralQuery) {
        NeutralQuery neutralQuery = origNeutralQuery == null ? new NeutralQuery() : origNeutralQuery;
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

    public Iterable<T> findAllByPaths(String collectionName, Map<String, String> paths, NeutralQuery neutralQuery) {

        // Enforcing the tenantId query. The rationale for this is all CRUD
        // Operations should be restricted based on tenant.
        this.addDefaultQueryParams(neutralQuery, collectionName);

        for (Map.Entry<String, String> field : paths.entrySet()) {
            neutralQuery.addCriteria(new NeutralCriteria(field.getKey(), "=", field.getValue(), false));
        }

        // find and return an entity
        guideIfTenantAgnostic(collectionName);
        Query mongoQuery = this.queryConverter.convert(collectionName, neutralQuery);
        return template.find(mongoQuery, getRecordClass(), collectionName);
    }

    @Override
    public long count(String collectionName, NeutralQuery neutralQuery) {
        guideIfTenantAgnostic(collectionName);
        return this.count(collectionName, this.queryConverter.convert(collectionName, neutralQuery).getQueryObject());
    }

    @Override
    public long count(String collectionName, Query query) {
        return count(collectionName, query.getQueryObject());
    }

    private long count(String collectionName, DBObject queryObject) {
        DBCollection collection = getCollection(collectionName);
        if (collection == null) {
            return 0;
        }
        return collection.count(queryObject);
    }

    @Override
    public DBCollection getCollection(String collectionName) {
        guideIfTenantAgnostic(collectionName);
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

        // attempt upsert
        WriteResult result = upsert(query, update, collection);
        // insert goes through the encryption pipeline, so use the unencrypted record
        if (result.getError() != null) {
            LOG.error("Update/upsert on collection {} failed with error: {}", collection, result.getError());
            return false;
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

        return updateFirst(convertedQuery, convertedUpdate, collectionName);
    }

    private WriteResult updateFirst(Query query, Update update, String collectionName) {
        guideIfTenantAgnostic(collectionName);
        return template.updateFirst(query, update, collectionName);
    }

    private WriteResult upsert(Query query, Update update, String collectionName) {
        guideIfTenantAgnostic(collectionName);
        return template.upsert(query, update, collectionName);
    }

    @Override
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
        guideIfTenantAgnostic(collectionName);
        return template.updateMulti(convertedQuery, convertedUpdate, collectionName);
    }

    @Override
    public boolean doUpdate(String collection, NeutralQuery query, Update update) {
        return updateFirst(queryConverter.convert(collection, query), update, collection).getLastError().ok();
    }

    protected abstract Query getUpdateQuery(T entity);

    protected abstract T getEncryptedRecord(T entity);

    protected abstract Update getUpdateCommand(T entity);

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

        guideIfTenantAgnostic(collectionName);
        T deleted = template.findAndRemove(query, getRecordClass(), collectionName);
        LOG.debug("delete a entity in collection {} with id {}", new Object[] { collectionName, id });
        return deleted != null;
    }

    public void deleteAll(String collectionName) {
        // We decided that if TenantId is null, then we will search on blank.
        // This option may need to be revisted.
        String tenantId = TenantContext.getTenantId();
        BasicDBObject obj = new BasicDBObject();

        guideIfTenantAgnostic(collectionName);
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

    @Override
    public void deleteAll(String collectionName, NeutralQuery query) {
        this.addDefaultQueryParams(query, collectionName);
        Query convertedQuery = this.queryConverter.convert(collectionName, query);
        guideIfTenantAgnostic(collectionName);
        template.remove(convertedQuery, collectionName);

    }

    protected abstract String getRecordId(T record);

    protected abstract Class<T> getRecordClass();

    @Deprecated
    protected Iterable<T> findByQuery(String collectionName, Query query) {
        Iterable<T> results = findAll(query, collectionName);

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
    public Iterable<T> findByQuery(String collectionName, Query origQuery, int skip, int max) {
        Query query = origQuery == null ? new Query() : origQuery;

        query.skip(skip).limit(max);

        return findByQuery(collectionName, query);
    }

    @Override
    /**The existing collections have been cached
     * to avoid unnecessary DB queries.
     *
     */
    public boolean collectionExists(String collection) {
        guideIfTenantAgnostic(collection);
        return template.collectionExists(collection);
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
        // prepare update operation for record to be patched
        Update update = new Update();
        for (Entry<String, Object> patch : newValues.entrySet()) {
            update.set("body." + patch.getKey(), patch.getValue());
        }

        WriteResult result = updateFirst(query, update, collectionName);

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

    protected Set<String> getCollectionNames() {
        return template.getCollectionNames();
    }

    @Override
    public List<DBCollection> getCollections(boolean includeSystemCollections) {
        List<DBCollection> collections = new ArrayList<DBCollection>();

        for (String name : getCollectionNames()) {

            if (!includeSystemCollections && name.startsWith("system.")) {
                continue;
            }
            collections.add(getCollection(name));
        }
        return collections;
    }

    public void setTenantAgnosticCollections(Set<String> tenantAgnosticCollections) {
        this.tenantAgnosticCollections = tenantAgnosticCollections;
    }

    /**
     * Checks if this is a tenant-specific collection based on a set provided in spring
     * configuration.
     *
     * @param collectionName
     * @return <code>true</code> if the collection is in the non-tenant-specific set.
     */
    protected boolean isTenantAgnostic(String collectionName) {
        return tenantAgnosticCollections.contains(collectionName);
    }

    /**
     * Set a boolean value in TenantContext threadlocal store which signals whether this collection
     * is tenant-specific. The method should be used before MongoTemplate calls to ensure that the
     * correct database is used.
     *
     * @param collectionName
     * @return <code>true</code> if this collection is tenant agnostic.
     */
    protected boolean guideIfTenantAgnostic(String collectionName) {
        boolean isTenantAgnostic = isTenantAgnostic(collectionName);
        TenantContext.setIsSystemCall(isTenantAgnostic);
        return isTenantAgnostic;
    }

    @Override
    @SuppressWarnings("PMD")
    public T findAndUpdate(String collectionName, NeutralQuery neutralQuery, Update update) {
        return null;
    }
}

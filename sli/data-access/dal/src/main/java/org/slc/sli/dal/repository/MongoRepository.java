package org.slc.sli.dal.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slc.sli.dal.TenantContext;
import org.slc.sli.dal.convert.IdConverter;
import org.slc.sli.domain.NeutralCriteria;
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
    protected static final Logger LOG = LoggerFactory.getLogger(MongoRepository.class);
    
    protected MongoTemplate template;
    
    protected IdConverter idConverter;
    
    @Autowired
    private MongoQueryConverter queryConverter;
    
    private static final String[] collectionsExcluded = { "tenant", "userSession", "realm", "userAccount", "roles",
            "application", "applicationAuthorization" };
    private static final Set<String> NOT_BY_TENANT = new HashSet<String>(Arrays.asList(collectionsExcluded));
    
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
        
        // Add tenant ID
        if (!NOT_BY_TENANT.contains(collectionName)) {
            String tenantId = TenantContext.getTenantId();
            // We decided that if tenantId is null then we will query on blank string.
            // This may need to be revisited.
            if (tenantId == null) {
                tenantId = "";
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
        if (!NOT_BY_TENANT.contains(collectionName)) {
            return null;
        }
        String tenantId = TenantContext.getTenantId();
        
        // We decided that if tenantId is null then we will query on blank string.
        // This may need to be revisited.
        if (tenantId == null) {
            tenantId = "";
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
        stampTenantId(body);
        return create(type, body, type);
    }
    
    protected void stampTenantId(Map<String, Object> body) {
   
        if (body == null) {
            return;
        }
        
        Map<String, Object> metaData = (Map<String, Object>) body.get("metaData");
        if (metaData == null) {
            metaData = new HashMap<String, Object>();
        }
        
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            tenantId = "";
        }
        
        metaData.put("tenantId", tenantId);
        body.put("metaData", metaData);
    }
    
    @Override
    public T create(String type, Map<String, Object> body, String collectionName) {
        stampTenantId(body);
        return create(type, body, new HashMap<String, Object>(), collectionName);
    }
    
    @Override
    public abstract T create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName);
    
    // DE719 -- Not sure how to handle this, since it is using Generics. We
    // will not know until compileTime, what the object will be.
    public T create(T record, String collectionName) {
        template.save(record, collectionName);
        LOG.debug(" create a record in collection {} with id {}", new Object[] { collectionName, getRecordId(record) });
        
        return record;
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
            // return template.findById(databaseId, getRecordClass(),
            // collectionName);
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
            if (tenantId == null) {
                tenantId = "";
            }
            BasicDBObject obj = new BasicDBObject("metaData.tenantId", tenantId);
            obj.append("_id", databaseId);
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
        neutralQuery.setIncludeFields("_id");
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
        this.stampTenantId(body);
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
    
    public WriteResult update(NeutralQuery query, Map<String, Object> update, String collectionName) {
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
            }
        }
        
        return template.updateFirst(convertedQuery, convertedUpdate, collectionName);
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
        if (tenantId == null) {
            tenantId = "";
        }
        BasicDBObject obj = new BasicDBObject("metaData.tenantId", tenantId);
        
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
    public Iterable<T> findByPaths(String collectionName, Map<String, String> paths) {
        
        // Enforcing the tenantId query. The rationale for this is all CRUD
        // Operations should be restricted based on tenant.
        NeutralQuery neutralQuery = new NeutralQuery();
        this.addDefaultQueryParams(neutralQuery, collectionName);
        
        Query query = this.queryConverter.convert(collectionName, neutralQuery);
        
        return findByQuery(collectionName, addSearchPathsToQuery(query, paths));
    }
    
    @Deprecated
    protected Iterable<T> findByQuery(String collectionName, Query query) {
        List<T> results = template.find(query, getRecordClass(), collectionName);
        logResults(collectionName, results);
        return results;
    }
    
    @Override
    @Deprecated
    public Iterable<T> findByQuery(String collectionName, Query query, int skip, int max) {
        
        if (query == null) {
            query = new Query();
        }
        
        // Enforcing the tenantId query. The rationale for this is all CRUD
        // Operations should be restricted based on tenant.
        Criteria criteria = createTenantCriteria(collectionName);
        if (criteria != null) {
            query.addCriteria(criteria);
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
        
    }
}

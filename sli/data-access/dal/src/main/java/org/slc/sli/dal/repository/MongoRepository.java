package org.slc.sli.dal.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

import org.slc.sli.common.util.threadutil.ThreadLocalStorage;
import org.slc.sli.dal.convert.IdConverter;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * mongodb implementation of the repository interface that provides basic
 * CRUD and field query methods for all object classes.
 *
 * @author Thomas Shewchuk tshewchuk@wgen.net 3/2/2012 (PI3 US1226)
 *
 */

public abstract class MongoRepository<T> implements Repository<T> {
    protected static final Logger LOG = LoggerFactory.getLogger(MongoRepository.class);

    protected ThreadLocalStorage threadStore = new ThreadLocalStorage("" + UUID.randomUUID().toString());

    protected MongoTemplate template;

    protected IdConverter idConverter;

    @Autowired
    protected MongoQueryConverter queryConverter;

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

    public T create(T record, String collectionName) {
        template.save(record, getComposedCollectionName(collectionName));
        LOG.debug(" create a record in collection {} with id {}", new Object[] {
                getComposedCollectionName(collectionName), getRecordId(record) });
        return record;
    }

    @Override
    public T findById(String collectionName, String id) {
        Object databaseId = idConverter.toDatabaseId(id);
        LOG.debug("find a record in collection {} with id {}", new Object[] {
                getComposedCollectionName(collectionName), id });
        try {
            return template.findById(databaseId, getRecordClass(), getComposedCollectionName(collectionName));
        } catch (Exception e) {
            LOG.error("Exception occurred", e);
            return null;
        }
    }

    @Override
    public boolean exists(String collectionName, String id) {
        Object databaseId = idConverter.toDatabaseId(id);
        LOG.debug("find a record in collection {} with id {}", new Object[] {
                getComposedCollectionName(collectionName), id });
        try {
            return template.getCollection(getComposedCollectionName(collectionName)).getCount(new BasicDBObject("_id", databaseId)) != 0L;
        } catch (Exception e) {
            LOG.error("Exception occurred", e);
            return false;
        }
    }

    @Override
    public T findOne(String collectionName, NeutralQuery neutralQuery) {

        //convert the neutral query into a mongo query
        Query mongoQuery = this.queryConverter.convert(getComposedCollectionName(collectionName), neutralQuery);

        // find and return an entity
        return template.findOne(mongoQuery, getRecordClass(), getComposedCollectionName(collectionName));
    }

    @Override
    public Iterable<T> findAll(String collectionName) {
        return findAll(collectionName, new NeutralQuery());
    }

    @Override
    public Iterable<T> findAll(String collectionName, NeutralQuery neutralQuery) {

        //convert the neutral query into a mongo query
        Query mongoQuery = this.queryConverter.convert(getComposedCollectionName(collectionName), neutralQuery);

        // find and return an instance
        return template.find(mongoQuery, getRecordClass(), getComposedCollectionName(collectionName));
    }

    @Override
    public Iterable<String> findAllIds(String collectionName, NeutralQuery neutralQuery) {
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
    public Iterable<T> findAllByPaths(String collectionName, Map<String, String> paths, NeutralQuery neutralQuery) {
        Query mongoQuery = this.queryConverter.convert(getComposedCollectionName(collectionName), neutralQuery);

        for (Map.Entry<String, String> field : paths.entrySet()) {
            mongoQuery.addCriteria(Criteria.where(field.getKey()).is(field.getValue()));
        }

        // find and return an entity
        return template.find(mongoQuery, getRecordClass(), getComposedCollectionName(collectionName));
    }

    @Override
    public long count(String collectionName, NeutralQuery neutralQuery) {
        DBCollection collection = template.getCollection(getComposedCollectionName(collectionName));
        if (collection == null) {
            return 0;
        }
        return collection.count(this.queryConverter.convert(getComposedCollectionName(collectionName), neutralQuery)
                .getQueryObject());
    }

    @Override
    public DBCollection getCollection(String collectionName) {
        return template.getCollection(getComposedCollectionName(collectionName));
    }

    @Override
    public abstract boolean update(String collection, T record);

    public boolean update(String collection, T record, Map<String, Object> body) {
        Assert.notNull(record, "The given record must not be null!");
        String id = getRecordId(record);
        if (id.equals("")) {
            return false;
        }

        if (exists(collection, id)) {
            template.save(record, collection);

            return true;
        }
//        WriteResult result = template.updateFirst(new Query(Criteria.where("_id").is(idConverter.toDatabaseId(id))),
//                new Update().set("body", body), collection);
//        LOG.debug("update a record in collection {} with id {}", new Object[] { collection, id });
//        return result.getN() == 1;
        return false;
    }

    @Override
    public CommandResult execute(DBObject command) {
        return template.executeCommand(command);
    }

    @Override
    public boolean delete(String collectionName, String id) {
        if (id.equals("")) {
            return false;
        }
        T deleted = template.findAndRemove(new Query(Criteria.where("_id").is(idConverter.toDatabaseId(id))),
                getRecordClass(), getComposedCollectionName(collectionName));
        LOG.debug("delete a entity in collection {} with id {}", new Object[] {
                getComposedCollectionName(collectionName), id });
        return deleted != null;
    }

    @Override
    public void deleteAll(String collectionName) {
        for (T t : this.findAll(collectionName)) {
            this.delete(collectionName, getRecordId(t));
        }
        LOG.debug("delete all objects in collection {}", getComposedCollectionName(collectionName));
    }

    protected void logResults(String collectioName, List<T> results) {
        if (results == null) {
            LOG.debug("find objects in collection {} with total numbers is {}", new Object[] { collectioName, 0 });
        } else {
            LOG.debug("find objects in collection {} with total numbers is {}",
                    new Object[] { collectioName, results.size() });
        }
    }

    /**
     * returns collection name with a append of collectionGroupingIdentifier (if
     * collectionGroupingFlag is set to true)
     *
     * @param collectionName
     * @return
     */
    protected String getComposedCollectionName(String collectionName) {
        if (isCollectionGrouping()) {
            return collectionName + "_" + getCollectionGroupingIdentifier();
        }

        return collectionName;
    }

    public boolean isCollectionGrouping() {
        if (threadStore.get("collectionGroupingFlag") != null) {
            return (Boolean) threadStore.get("collectionGroupingFlag");
        }

        return false;
    }

    public void setCollectionGrouping(boolean collectionGrouping) {
        threadStore.put("collectionGroupingFlag", collectionGrouping);
    }

    public String getCollectionGroupingIdentifier() {
        return (String) threadStore.get("collectionGroupingIdentifier");
    }

    public void setCollectionGroupingIdentifier(String collectionGroupingIdentifier) {
        threadStore.put("collectionGroupingIdentifier", collectionGroupingIdentifier);
    }

    protected abstract String getRecordId(T record);

    protected abstract Class<T> getRecordClass();

    @Override
    @Deprecated
    public Iterable<T> findByPaths(String collectionName, Map<String, String> paths) {
        Query query = new Query();

        return findByQuery(collectionName, addSearchPathsToQuery(query, paths));
    }

    @Deprecated
    protected Iterable<T> findByQuery(String collectionName, Query query) {
        List<T> results = template.find(query, getRecordClass(), getComposedCollectionName(collectionName));
        logResults(getComposedCollectionName(collectionName), results);
        return results;
    }

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
}

package org.slc.sli.dal.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.DBCollection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.dal.convert.IdConverter;
import org.slc.sli.domain.SmartQuery;
import org.slc.sli.domain.Repository;

/**
 * mongodb implementation of the repository interface that provides basic
 * CRUD and field query methods for all object classes.
 *
 * @author Thomas Shewchuk tshewchuk@wgen.net 3/2/2012 (PI3 US1226)
 *
 */

public abstract class MongoRepository<T> implements Repository<T> {
    private static final Logger LOG = LoggerFactory.getLogger(MongoRepository.class);

    protected MongoTemplate template;

    public void setTemplate(MongoTemplate template) {
        this.template = template;
    }

    private Class<T> clazz;

    public void setClass(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Autowired
    protected IdConverter idConverter;

    @Override
    abstract public T find(String collectionName, String id);

    @Override
    public T find(String collectionName, Map<String, String> queryParameters) {
        // turn query parameters into a Mongo-specific query
        Query query = createQuery(queryParameters);
        // find and return an object
        return template.findOne(query, clazz, collectionName);
    }

    @Override
    public Iterable<T> findAll(String collectionName, Map<String, String> queryParameters) {
        // turn query parameters into a Mongo-specific query
        Query query = createQuery(queryParameters);
        // find and return an object
        return template.find(query, clazz, collectionName);
    }

    @Override
    public Iterable<T> findAll(String collectionName, SmartQuery query) {
        // turn query parameters into a Mongo-specific query
        Query mongoQuery = convertToQuery(query);

        // find and return an instance
        return template.find(mongoQuery, clazz, collectionName);
    }

    /**
     * Converts a SmartQuery to a MongoQuery
     *
     * @param query
     * @return
     */
    protected Query convertToQuery(SmartQuery query) {
        Query mongoQuery = new Query();
        final String mongoBody = "body.";

        // Include fields
        if (query.getIncludeFields() != null) {
            mongoQuery.fields().include(mongoBody + query.getIncludeFields());
        }

        // Exclude fields
        if (query.getExcludeFields() != null) {
            mongoQuery.fields().exclude(mongoBody + query.getExcludeFields());
        }

        // Sorting
        if (query.getSortBy() != null) {
            if (query.getSortOrder() != null) {
                Order sortOrder = query.getSortOrder().equals(SmartQuery.SortOrder.ascending) ? Order.ASCENDING
                        : Order.DESCENDING;
                mongoQuery.sort().on(mongoBody + query.getSortBy(), sortOrder);
            } else { // default to ascending order
                mongoQuery.sort().on(mongoBody + query.getSortBy(), Order.ASCENDING);
            }
        }

        // Limit
        if (query.getLimit() != 0) {
            mongoQuery.limit(query.getLimit());
        }

        // Offset
        if (query.getOffset() != 0) {
            mongoQuery.skip(query.getOffset());
        }

        Map<String, String> fields = query.getFields();

        // _id field
        final String mongoId = "_id";

        if (fields.containsKey(mongoId)) {
            String fieldId = fields.get(mongoId);

            if (fieldId != null) {
                String[] ids = fieldId.split(",");
                List<Object> databaseIds = new ArrayList<Object>();
                for (String id : ids) {
                    Object databaseId = idConverter.toDatabaseId(id);
                    if (databaseId == null) {
                        LOG.debug("Unable to process id {}", new Object[] { id });
                    }
                    databaseIds.add(databaseId);
                }
                mongoQuery.addCriteria(Criteria.where(mongoId).in(databaseIds));
            }
            fields.remove(mongoId);
        }

        // Query fields
        for (Map.Entry<String, String> entry : query.getFields().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value != null) {
                mongoQuery.addCriteria(Criteria.where(mongoBody + key).is(value));
            }
        }
        return mongoQuery;
    }

    /**
     * Constructs a mongo-specific Query object from a map of key/value pairs. Contains special
     * cases when the key is "_id", "includeFields",
     * "excludeFields", "skip", and "limit". All other keys are added to the query as criteria
     * specifying a field to search for (in the object's
     * body).
     *
     * @param queryParameters
     *            all parameters to be included in query
     * @param converter
     *            used to convert human readable IDs into GUIDs (if queryParameters contains "_id"
     *            key)
     * @return query object compatible with Mongo containing all parameters specified in the
     *         original map
     *
     */
    protected Query createQuery(Map<String, String> queryParameters) {
        Query query = new Query();

        if (queryParameters == null) {
            return query;
        }

        // read each entry in map
        for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
            String key = entry.getKey();

            // id field needs to be translated
            if (key.equals("_id")) {
                String id = entry.getValue();
                if (id != null) {
                    Object databaseId = idConverter.toDatabaseId(id);
                    if (databaseId == null) {
                        LOG.debug("Unable to process id {}", new Object[] { id });
                        return null;
                    }
                    query.addCriteria(Criteria.where(entry.getKey()).is(databaseId));
                }
            } else if (key.equals("includeFields")) { // specific field(s) to include in result set
                String includeFields = entry.getValue();
                if (includeFields != null) {
                    for (String includeField : includeFields.split(",")) {
                        LOG.debug("Including field " + includeField + " in resulting body");
                        query.fields().include("body." + includeField);
                    }
                }
            } else if (key.equals("excludeFields")) { // specific field(s) to exclude from result
                                                      // set
                String excludeFields = entry.getValue();
                if (excludeFields != null) {
                    for (String excludeField : excludeFields.split(",")) {
                        LOG.debug("Excluding field " + excludeField + " from resulting body");
                        query.fields().exclude("body." + excludeField);
                    }
                }
            } else if (key.equals("skip")) { // skip to record X instead of starting at the
                                             // beginning
                String skip = entry.getValue();
                if (skip != null) {
                    query.skip(Integer.parseInt(skip));
                }
            } else if (key.equals("limit")) { // display X results instead of all of them
                String limit = entry.getValue();
                if (limit != null) {
                    query.limit(Integer.parseInt(limit));
                }
            } else { // query param on record
                String value = entry.getValue();
                if (value != null) {
                    query.addCriteria(Criteria.where("body." + key).is(value));
                }
            }
        }

        return query;
    }

    @Override
    public Iterable<T> findAll(String collectionName, int skip, int max) {
        List<T> results = template.find(new Query().skip(skip).limit(max), clazz, collectionName);
        logResults(collectionName, results);
        return results;
    }

    @Override
    abstract public boolean update(String collection, T object);

    @Override
    public T create(String type, Map<String, Object> body) {
        return create(type, body, type);
    }

    @Override
    public T create(String type, Map<String, Object> body, String collectionName) {
        return create(type, body, new HashMap<String, Object>(), collectionName);
    }

    @Override
    abstract public T create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName);

    @Override
    abstract public boolean delete(String collectionName, String id);

    @Override
    public Iterable<T> findByFields(String collectionName, Map<String, String> fields, int skip, int max) {
        return findByPaths(collectionName, convertBodyToPaths(fields), skip, max);
    }

    @Override
    public Iterable<T> findByPaths(String collectionName, Map<String, String> paths, int skip, int max) {
        Query query = new Query();

        return findByQuery(collectionName, addSearchPathsToQuery(query, paths), skip, max);
    }

    @Override
    public void deleteAll(String collectionName) {
        template.remove(new Query(), collectionName);
        LOG.info("delete all objects in collection {}", collectionName);
    }

    @Override
    public Iterable<T> findAll(String collectionName) {
        return findByQuery(collectionName, new Query());
    }

    @Override
    public Iterable<T> findByFields(String collectionName, Map<String, String> fields) {
        return findByPaths(collectionName, convertBodyToPaths(fields));
    }

    @Override
    public Iterable<T> findByPaths(String collectionName, Map<String, String> paths) {
        Query query = new Query();

        return findByQuery(collectionName, addSearchPathsToQuery(query, paths));
    }

    @Override
    public Iterable<T> findByQuery(String collectionName, Query query, int skip, int max) {
        if (query == null)
            query = new Query();

        query.skip(skip).limit(max);

        return findByQuery(collectionName, query);
    }

    protected Iterable<T> findByQuery(String collectionName, Query query) {
        List<T> results = template.find(query, clazz, collectionName);
        logResults(collectionName, results);
        return results;
    }

    @Override
    public T findOne(String collectionName, Query query) {
        T object = this.template.findOne(query, clazz, collectionName);
        logResults(collectionName, Arrays.asList(object));
        return object;
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
    abstract public Iterable<String> findIdsByQuery(String collectionName, Query query, int skip, int max);

    private Query addSearchPathsToQuery(Query query, Map<String, String> searchPaths) {
        for (Map.Entry<String, String> field : searchPaths.entrySet()) {
            Criteria criteria = Criteria.where(field.getKey()).is(field.getValue());
            query.addCriteria(criteria);
        }

        return query;
    }

    private void logResults(String collectioName, List<T> results) {
        if (results == null) {
            LOG.debug("find objects in collection {} with total numbers is {}", new Object[] { collectioName, 0 });
        } else {
            LOG.debug("find objects in collection {} with total numbers is {}",
                    new Object[] { collectioName, results.size() });
        }

    }

    protected Map<String, String> convertBodyToPaths(Map<String, String> body) {
        Map<String, String> paths = new HashMap<String, String>();
        for (Map.Entry<String, String> field : body.entrySet()) {
            paths.put("body." + field.getKey(), field.getValue());
        }

        return paths;
    }

}

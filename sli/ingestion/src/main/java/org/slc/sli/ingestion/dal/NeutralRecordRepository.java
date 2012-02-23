package org.slc.sli.ingestion.dal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.DBCollection;
import com.mongodb.WriteResult;
import com.mongodb.Mongo;
import org.slc.sli.dal.convert.MongoIdConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import org.slc.sli.dal.convert.IdConverter;
import org.slc.sli.ingestion.NeutralRecord;

/**
 * Specialized class providing basic CRUD and field query methods for neutral records
 * using a Mongo "sandbox" DB, for use by the Ingestion Aggregation/Splitting transformers.
 *
 * @author Thomas Shewchuk tshewchuk@wgen.net 2/23/2012 (PI3 US1226)
 *
 */
public class NeutralRecordRepository {
    private static final Logger LOG = LoggerFactory.getLogger(NeutralRecordRepository.class);

    @Autowired
    private MongoTemplate template;

    @Autowired
    private IdConverter idConverter;

    public NeutralRecord find(String collectionName, String id) {
        Object databaseId = idConverter.toDatabaseId(id);
        LOG.debug("find a Neutral Record in collection {} with id {}", new Object[] { collectionName, id });
        return template.findById(databaseId, NeutralRecord.class, collectionName);
    }

    public NeutralRecord find(String collectionName, Map<String, String> queryParameters) {
        // turn query parameters into a Neutral-specific query
        Query query = NeutralRecordRepository.createQuery(queryParameters, this.idConverter);

        // find and return an NeutralRecord
        return template.findOne(query, NeutralRecord.class, collectionName);
    }

    public Iterable<NeutralRecord> findAll(String collectionName, Map<String, String> queryParameters) {
        // turn query parameters into a Neutral-specific query
        Query query = NeutralRecordRepository.createQuery(queryParameters, this.idConverter);

        // find and return an NeutralRecord
        return template.find(query, NeutralRecord.class, collectionName);
    }

    /**
     * Constructs a Neutral-specific Query object from a map of key/value pairs. Contains special
     * cases when the key is "_id", "includeFields",
     * "excludeFields", "skip", and "limit". All other keys are added to the query as criteria
     * specifying a field to search for (in the NeutralRecord's
     * body).
     *
     * @param queryParameters
     *            all parameters to be included in query
     * @param converter
     *            used to convert human readable IDs into GUIDs (if queryParameters contains "_id"
     *            key)
     * @return query object compatible with Neutral containing all parameters specified in the
     *         original map
     */
    private static Query createQuery(Map<String, String> queryParameters, IdConverter converter) {
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
                    Object databaseId = converter.toDatabaseId(id);
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

    public Iterable<NeutralRecord> findAll(String collectionName, int skip, int max) {
        List<NeutralRecord> results = template.find(new Query().skip(skip).limit(max), NeutralRecord.class,
                collectionName);
        logResults(collectionName, results);
        return results;
    }

    public boolean update(String collection, NeutralRecord neutralRecord) {
        Assert.notNull(neutralRecord, "The given Neutral Record must not be null!");
        String id = neutralRecord.getLocalId().toString();
        if (id.equals(""))
            return false;

        NeutralRecord found = template.findOne(new Query(Criteria.where("_id").is(idConverter.toDatabaseId(id))),
                NeutralRecord.class, collection);
        if (found != null) {
            template.save(neutralRecord, collection);
        }
        WriteResult result = template.updateFirst(new Query(Criteria.where("_id").is(idConverter.toDatabaseId(id))),
                new Update().set("body", neutralRecord.getAttributes()), collection);
        LOG.info("update a NeutralRecord in collection {} with id {}", new Object[] { collection, id });
        return result.getN() == 1;
    }

    public NeutralRecord create(String collection, NeutralRecord neutralRecord) {
        Assert.notNull(neutralRecord.getAttributes(), "The given Neutral Record must not be null!");

        template.save(neutralRecord, collection);
        LOG.info(" create a Neutral Record in collection {} with id {}",
                new Object[] { collection, neutralRecord.getLocalId() });
        return neutralRecord;
    }

    public boolean delete(String collectionName, String id) {
        if (id.equals(""))
            return false;
        NeutralRecord deleted = template.findAndRemove(
                new Query(Criteria.where("_id").is(idConverter.toDatabaseId(id))), NeutralRecord.class, collectionName);
        LOG.info("delete a NeutralRecord in collection {} with id {}", new Object[] { collectionName, id });
        return deleted != null;
    }

    public Iterable<NeutralRecord> findByFields(String collectionName, Map<String, String> fields, int skip, int max) {
        return findByPaths(collectionName, convertBodyToPaths(fields), skip, max);
    }

    public Iterable<NeutralRecord> findByPaths(String collectionName, Map<String, String> paths, int skip, int max) {
        Query query = new Query();

        return findByQuery(collectionName, addSearchPathsToQuery(query, paths), skip, max);
    }

    public void deleteAll(String collectionName) {
        template.remove(new Query(), collectionName);
        LOG.info("delete all entities in collection {}", collectionName);
    }

    public Iterable<NeutralRecord> findAll(String collectionName) {
        return findByQuery(collectionName, new Query());
    }

    public Iterable<NeutralRecord> findByFields(String collectionName, Map<String, String> fields) {
        return findByPaths(collectionName, convertBodyToPaths(fields));
    }

    public Iterable<NeutralRecord> findByPaths(String collectionName, Map<String, String> paths) {
        Query query = new Query();

        return findByQuery(collectionName, addSearchPathsToQuery(query, paths));
    }

    public Iterable<NeutralRecord> findByQuery(String collectionName, Query query, int skip, int max) {
        if (query == null)
            query = new Query();

        query.skip(skip).limit(max);

        return findByQuery(collectionName, query);
    }

    protected Iterable<NeutralRecord> findByQuery(String collectionName, Query query) {
        List<NeutralRecord> results = template.find(query, NeutralRecord.class, collectionName);
        logResults(collectionName, results);
        return results;
    }

    public long count(String collectionName, Query query) {
        DBCollection collection = template.getCollection(collectionName);
        if (collection == null) {
            return 0;
        }
        return collection.count(query.getQueryObject());
    }

    public Iterable<String> findIdsByQuery(String collectionName, Query query, int skip, int max) {
        if (query == null) {
            query = new Query();
        }
        query.fields().include("_id");
        List<String> ids = new ArrayList<String>();
        for (NeutralRecord nr : findByQuery(collectionName, query, skip, max)) {
            ids.add(nr.getLocalId().toString());
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

    private void logResults(String collectioName, List<NeutralRecord> results) {
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

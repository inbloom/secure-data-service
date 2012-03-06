package org.slc.sli.ingestion.dal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import org.slc.sli.dal.repository.MongoRepository;
import org.slc.sli.ingestion.NeutralRecord;

/**
 * Specialized class providing basic CRUD and field query methods for neutral records
 * using a Mongo "sandbox" DB, for use by the Ingestion Aggregation/Splitting transformers.
 *
 * @author Thomas Shewchuk tshewchuk@wgen.net 2/23/2012 (PI3 US1226)
 *
 */
public class NeutralRecordRepository extends MongoRepository<NeutralRecord> {
    private static final Logger LOG = LoggerFactory.getLogger(NeutralRecordRepository.class);

    NeutralRecordRepository() {
        super.setClass(NeutralRecord.class);
    }

    @Override
    public NeutralRecord find(String collection, String id) {
        LOG.debug("find a Neutral Record in collection {} with id {}", new Object[] { collection, id });
        Map<String, String> query = new HashMap<String, String>();
        query.put("body.localId", id);
        return find(collection, query);
    }

    @Override
    public Iterable<NeutralRecord> findAll(String collection, int skip, int max) {
        List<NeutralRecord> results = template.find(new Query().skip(skip).limit(max), NeutralRecord.class, collection);
        logResults(collection, results);
        return results;
    }

    @Override
    public boolean update(String collection, NeutralRecord object) {
        //TODO: Add implementation for Transformer.
        return update(object);
    }

    public boolean update(NeutralRecord neutralRecord) {
        Assert.notNull(neutralRecord, "The given Neutral Record must not be null!");
        String id = neutralRecord.getLocalId().toString();
        if (id.equals(""))
            return false;

        String collection = neutralRecord.getRecordType();
        NeutralRecord found = template.findOne(new Query(Criteria.where("body.localId").is(id)), NeutralRecord.class,
                collection);
        if (found != null) {
            template.save(neutralRecord, collection);
        }
        WriteResult result = template.updateFirst(new Query(Criteria.where("body.localId").is(id)),
                new Update().set("body", neutralRecord.getAttributes()), collection);
        LOG.info("update a NeutralRecord in collection {} with id {}", new Object[] { collection, id });
        return result.getN() == 1;
    }

    @Override
    public NeutralRecord create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName) {
        //TODO: Add implementation for Transformer.
        return null;
    }

    public NeutralRecord create(NeutralRecord neutralRecord) {
        Assert.notNull(neutralRecord.getAttributes(), "The given Neutral Record must not be null!");

        String collection = neutralRecord.getRecordType();
        template.save(neutralRecord, collection);
        LOG.info(" create a Neutral Record in collection {} with id {}",
                new Object[] { collection, neutralRecord.getLocalId() });
        return neutralRecord;
    }

    @Override
    public boolean delete(String collection, String id) {
        if (id.equals(""))
            return false;
        NeutralRecord deleted = template.findAndRemove(new Query(Criteria.where("body.localId").is(id)),
                NeutralRecord.class, collection);
        LOG.info("delete a NeutralRecord in collection {} with id {}", new Object[] { collection, id });
        return deleted != null;
    }

    @Override
    public Iterable<NeutralRecord> findByFields(String collection, Map<String, String> fields, int skip, int max) {
        return findByPaths(collection, convertBodyToPaths(fields), skip, max);
    }

    @Override
    public Iterable<NeutralRecord> findByPaths(String collection, Map<String, String> paths, int skip, int max) {
        Query query = new Query();

        return findByQuery(collection, addSearchPathsToQuery(query, paths), skip, max);
    }

    @Override
    public void deleteAll(String collection) {
        template.remove(new Query(), collection);
        LOG.info("delete all entities in collection {}", collection);
    }

    @Override
    public Iterable<NeutralRecord> findAll(String collection) {
        return findByQuery(collection, new Query());
    }

    @Override
    public Iterable<NeutralRecord> findByFields(String collection, Map<String, String> fields) {
        return findByPaths(collection, convertBodyToPaths(fields));
    }

    @Override
    public Iterable<NeutralRecord> findByPaths(String collection, Map<String, String> paths) {
        Query query = new Query();

        return findByQuery(collection, addSearchPathsToQuery(query, paths));
    }

    @Override
    public Iterable<NeutralRecord> findByQuery(String collection, Query query, int skip, int max) {
        if (query == null)
            query = new Query();

        query.skip(skip).limit(max);

        return findByQuery(collection, query);
    }

    @Override
    protected Iterable<NeutralRecord> findByQuery(String collection, Query query) {
        List<NeutralRecord> results = template.find(query, NeutralRecord.class, collection);
        logResults(collection, results);
        return results;
    }

    @Override
    public long count(String collection, Query query) {
        DBCollection dBcollection = template.getCollection(collection);
        if (collection == null) {
            return 0;
        }
        return dBcollection.count(query.getQueryObject());
    }

    @Override
    public Iterable<String> findIdsByQuery(String collection, Query query, int skip, int max) {
        if (query == null) {
            query = new Query();
        }
        List<String> ids = new ArrayList<String>();
        for (NeutralRecord nr : findByQuery(collection, query, skip, max)) {
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

    private void logResults(String collection, List<NeutralRecord> results) {
        if (results == null) {
            LOG.debug("find entities in collection {} with total numbers is {}", new Object[] { collection, 0 });
        } else {
            LOG.debug("find entities in collection {} with total numbers is {}",
                    new Object[] { collection, results.size() });
        }

    }
}

package org.slc.sli.ingestion.dal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import org.slc.sli.dal.repository.MongoRepository;
import org.slc.sli.ingestion.NeutralRecord;

/**
 * Specialized class providing basic CRUD and field query methods for neutral records
 * using a Mongo Staging DB, for use for staging data for intermediate operations.
 *
 * @author Thomas Shewchuk tshewchuk@wgen.net 2/23/2012 (PI3 US1226)
 *
 */
public class NeutralRecordRepository extends MongoRepository<NeutralRecord> {
    protected static final Logger LOG = LoggerFactory.getLogger(NeutralRecordRepository.class);

    NeutralRecordRepository() {
        super.setClass(NeutralRecord.class);
    }

    public NeutralRecord findByLocalId(String collection, String localId) {
        LOG.debug("find a Neutral Record in collection {} with id {}", new Object[] { collection, localId });
        Map<String, String> query = new HashMap<String, String>();
        query.put("localId", localId);
        return find(collection, query);
    }

    public boolean update(NeutralRecord neutralRecord) {
        return update(neutralRecord.getRecordType(), neutralRecord);
    }

    @Override
    public boolean update(String collection, NeutralRecord neutralRecord) {
        Assert.notNull(neutralRecord, "The given Neutral Record must not be null!");
        String localId = neutralRecord.getLocalId().toString();
        if (localId.equals(""))
            return false;

        NeutralRecord found = template.findOne(new Query(Criteria.where("body.localId").is(localId)),
                NeutralRecord.class, collection);
        if (found == null)
            return false;

/*        Map<String, Object> body = neutralRecord.getAttributes();
        body.put("localId", localId);
        WriteResult result = template.updateFirst(new Query(Criteria.where("body.localId").is(localId)),
                new Update().set("body", body), collection);
        return result.getN() == 1;*/

        deleteByLocalId(collection, localId);
        create(neutralRecord);
        LOG.info("update a NeutralRecord in collection {} with id {}", new Object[] { collection, localId });
        return true;
    }

    @Override
    public NeutralRecord create(String type, Map<String, Object> body, Map<String, Object> metaData,
            String collectionName) {
        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setLocalId(metaData.get("externalId"));
        neutralRecord.setAttributes(body);
        return create(neutralRecord);
    }

    public NeutralRecord create(NeutralRecord neutralRecord) {
        Assert.notNull(neutralRecord.getAttributes(), "The given Neutral Record must not be null!");

        String collection = neutralRecord.getRecordType();
        template.save(neutralRecord, collection);
        LOG.info(" create a Neutral Record in collection {} with id {}", new Object[] { collection,
                getRecordId(neutralRecord) });
        return neutralRecord;
    }

    public boolean deleteByLocalId(String collection, String localId) {
        if (localId.equals("")) {
            return false;
        }
        NeutralRecord deleted = template.findAndRemove(new Query(Criteria.where(getRecordIdName()).is(localId)),
                NeutralRecord.class, collection);
        LOG.info("delete a NeutralRecord in collection {} with id {}", new Object[] { collection, localId });
        return deleted != null;
    }

    @Override
    protected String getRecordId(NeutralRecord neutralRecord) {
        return neutralRecord.getLocalId().toString();
    }

    @Override
    protected String getRecordIdName() {
        return "body.localId";
    }
    }
    public Set<String> getCollectionNames() {
        return template.getCollectionNames();
    }
}

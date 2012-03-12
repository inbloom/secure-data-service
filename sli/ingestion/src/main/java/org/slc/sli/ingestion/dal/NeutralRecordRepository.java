package org.slc.sli.ingestion.dal;

import java.util.HashMap;
import java.util.Map;

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
        String id = neutralRecord.getLocalId().toString();
        if (id.equals(""))
            return false;

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
        LOG.info(" create a Neutral Record in collection {} with id {}",
                new Object[] { collection, getRecordId(neutralRecord) });
        return neutralRecord;
    }

    public boolean deleteByLocalId(String collection, String localId) {
        if (localId.equals(""))
            return false;
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

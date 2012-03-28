package org.slc.sli.ingestion.dal;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

import com.mongodb.MongoException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Override
    public boolean update(String collection, NeutralRecord neutralRecord) {
        return update(neutralRecord.getRecordType(), neutralRecord, neutralRecord.getAttributes());
    }

    @Override
    public NeutralRecord create(String type, Map<String, Object> body, Map<String, Object> metaData,
            String collectionName) {
        Assert.notNull(body, "The given entity must not be null!");
        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setLocalId(metaData.get("externalId"));
        neutralRecord.setAttributes(body);
        return create(neutralRecord, collectionName);
    }

    public NeutralRecord create(NeutralRecord neutralRecord) {
        return create(neutralRecord, neutralRecord.getRecordType());
    }

    @Override
    protected String getRecordId(NeutralRecord neutralRecord) {
        return neutralRecord.getRecordId();
    }

    public Set<String> getCollectionNames() {
        return template.getCollectionNames();
    }

    public void changeTemplate(String batchJobId) {
        StagingMongoTemplate currentTemplate = (StagingMongoTemplate) getTemplate();
        try {
            setTemplate(new StagingMongoTemplate(currentTemplate.getDatabasePrefix(), batchJobId, currentTemplate.getNeutralRecordMappingConverter()));
        } catch (UnknownHostException e) {

            LOG.error(e.getMessage());

        } catch (MongoException e) {

            LOG.error(e.getMessage());

        }
    }

    @Override
    protected Class<NeutralRecord> getRecordClass() {
        return NeutralRecord.class;
    }
}

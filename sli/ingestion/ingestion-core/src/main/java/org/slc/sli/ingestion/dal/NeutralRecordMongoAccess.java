package org.slc.sli.ingestion.dal;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.ResourceWriter;

/**
 * write NeutralRecord objects to mongo
 *
 * @author dduran
 *
 */
public class NeutralRecordMongoAccess implements ResourceWriter<NeutralRecord> {

    private NeutralRecordRepository neutralRecordRepository;

    private MongoIndexManager mongoIndexManager;

    public MongoIndexManager getMongoIndexManager() {
        return mongoIndexManager;
    }

    public void setMongoIndexManager(MongoIndexManager mongoIndexManager) {
        this.mongoIndexManager = mongoIndexManager;
    }

    @Override
    public void writeResource(NeutralRecord neutralRecord, String jobId) {
        neutralRecordRepository.createForJob(neutralRecord, jobId);
    }

    public NeutralRecordRepository getRecordRepository() {
        return this.neutralRecordRepository;
    }

    public void setNeutralRecordRepository(NeutralRecordRepository neutralRecordRepository) {
        this.neutralRecordRepository = neutralRecordRepository;
    }

    /**
     * ensureIndexes for all the collections configured
     *
     * @author tke
     */
    public void ensureIndex() {
        mongoIndexManager.ensureIndex(neutralRecordRepository);
    }

}

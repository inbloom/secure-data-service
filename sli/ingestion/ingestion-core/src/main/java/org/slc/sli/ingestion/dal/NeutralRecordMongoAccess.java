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

}

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
    public void writeResource(NeutralRecord neutralRecord) {

        // TODO: we need to be able to create databases on the fly (base on jobId)

        neutralRecordRepository.create(neutralRecord);

    }

    public NeutralRecordRepository getRecordRepository() {
        return this.neutralRecordRepository;
    }

    public void setNeutralRecordRepository(NeutralRecordRepository neutralRecordRepository) {
        this.neutralRecordRepository = neutralRecordRepository;
    }

    public void changeMongoTemplate(String batchJobId) {
        neutralRecordRepository.changeTemplate(batchJobId);
    }

}

package org.slc.sli.ingestion.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.ResourceWriter;

/**
 * write NeutralRecord objects to mongo
 *
 * @author dduran
 *
 */
@Component
public class NeutralRecordMongoAccess implements ResourceWriter<NeutralRecord> {

    @Autowired
    NeutralRecordRepository neutralRecordRepository;

    @Override
    public void writeResource(NeutralRecord neutralRecord) {

        // TODO: we need to be able to create databases on the fly (base on jobId)

        neutralRecordRepository.create(neutralRecord);

    }
    
    public NeutralRecordRepository getRecordRepository() {
        return this.neutralRecordRepository;
    }

}

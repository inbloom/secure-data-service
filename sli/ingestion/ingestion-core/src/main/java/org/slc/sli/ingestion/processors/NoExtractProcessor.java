package org.slc.sli.ingestion.processors;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJobStatusType;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.WorkNoteImpl;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.da.BatchJobDAO;

/**
 * Processor for no extract route used for performance measuring
 *
 * @author dduran
 *
 */
@Component
public class NoExtractProcessor implements Processor {

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Override
    public void process(Exchange exchange) throws Exception {
        File file = exchange.getIn().getBody(File.class);
        String batchJobId = file.getName().substring(0, file.getName().indexOf(".noextract"));
        exchange.getIn().setHeader("BatchJobId", batchJobId);

        NewBatchJob job = new NewBatchJob(batchJobId);
        job.setStatus(BatchJobStatusType.RUNNING.getName());
        job.setSourceId(file.getParentFile().getAbsolutePath() + File.separator);
        batchJobDAO.saveBatchJob(job);

        WorkNote workNote = WorkNoteImpl.createSimpleWorkNote(batchJobId);
        exchange.getIn().setBody(workNote, WorkNote.class);

    }
}

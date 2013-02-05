package org.slc.sli.ingestion;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * @author slee
 *
 */
@Component
public class IngestionFileEntryLatch
{
    private static final String FILE_ENTRY_FILE_TYPE = "FileEntryWorkNoteFileType";
    private static final String FILE_ENTRY_FILE_NAME = "IngestionFileEntryFileName";
    private static final String FILE_ENTRY_BATCH_JOB_ID = "IngestionFileEntryBatchJobId";

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Handler
    public void receive(Exchange exchange) throws Exception {

        FileEntryWorkNote workNote = exchange.getIn().getBody(FileEntryWorkNote.class);
        String batchJobId = workNote.getBatchJobId();
        TenantContext.setJobId(batchJobId);

        exchange.getIn().setHeader(FILE_ENTRY_FILE_TYPE, workNote.getFileEntry().getFileType().getName());
        exchange.getIn().setHeader(FILE_ENTRY_FILE_NAME, workNote.getFileEntry().getFileName());
        exchange.getIn().setHeader(FILE_ENTRY_BATCH_JOB_ID, workNote.getFileEntry().getBatchJobId());
    }

}

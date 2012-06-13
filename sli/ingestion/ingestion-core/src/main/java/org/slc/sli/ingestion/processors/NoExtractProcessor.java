package org.slc.sli.ingestion.processors;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.dal.TenantContext;
import org.slc.sli.ingestion.BatchJobStatusType;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.WorkNoteImpl;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    private static final Logger LOG = LoggerFactory.getLogger(NoExtractProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        
        //We need to extract the TenantID for each thread, so the DAL has access to it.
        try {
            ControlFileDescriptor cfd = exchange.getIn().getBody(ControlFileDescriptor.class);
            ControlFile cf = cfd.getFileItem();
            String tenantId = cf.getConfigProperties().getProperty("tenantId");
            TenantContext.setTenantId(tenantId);
        } catch (NullPointerException ex) {
            LOG.error("Could Not find Tenant ID.");
            TenantContext.setTenantId(null);
        }

        
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

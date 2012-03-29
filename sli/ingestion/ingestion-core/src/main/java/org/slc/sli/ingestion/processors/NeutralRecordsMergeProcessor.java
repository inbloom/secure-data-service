package org.slc.sli.ingestion.processors;

import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.util.performance.Profiled;

/**
 * 
 * This processor is responsible for merging any NeutralRecord (Ed-Fi) whose corresponding record
 * type has been collapsed
 * into another in the sliXsd data model. These 'processed' NeutralRecords will later be mapped to
 * Entity (sliXsd).
 * 
 */
@Component
public class NeutralRecordsMergeProcessor implements Processor {
    
    private static final Logger LOG = LoggerFactory.getLogger(NeutralRecordsMergeProcessor.class);
    
    @Autowired
    private EdFiAssessmentConvertor assessmentConvertor;
    
    /**
     * Camel Exchange process callback method
     * 
     * @param exchange
     */
    @Override
    @ExtractBatchJobIdToContext
    @Profiled
    public void process(Exchange exchange) {

        // TODO: get the batchjob from the state manager
        // Get the batch job ID from the exchange
//      String batchJobId = exchange.getIn().getBody(String.class);
//      BatchJobUtils.startStage(batchJobId, this.getClass().getName());
//      BatchJob batchJob = BatchJobUtils.getBatchJob(batchJobId);
        BatchJob job = BatchJobUtils.getBatchJobUsingStateManager(exchange);

        mergeNeutralRecordsInBatchJob(job);

        BatchJobUtils.saveBatchJobUsingStateManager(job);
        // TODO When the interface firms up we should set the stage stopTimeStamp in job before saving to db, rather than after really
        BatchJobUtils.stopStage(job.getId(), this.getClass().getName());

        exchange.getIn().setHeader("IngestionMessageType", MessageType.PERSIST_REQUEST.name());
    }
    
    private void mergeNeutralRecordsInBatchJob(BatchJob job) {
        LOG.info("merging NeutralRecord entries in BatchJob: {}", job);
        for (IngestionFileEntry file : job.getFiles()) {
            
            // TODO BatchJobUtil.startMetric(job.getId(), this.getClass().getName(), file.getFileName())

            if (FileType.XML_ASSESSMENT_METADATA.equals(file.getFileType())) {
                try {
                    assessmentConvertor.doConversion(file);
                } catch (IOException e) {
                    String errorText = "Exception occurred while converting assessments: " + file.getFileName();
                    LOG.error(errorText, e);
                    job.getErrorReport().error(errorText, NeutralRecordsMergeProcessor.class);
                }
            }

            // TODO Add a recordCount variables to IngestionFileEntry for each file (i.e. original, neutral, ...) - see if things have changed with transform
            // TODO BatchJobUtil.stopMetric(job.getId(), this.getClass().getName(), file.getFileName(), file.getNeutralRecordCount(), file.getFaultsReport.getFaults().size())

        }
        
    }
    
}

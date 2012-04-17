package org.slc.sli.ingestion.processors;

import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.performance.Profiled;
import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;
import org.slc.sli.ingestion.queues.MessageType;

/**
 *
 * This processor is responsible for merging any NeutralRecord (Ed-Fi) whose corresponding record
 * type has been collapsed
 * into another in the sliXsd data model. These 'processed' NeutralRecords will later be mapped to
 * Entity (sliXsd).
 * @deprecated This will be removed at a later date.
 */
@Deprecated
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

        String batchJobId = exchange.getIn().getHeader("BatchJobId", String.class);
        if (batchJobId == null) {
            exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            LOG.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
        }
        BatchJobDAO batchJobDAO = new BatchJobMongoDA();
        NewBatchJob newJob = batchJobDAO.findBatchJobById(batchJobId);
        // batchJobDAO.startStage(batchJobId, BatchJobStageType.EDFI_PROCESSING);

        BatchJob job = exchange.getIn().getBody(BatchJob.class);

        mergeNeutralRecordsInBatchJob(job);

        // batchJobDAO.stopStage(batchJobId, BatchJobStageType.EDFI_PROCESSING);
        exchange.getIn().setHeader("IngestionMessageType", MessageType.PERSIST_REQUEST.name());
    }

    private void mergeNeutralRecordsInBatchJob(BatchJob job) {
        LOG.info("merging NeutralRecord entries in BatchJob: {}", job);
        for (IngestionFileEntry file : job.getFiles()) {

            if (FileType.XML_ASSESSMENT_METADATA.equals(file.getFileType())) {

                // batchJobDAO.startMetric(batchJobId, BatchJobStageType.EDFI_PROCESSING, file.getFileName());

                try {
                    assessmentConvertor.doConversion(file);
                } catch (IOException e) {
                    String errorText = "Exception occurred while converting assessments: " + file.getFileName();
                    LOG.error(errorText, e);
                    job.getErrorReport().error(errorText, NeutralRecordsMergeProcessor.class);
                }

                // batchJobDAO.stopMetric(batchJobId, BatchJobStageType.EDFI_PROCESSING,
                // fe.getFileName(), fe.getRecordCount, fe.getFaultsReport.getFaults().size())
            }

        }

    }

}

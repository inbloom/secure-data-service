package org.slc.sli.ingestion.processors;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;
import org.slc.sli.ingestion.queues.MessageType;

/**
 * Transforms body from ControlFile to ControlFileDescriptor type.
 *
 * @author okrook
 *
 */
public class ControlFilePreProcessor implements Processor {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.CONTROL_FILE_PREPROCESSING;

    private LandingZone landingZone;

    Logger log = LoggerFactory.getLogger(ZipFileProcessor.class);

    public ControlFilePreProcessor(LandingZone lz) {
        this.landingZone = lz;
    }

    /**
     * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
     */
    @Override
    public void process(Exchange exchange) throws Exception {

        processUsingNewBatchJob(exchange);
    }

    private void processUsingNewBatchJob(Exchange exchange) throws Exception {

        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE);

        String batchJobId = exchange.getIn().getHeader("BatchJobId", String.class);

        // TODO handle invalid control file (user error)
        // TODO handle IOException or other system error
        try {
            BatchJobDAO batchJobDAO = new BatchJobMongoDA();

            File fileForControlFile = exchange.getIn().getBody(File.class);

            NewBatchJob newBatchJob = getNewBatchJobFromDb(batchJobId, batchJobDAO);
            if (newBatchJob == null) {
                newBatchJob = createNewBatchJob(fileForControlFile, exchange);
            }

            ControlFile controlFile = ControlFile.parse(fileForControlFile);

            newBatchJob.setTotalFiles(controlFile.getFileEntries().size());

            newBatchJob.addCompletedStage(stage);

            batchJobDAO.saveBatchJob(newBatchJob);

            setExchangeHeaders(exchange, controlFile);

        } catch (Exception exception) {
            handleExceptions(exchange, batchJobId, exception);
        }
    }

    private void handleExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        log.error("Exception:", exception);
        if (batchJobId != null) {
            BatchJobMongoDA.logBatchStageError(batchJobId, BATCH_JOB_STAGE,
                    FaultType.TYPE_ERROR.getName(), null, exception.toString());
        }
    }

    private void setExchangeHeaders(Exchange exchange, ControlFile controlFile) {
        exchange.getIn().setBody(new ControlFileDescriptor(controlFile, landingZone), ControlFileDescriptor.class);
        exchange.getIn().setHeader("IngestionMessageType", MessageType.BATCH_REQUEST.name());
    }

    private NewBatchJob getNewBatchJobFromDb(String batchJobId, BatchJobDAO batchJobDAO) {
        if (batchJobId != null) {
            return batchJobDAO.findBatchJobById(batchJobId);
        }
        return null;
    }

    private NewBatchJob createNewBatchJob(File controlFile, Exchange exchange) {

        NewBatchJob newJob = NewBatchJob.createJobForFile(controlFile.getName());

        // TODO Make getting the path a little nicer (i.e., not stripping the zip temp path)
        newJob.setSourceId(landingZone.getLZId() + File.separator);

        exchange.getIn().setHeader("BatchJobId", newJob.getId());
        log.info("Created job [{}]", newJob.getId());
        return newJob;
    }

}

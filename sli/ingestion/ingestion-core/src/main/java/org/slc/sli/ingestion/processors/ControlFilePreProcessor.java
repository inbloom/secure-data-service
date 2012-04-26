package org.slc.sli.ingestion.processors;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.BatchJobStatusType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.util.BatchJobUtils;

/**
 * Transforms body from ControlFile to ControlFileDescriptor type.
 *
 * @author okrook
 *
 */
@Component
public class ControlFilePreProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(ControlFilePreProcessor.class);

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.CONTROL_FILE_PREPROCESSOR;

    @Autowired
    private LandingZone landingZone;

    @Autowired
    private BatchJobDAO batchJobDAO;

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
        NewBatchJob newBatchJob = null;
        try {

            File fileForControlFile = exchange.getIn().getBody(File.class);

            LandingZone resolvedLZ = resolveLandingZone(batchJobId, fileForControlFile);

            newBatchJob = getOrCreateNewBatchJob(batchJobId, fileForControlFile, resolvedLZ);

            ControlFile controlFile = ControlFile.parse(fileForControlFile);

            newBatchJob.setTotalFiles(controlFile.getFileEntries().size());
            createResourceEntryAndAddToJob(controlFile, newBatchJob);

            ControlFileDescriptor controlFileDescriptor = new ControlFileDescriptor(controlFile, resolvedLZ);

            setExchangeHeaders(exchange, controlFileDescriptor, newBatchJob);

        } catch (Exception exception) {
            handleExceptions(exchange, batchJobId, exception);
        } finally {
            if (newBatchJob != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, newBatchJob);
                batchJobDAO.saveBatchJob(newBatchJob);
            }
        }
    }

    private LandingZone resolveLandingZone(String batchJobId, File fileForControlFile) {
        // if this has a batch id it came from zipfileprocessor so change lz to parent of ctl file
        LandingZone resolvedLandingZone = landingZone;
        if (batchJobId != null) {
            resolvedLandingZone = new LocalFileSystemLandingZone();
            ((LocalFileSystemLandingZone) resolvedLandingZone).setDirectory(fileForControlFile.getParentFile());
        }
        return resolvedLandingZone;
    }

    private void handleExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("Exception:", exception);
        if (batchJobId != null) {
            Error error = Error.createIngestionError(batchJobId, null, BATCH_JOB_STAGE.getName(), null, null, null,
                    FaultType.TYPE_ERROR.getName(), null, exception.toString());
            batchJobDAO.saveError(error);
        }
    }

    private void setExchangeHeaders(Exchange exchange, ControlFileDescriptor controlFileDescriptor, NewBatchJob newJob) {
        exchange.getIn().setHeader("BatchJobId", newJob.getId());
        exchange.getIn().setBody(controlFileDescriptor, ControlFileDescriptor.class);
        exchange.getIn().setHeader("IngestionMessageType", MessageType.BATCH_REQUEST.name());
    }

    private NewBatchJob getOrCreateNewBatchJob(String batchJobId, File cf, LandingZone lz) {
        NewBatchJob job = null;
        if (batchJobId != null) {
            job = batchJobDAO.findBatchJobById(batchJobId);
        } else {
            job = createNewBatchJob(cf, lz);
        }
        return job;
    }

    private NewBatchJob createNewBatchJob(File controlFile, LandingZone landingZone) {
        NewBatchJob newJob = NewBatchJob.createJobForFile(controlFile.getName());
        newJob.setSourceId(landingZone.getLZId() + File.separator);
        newJob.setStatus(BatchJobStatusType.RUNNING.getName());
        LOG.info("Created job [{}]", newJob.getId());
        return newJob;
    }

    private void createResourceEntryAndAddToJob(ControlFile cf, NewBatchJob newJob) {
        ResourceEntry resourceEntry = new ResourceEntry();
        resourceEntry.setResourceId(cf.getFileName());
        resourceEntry.setExternallyUploadedResourceId(cf.getFileName());
        resourceEntry.setResourceName(newJob.getSourceId() + cf.getFileName());
        resourceEntry.setResourceFormat(FileFormat.CONTROL_FILE.getCode());
        newJob.getResourceEntries().add(resourceEntry);
    }

}

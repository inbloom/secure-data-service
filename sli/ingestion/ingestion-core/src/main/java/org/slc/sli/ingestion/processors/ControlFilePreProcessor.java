package org.slc.sli.ingestion.processors;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.BatchJobStatusType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.landingzone.validation.IngestionException;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.tenant.TenantDA;
import org.slc.sli.ingestion.tenant.TenantMongoDA;

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
    private BatchJobDAO batchJobDAO;

    @Value("${sli.ingestion.tenant.deriveTenants}")
    private boolean deriveTenantId;

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
            newBatchJob = getOrCreateNewBatchJob(batchJobId, fileForControlFile);

            File lzFile = new File(newBatchJob.getTopLevelSourceId());
            File sourceFile = new File(newBatchJob.getSourceId());
            LandingZone topLevelLandingZone = new LocalFileSystemLandingZone(lzFile);
            LandingZone resolvedLandingZone = new LocalFileSystemLandingZone(sourceFile);

            ControlFile controlFile = ControlFile.parse(fileForControlFile, topLevelLandingZone);

            newBatchJob.setTotalFiles(controlFile.getFileEntries().size());
            createResourceEntryAndAddToJob(controlFile, newBatchJob);

            //determine whether to override the tenantId property with a LZ derived value
            if (deriveTenantId) {
                //derive the tenantId property from the landing zone directory with a mongo lookup
                setTenantId(controlFile, lzFile.getAbsolutePath());
            }

            ControlFileDescriptor controlFileDescriptor = new ControlFileDescriptor(controlFile, resolvedLandingZone);

            setExchangeHeaders(exchange, controlFileDescriptor, newBatchJob);

        } catch (Exception exception) {
            handleExceptions(exchange, batchJobId, exception);
        } finally {
            if (newBatchJob != null) {
                newBatchJob.addCompletedStage(stage);
                batchJobDAO.saveBatchJob(newBatchJob);
            }
        }
    }

    private void handleExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("Exception:", exception);
        if (batchJobId != null) {
            Error error = Error.createIngestionError(batchJobId, BATCH_JOB_STAGE.getName(), null, null, null, null,
                    FaultType.TYPE_ERROR.getName(), null, exception.toString());
            batchJobDAO.saveError(error);
        }
    }

    private void setExchangeHeaders(Exchange exchange, ControlFileDescriptor controlFileDescriptor, NewBatchJob newJob) {
        exchange.getIn().setHeader("BatchJobId", newJob.getId());
        exchange.getIn().setBody(controlFileDescriptor, ControlFileDescriptor.class);
        exchange.getIn().setHeader("IngestionMessageType", MessageType.BATCH_REQUEST.name());
    }

    private NewBatchJob getOrCreateNewBatchJob(String batchJobId, File cf) {
        NewBatchJob job = null;
        if (batchJobId != null) {
            job = batchJobDAO.findBatchJobById(batchJobId);
        } else {
            job = createNewBatchJob(cf);
        }
        return job;
    }

    private NewBatchJob createNewBatchJob(File controlFile) {
        NewBatchJob newJob = NewBatchJob.createJobForFile(controlFile.getName());
        newJob.setSourceId(controlFile.getParentFile().getAbsolutePath() + File.separator);
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
        resourceEntry.setTopLevelLandingZonePath(newJob.getTopLevelSourceId());
        newJob.getResourceEntries().add(resourceEntry);
    }

    /**
     * Derive the tenantId using a database look up based on the LZ path
     * and override the property on the ControlFile with he derived value.
     *
     * Throws an IngestionException if a tenantId could not be resolved.
     */
    private void setTenantId(ControlFile cf, String lzPath) throws IngestionException {
        TenantDA tenantDA = new TenantMongoDA();
        // replacing windows-style paths with unix-style
        lzPath = lzPath.replaceAll("\\\\", "/");
        //TODO add user facing error report for no tenantId found
        String tenantId = tenantDA.getTenantId(lzPath);
        if (tenantId != null) {
            cf.getConfigProperties().put("tenantId", tenantId);
        } else {
            throw new IngestionException("Could not find tenantId for landing zone: " + lzPath);
        }
    }

}

package org.slc.sli.ingestion.processors;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.BatchJobStatusType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.WorkNoteImpl;
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
    private BatchJobDAO batchJobDAO;

    @Autowired
    private TenantDA tenantDA;

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

            // determine whether to override the tenantId property with a LZ derived value
            if (deriveTenantId) {
                // derive the tenantId property from the landing zone directory with a mongo lookup
                setTenantId(controlFile, lzFile.getAbsolutePath());
            }

            ControlFileDescriptor controlFileDescriptor = new ControlFileDescriptor(controlFile, resolvedLandingZone);

            setExchangeHeaders(exchange, controlFileDescriptor, newBatchJob);

            byte[] ipAddr = null;
            try {
                InetAddress addr = InetAddress.getLocalHost();

                // Get IP Address
                ipAddr = addr.getAddress();

            } catch (UnknownHostException e) {
                LOG.error("Error getting local host", e);
            }
            List<String> userRoles = Collections.emptyList();
            SecurityEvent event = new SecurityEvent(controlFile.getConfigProperties().getProperty("tenantId"), // Alpha
                                                                                                               // MH
                    "", // user
                    "", // targetEdOrg
                    "processUsingNewBatchJob", // Alpha MH (actionUri)
                    "Ingestion", // Alpha MH (appId)
                    "", // origin
                    ipAddr[0] + "." + ipAddr[1] + "." + ipAddr[2] + "." + ipAddr[3], // executedOn
                    "", // Alpha MH (Credential - N/A for ingestion)
                    "", // userOrigin
                    new Date(), // Alpha MH (timeStamp)
                    ManagementFactory.getRuntimeMXBean().getName(), // processNameOrId
                    this.getClass().getName(), // className
                    LogLevelType.TYPE_INFO, // Alpha MH (logLevel)
                    userRoles, "Ingestion process started."); // Alpha MH (logMessage)

            audit(event);

        } catch (Exception exception) {
            handleExceptions(exchange, batchJobId, exception);
        } finally {
            if (newBatchJob != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, newBatchJob);
                batchJobDAO.saveBatchJob(newBatchJob);
            }
        }
    }

    private void handleExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("Exception:", exception);
        if (batchJobId != null) {
            Error error = Error.createIngestionError(batchJobId, null, BATCH_JOB_STAGE.getName(), null, null, null,
                    FaultType.TYPE_ERROR.getName(), null, exception.toString());
            batchJobDAO.saveError(error);

            // FIXME we should be creating WorkNote at the very first point of processing.
            // this will require some routing changes
            WorkNote workNote = WorkNoteImpl.createSimpleWorkNote(batchJobId);
            exchange.getIn().setBody(workNote, WorkNote.class);
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
        lzPath = new File(lzPath).getAbsolutePath();
        // TODO add user facing error report for no tenantId found
        String tenantId = tenantDA.getTenantId(lzPath);
        if (tenantId != null) {
            cf.getConfigProperties().put("tenantId", tenantId);
        } else {
            throw new IngestionException("Could not find tenantId for landing zone: " + lzPath);
        }
    }

}

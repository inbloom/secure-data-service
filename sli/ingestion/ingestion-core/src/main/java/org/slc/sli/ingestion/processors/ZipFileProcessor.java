package org.slc.sli.ingestion.processors;

import java.io.File;
import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.BatchJobStatusType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.handler.ZipFileHandler;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Zip file handler.
 *
 * @author okrook
 *
 */
@Component
public class ZipFileProcessor implements Processor {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.ZIP_FILE_PROCESSOR;

    private static final Logger LOG = LoggerFactory.getLogger(ZipFileProcessor.class);

    @Autowired
    private ZipFileHandler zipFileHandler;

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Override
    public void process(Exchange exchange) throws Exception {

        processZipFile(exchange);
    }

    private void processZipFile(Exchange exchange) throws Exception {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE);

        String batchJobId = null;

        NewBatchJob newJob = null;
        try {
            LOG.info("Received zip file: " + exchange.getIn());

            File zipFile = exchange.getIn().getBody(File.class);

            newJob = createNewBatchJob(zipFile);
            batchJobId = newJob.getId();

            FaultsReport errorReport = new FaultsReport();

            File ctlFile = zipFileHandler.handle(zipFile, errorReport);

            BatchJobUtils.writeErrorsWithDAO(batchJobId, zipFile.getName(), BATCH_JOB_STAGE, errorReport, batchJobDAO);

            createResourceEntryAndAddToJob(zipFile, newJob);

            if (ctlFile != null) {
                newJob.setSourceId(ctlFile.getParentFile().getCanonicalPath() + File.separator);
            }

            setExchangeHeaders(exchange, errorReport, newJob);

            setExchangeBody(exchange, ctlFile, errorReport);

        } catch (Exception exception) {
            handleProcessingException(exchange, batchJobId, exception);
        } finally {
            if (newJob != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, newJob);
                batchJobDAO.saveBatchJob(newJob);
            }
        }
    }

    private NewBatchJob createNewBatchJob(File zipFile) {
        String batchJobId = NewBatchJob.createId(zipFile.getName());
        NewBatchJob newJob = new NewBatchJob(batchJobId);
        newJob.setStatus(BatchJobStatusType.RUNNING.getName());

        // added so that errors are later logged to correct location in case process fails early
        File parentFile = zipFile.getParentFile();
        String topLevelSourceId = parentFile.getAbsolutePath();
        if (topLevelSourceId.endsWith(".done")) {
            topLevelSourceId = parentFile.getParentFile().getAbsolutePath();
        }
        newJob.setTopLevelSourceId(topLevelSourceId);

        return newJob;
    }

    private void handleProcessingException(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LogUtil.error(LOG, "Error processing batch job " + batchJobId, exception);
        if (batchJobId != null) {
            Error error = Error.createIngestionError(batchJobId, null, BatchJobStageType.ZIP_FILE_PROCESSOR.getName(),
                    null, null, null, FaultType.TYPE_ERROR.getName(), null, exception.toString());
            batchJobDAO.saveError(error);
        }
    }

    private void setExchangeBody(Exchange exchange, File ctlFile, ErrorReport errorReport) {
        if (!errorReport.hasErrors() && ctlFile != null) {
            exchange.getIn().setBody(ctlFile, File.class);
        }
    }

    private void setExchangeHeaders(Exchange exchange, FaultsReport errorReport, NewBatchJob newJob) {
        exchange.getIn().setHeader("BatchJobId", newJob.getId());
        if (errorReport.hasErrors()) {
            exchange.getIn().setHeader("hasErrors", errorReport.hasErrors());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.BATCH_REQUEST.name());
        }
    }

    private void createResourceEntryAndAddToJob(File zipFile, NewBatchJob newJob) throws IOException {
        ResourceEntry resourceName = new ResourceEntry();
        resourceName.setResourceName(zipFile.getCanonicalPath());
        resourceName.setResourceId(zipFile.getName());
        resourceName.setExternallyUploadedResourceId(zipFile.getName());
        resourceName.setResourceFormat(FileFormat.ZIP_FILE.getCode());
        newJob.getResourceEntries().add(resourceName);
    }

    public ZipFileHandler getHandler() {
        return zipFileHandler;
    }

    public void setHandler(ZipFileHandler handler) {
        this.zipFileHandler = handler;
    }

}

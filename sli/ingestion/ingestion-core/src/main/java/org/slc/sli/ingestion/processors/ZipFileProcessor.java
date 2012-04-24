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
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.handler.ZipFileHandler;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.util.BatchJobUtils;

/**
 * Zip file handler.
 *
 * @author okrook
 *
 */
@Component
public class ZipFileProcessor implements Processor {

    Logger log = LoggerFactory.getLogger(ZipFileProcessor.class);

    @Autowired
    private ZipFileHandler handler;

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Override
    public void process(Exchange exchange) throws Exception {

        processUsingNewBatchJob(exchange);
    }

    private void processUsingNewBatchJob(Exchange exchange) throws Exception {

        String batchJobId = null;

        try {
            log.info("Received zip file: " + exchange.getIn());

            File zipFile = exchange.getIn().getBody(File.class);

            batchJobId = NewBatchJob.createId(zipFile.getName());
            NewBatchJob newJob = new NewBatchJob(batchJobId);
            newJob.setStatus(BatchJobStatusType.RUNNING.getName());

            Stage stage = new Stage();
            stage.setStageName(BatchJobStageType.ZIP_FILE_PROCESSOR.getName());
            stage.startStage();

            exchange.getIn().setHeader("BatchJobId", batchJobId);

            FaultsReport errorReport = new FaultsReport();

            File ctlFile = handler.handle(zipFile, errorReport);

            BatchJobUtils
                    .writeErrorsWithDAO(batchJobId, BatchJobStageType.ZIP_FILE_PROCESSOR, errorReport, batchJobDAO);

            ResourceEntry resourceName = new ResourceEntry();
            resourceName.setResourceName(zipFile.getCanonicalPath());
            resourceName.setResourceId(zipFile.getName());
            newJob.getResourceEntries().add(resourceName);

            if (ctlFile != null) {
                newJob.setSourceId(ctlFile.getParentFile().getCanonicalPath() + File.separator);
            }
            stage.stopStage();
            newJob.getStages().add(stage);
            batchJobDAO.saveBatchJob(newJob);

            if (errorReport.hasErrors()) {
                exchange.getIn().setHeader("hasErrors", errorReport.hasErrors());
                exchange.getIn().setHeader("IngestionMessageType", MessageType.BATCH_REQUEST.name());
            } else if (ctlFile != null) {
                exchange.getIn().setBody(ctlFile, File.class);
            }
        } catch (Exception exception) {
            exchange.getIn().setHeader("ErrorMessage", exception.toString());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            log.error("Exception:", exception);
            if (batchJobId != null) {
                Error error = Error.createIngestionError(batchJobId, BatchJobStageType.ZIP_FILE_PROCESSOR.getName(),
                        null, null, null, null, FaultType.TYPE_ERROR.getName(), null, exception.toString());
                batchJobDAO.saveError(error);
            }
        }
    }

    public ZipFileHandler getHandler() {
        return handler;
    }

    public void setHandler(ZipFileHandler handler) {
        this.handler = handler;
    }

}

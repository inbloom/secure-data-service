 package org.slc.sli.ingestion.processors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.handler.ZipFileHandler;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.util.performance.Profiled;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;

/**
 * Zip file handler.
 *
 * @author okrook
 *
 */
@Component
public class ZipFileProcessor implements Processor, MessageSourceAware {

    Logger log = LoggerFactory.getLogger(ZipFileProcessor.class);

    @Autowired
    private ZipFileHandler handler;

    private MessageSource messageSource;

    @Override
    @Profiled
    public void process(Exchange exchange) throws Exception {

        String batchJobId = null;

        try {
            log.info("Received zip file: " + exchange.getIn());

            File zipFile = exchange.getIn().getBody(File.class);

            BatchJob job = BatchJob.createDefault(zipFile.getName());

            // TODO BJI: create job in the db
            batchJobId = NewBatchJob.createId(zipFile.getName());
            NewBatchJob newJob = new NewBatchJob(batchJobId);
            Stage stage = new Stage();
            stage.setStageName("ZipFileProcessor");
            newJob.startStage(stage);

            exchange.getIn().setHeader("BatchJobId", batchJobId);
            BatchJobDAO batchJobDAO = new BatchJobMongoDA();

            ErrorReport errorReport = job.getFaultsReport();

            File ctlFile = handler.handle(zipFile, errorReport);
            
            List<ResourceEntry> fileEntries = new ArrayList<ResourceEntry>();
            ResourceEntry resourceName = new ResourceEntry();
            resourceName.setResourceName(zipFile.getName());
            fileEntries.add(resourceName);
            newJob.setResourceEntries(fileEntries);
            List<Stage> stages = new ArrayList<Stage>();

            newJob.stopStage(stage);
            stages.add(stage);
            newJob.setStages(stages);
            batchJobDAO.saveBatchJob(newJob);
            
            if (errorReport.hasErrors()) {
                exchange.getIn().setBody(job, BatchJob.class);
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
                BatchJobMongoDA.logBatchStageError(batchJobId, BatchJobStageType.ZIP_FILE_PROCESSING, FaultType.TYPE_ERROR.getName(), null, exception.toString());
            }
        }
    }

    public ZipFileHandler getHandler() {
        return handler;
    }

    public void setHandler(ZipFileHandler handler) {
        this.handler = handler;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}

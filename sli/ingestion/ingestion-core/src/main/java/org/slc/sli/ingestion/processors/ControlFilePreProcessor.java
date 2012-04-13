package org.slc.sli.ingestion.processors;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.BatchJobStatusType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
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


        processExistingBatchJob(exchange);

        // TODO we are doing both in parallel for now, but will replace the existing once testing is done
        // this writes to a newJobxxx.txt output file in the lz
//        processUsingNewBatchJob(exchange);
    }

    private void processExistingBatchJob(Exchange exchange) throws Exception {

        String batchJobId = exchange.getIn().getHeader("BatchJobId", String.class);

        // TODO handle invalid control file (user error)
        // TODO handle IOException or other system error
        try {
            File controlFile = exchange.getIn().getBody(File.class);
            NewBatchJob newJob = null;
            BatchJobDAO batchJobDAO = new BatchJobMongoDA();
            if (batchJobId == null) {
                batchJobId = NewBatchJob.createId(controlFile.getName());
                exchange.getIn().setHeader("BatchJobId", batchJobId);
                log.info("Created job [{}]", batchJobId);
                newJob = new NewBatchJob(batchJobId);
                newJob.setStatus(BatchJobStatusType.RUNNING.getName());
            } else {
                newJob = batchJobDAO.findBatchJobById(batchJobId);
            }

            newJob.setSourceId(landingZone.getLZId());

            Stage stage = new Stage();
            stage.setStageName(BatchJobStageType.CONTROL_FILE_PREPROCESSING.getName());
            stage.startStage();

            // JobLogStatus.startStage(batchJobId, stageName)

            ControlFile cf = ControlFile.parse(controlFile);

            newJob.setTotalFiles(cf.getFileEntries().size());

            HashMap<String, String> batchProperties = new HashMap<String, String>();
            Enumeration<Object> keys = cf.getConfigProperties().keys();
            Enumeration<Object> elements = cf.getConfigProperties().elements();

            while (keys.hasMoreElements()) {
                String key = keys.nextElement().toString();
                String element = elements.nextElement().toString();
                batchProperties.put(key, element);
            }
            newJob.setBatchProperties(batchProperties);

            for (IngestionFileEntry file : cf.getFileEntries()) {
                ResourceEntry resourceEntry = new ResourceEntry();
                resourceEntry.update(file.getFileFormat().toString(), file.getFileType().toString(), file.getChecksum(), 0, 0);
                resourceEntry.setResourceName(file.getFileName());
                newJob.getResourceEntries().add(resourceEntry);
            }

            stage.stopStage();
            newJob.getStages().add(stage);
            batchJobDAO.saveBatchJob(newJob);

            // set headers for ingestion routing
            exchange.getIn().setBody(new ControlFileDescriptor(cf, landingZone), ControlFileDescriptor.class);
            exchange.getIn().setHeader("IngestionMessageType", MessageType.BATCH_REQUEST.name());

            // TODO May not need this ... JobLogStatus.completeStage(batchJobId, stageName)

        } catch (Exception exception) {
            exchange.getIn().setHeader("ErrorMessage", exception.toString());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            log.error("Exception:",  exception);
            if (batchJobId != null) {
                BatchJobMongoDA.logBatchStageError(batchJobId, BatchJobStageType.CONTROL_FILE_PREPROCESSING, FaultType.TYPE_ERROR.getName(), null, exception.toString());
            }
        }

    }

    private void processUsingNewBatchJob(Exchange exchange) throws Exception {

        String batchJobId = exchange.getIn().getHeader("BatchJobId", String.class);

        // TODO handle invalid control file (user error)
        // TODO handle IOException or other system error
        try {
            File controlFile = exchange.getIn().getBody(File.class);
            NewBatchJob newJob = null;
            BatchJobDAO batchJobDAO = new BatchJobMongoDA();
            if (batchJobId == null) {
                batchJobId = NewBatchJob.createId(controlFile.getName());
                exchange.getIn().setHeader("BatchJobId", batchJobId);
                log.info("Created job [{}]", batchJobId);
                newJob = new NewBatchJob(batchJobId);
                newJob.setStatus(BatchJobStatusType.RUNNING.getName());
            } else {
                newJob = batchJobDAO.findBatchJobById(batchJobId);
            }

            newJob.setSourceId(landingZone.getLZId());

            Stage stage = new Stage();
            stage.setStageName(BatchJobStageType.CONTROL_FILE_PREPROCESSING.getName());
            stage.startStage();

            // JobLogStatus.startStage(batchJobId, stageName)

            ControlFile cf = ControlFile.parse(controlFile);

            newJob.setTotalFiles(cf.getFileEntries().size());

            HashMap<String, String> batchProperties = new HashMap<String, String>();
            Enumeration<Object> keys = cf.getConfigProperties().keys();
            Enumeration<Object> elements = cf.getConfigProperties().elements();

            while (keys.hasMoreElements()) {
                String key = keys.nextElement().toString();
                String element = elements.nextElement().toString();
                batchProperties.put(key, element);
            }
            newJob.setBatchProperties(batchProperties);

            for (IngestionFileEntry file : cf.getFileEntries()) {
                ResourceEntry resourceEntry = new ResourceEntry();
                resourceEntry.update(file.getFileFormat().toString(), file.getFileType().toString(), file.getChecksum(), 0, 0);
                resourceEntry.setResourceName(file.getFileName());
                newJob.getResourceEntries().add(resourceEntry);
            }

            stage.stopStage();
            newJob.getStages().add(stage);
            batchJobDAO.saveBatchJob(newJob);

            // set headers for ingestion routing
            exchange.getIn().setBody(new ControlFileDescriptor(cf, landingZone), ControlFileDescriptor.class);
            exchange.getIn().setHeader("IngestionMessageType", MessageType.BATCH_REQUEST.name());

            // TODO May not need this ... JobLogStatus.completeStage(batchJobId, stageName)

        } catch (Exception exception) {
            exchange.getIn().setHeader("ErrorMessage", exception.toString());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            log.error("Exception:",  exception);
            if (batchJobId != null) {
                BatchJobMongoDA.logBatchStageError(batchJobId, BatchJobStageType.CONTROL_FILE_PREPROCESSING, FaultType.TYPE_ERROR.getName(), null, exception.toString());
            }
        }

    }

}

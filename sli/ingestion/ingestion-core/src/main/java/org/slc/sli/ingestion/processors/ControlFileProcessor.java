package org.slc.sli.ingestion.processors;

import java.util.Enumeration;
import java.util.HashMap;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.landingzone.BatchJobAssembler;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.validation.ControlFileValidator;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.util.performance.Profiled;

/**
 * Control file processor.
 *
 * @author okrook
 *
 */
@Component
public class ControlFileProcessor implements Processor {

    @Autowired
    private ControlFileValidator validator;

    private Logger log = LoggerFactory.getLogger(ControlFileProcessor.class);

    private static final String PURGE = "purge";

    @Autowired
    private BatchJobAssembler jobAssembler;

    @Override
    @Profiled
    public void process(Exchange exchange) throws Exception {


        processExistingBatchJob(exchange);

        // TODO we are doing both in parallel for now, but will replace the existing once testing is done
        // this writes to a newJobxxx.txt output file in the lz
//        processUsingNewBatchJob(exchange);
    }

    private void processExistingBatchJob(Exchange exchange) throws Exception {
        String batchJobId = exchange.getIn().getHeader("BatchJobId", String.class);
        if (batchJobId == null) {
            exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            log.error("Error:", "No BatchJobId specified in " + this.getClass().getName()
                    + " exchange message header.");
        }

        try {
            // get the job from the db
            BatchJobDAO batchJobDAO = new BatchJobMongoDA();
            NewBatchJob newJob = batchJobDAO.findBatchJobById(batchJobId);

            Stage stage = new Stage();
            stage.setStageName(BatchJobStageType.CONTROL_FILE_PROCESSING.getName());
            stage.startStage();
            // TODO JobLogStatus
            // Create the stage and metric
            // JobLogStatus.startStage(batchJobId, stageName)

            long startTime = System.currentTimeMillis();

            ControlFileDescriptor cfd = exchange.getIn().getBody(ControlFileDescriptor.class);

            BatchJob job = getJobAssembler()
                    .assembleJob(cfd, (String) exchange.getIn().getHeader("CamelFileNameOnly"));

            ControlFile cf = cfd.getFileItem();

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
                resourceEntry.update(file.getFileFormat().getCode() ,
                        file.getFileType().getName(), file.getChecksum(), 0, 0);
                resourceEntry.setResourceName(newJob.getSourceId() + file.getFileName());
                resourceEntry.setResourceId(file.getFileName());
                newJob.getResourceEntries().add(resourceEntry);
            }

            long endTime = System.currentTimeMillis();
            log.info("Assembled batch job [{}] in {} ms", job.getId(), endTime - startTime);


            //  TODO set properties on the exchange based on job properties
            // TODO set faults on the exchange if the control file sucked (?)

            // TODO Create the stage and metric
            // JobLogStatus.completeStage(batchJobId, stageName)
            stage.stopStage();
            newJob.getStages().add(stage);
            batchJobDAO.saveBatchJob(newJob);

            // set the exchange outbound message to the value of the job
            exchange.getIn().setBody(job, BatchJob.class);

            // set headers
            exchange.getIn().setHeader("hasErrors", job.getFaultsReport().hasErrors());
            if (job.getProperty(PURGE) != null) {
                exchange.getIn().setHeader("IngestionMessageType", MessageType.PURGE.name());
            } else {
            exchange.getIn().setHeader("IngestionMessageType", MessageType.CONTROL_FILE_PROCESSED.name());
            }
        } catch (Exception exception) {
            exchange.getIn().setHeader("ErrorMessage", exception.toString());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            log.error("Exception:", exception);
            if (batchJobId != null) {
                BatchJobMongoDA.logBatchStageError(batchJobId, BatchJobStageType.CONTROL_FILE_PROCESSING,
                        FaultType.TYPE_ERROR.getName(), null, exception.toString());
            }
        }

    }

    private void processUsingNewBatchJob(Exchange exchange) throws Exception {
        String batchJobId = exchange.getIn().getHeader("BatchJobId", String.class);
        if (batchJobId == null) {
            exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            log.error("Error:", "No BatchJobId specified in " + this.getClass().getName()
                    + " exchange message header.");
        }

        try {
            // get the job from the db
            BatchJobDAO batchJobDAO = new BatchJobMongoDA();
            NewBatchJob newJob = batchJobDAO.findBatchJobById(batchJobId);

            Stage stage = new Stage();
            stage.setStageName(BatchJobStageType.CONTROL_FILE_PROCESSING.getName());
            stage.startStage();
            // TODO JobLogStatus
            // Create the stage and metric
            // JobLogStatus.startStage(batchJobId, stageName)

            long startTime = System.currentTimeMillis();

            ControlFileDescriptor cfd = exchange.getIn().getBody(ControlFileDescriptor.class);


//            BatchJob job = getJobAssembler()
//                    .assembleJob(cfd, (String) exchange.getIn().getHeader("CamelFileNameOnly"));

            // TODO This code is basically the new job assembler.  move it to a
            // better place.
            ControlFile cf = cfd.getFileItem();

            HashMap<String, String> batchProperties = new HashMap<String, String>();
            Enumeration<Object> keys = cf.getConfigProperties().keys();
            Enumeration<Object> elements = cf.getConfigProperties().elements();

            while (keys.hasMoreElements()) {
                String key = keys.nextElement().toString();
                String element = elements.nextElement().toString();
                batchProperties.put(key, element);
            }
            newJob.setBatchProperties(batchProperties);

            FaultsReport errorReport = new FaultsReport();
//            ControlFileDescriptor cfd = new ControlFileDescriptor(cf, landingZone);

            //TODO Deal with validator being autowired in BatchJobAssembler
            // This code should live there anyway.
            if (validator.isValid(cfd, errorReport)) {
                for (IngestionFileEntry file : cf.getFileEntries()) {
                    ResourceEntry resourceEntry = new ResourceEntry();
                    resourceEntry.update(file.getFileFormat().getCode() ,
                            file.getFileType().getName(), file.getChecksum(), 0, 0);
                    resourceEntry.setResourceName(newJob.getSourceId() + file.getFileName());
                    resourceEntry.setResourceId(file.getFileName());
                    newJob.getResourceEntries().add(resourceEntry);
                }
            }

            Error.writeErrorsToMongo(batchJobId, errorReport);

            long endTime = System.currentTimeMillis();
            log.info("Assembled batch job [{}] in {} ms", newJob.getId(), endTime - startTime);

            // TODO set properties on the exchange based on job properties
            // TODO set faults on the exchange if the control file sucked (?)

            // TODO Create the stage and metric
            // JobLogStatus.completeStage(batchJobId, stageName)
            stage.stopStage();
            newJob.getStages().add(stage);
            batchJobDAO.saveBatchJob(newJob);

            // set the exchange outbound message to the value of the job
            exchange.getIn().setBody(newJob, NewBatchJob.class);

            // set headers
            // This error section is now handled by the writeErrorsToMongo above
//            exchange.getIn().setHeader("hasErrors", job.getFaultsReport().hasErrors());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.CONTROL_FILE_PROCESSED.name());

        } catch (Exception exception) {
            exchange.getIn().setHeader("ErrorMessage", exception.toString());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            log.error("Exception:", exception);
            if (batchJobId != null) {
                BatchJobMongoDA.logBatchStageError(batchJobId, BatchJobStageType.CONTROL_FILE_PROCESSING,
                        FaultType.TYPE_ERROR.getName(), null, exception.toString());
            }
        }

    }

    public BatchJobAssembler getJobAssembler() {
        return jobAssembler;
    }

    public void setJobAssembler(BatchJobAssembler jobAssembler) {
        this.jobAssembler = jobAssembler;
    }

}

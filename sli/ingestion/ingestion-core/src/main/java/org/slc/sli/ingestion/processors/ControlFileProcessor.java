package org.slc.sli.ingestion.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.landingzone.BatchJobAssembler;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.model.NewBatchJob;
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

    private Logger log = LoggerFactory.getLogger(ControlFileProcessor.class);

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

            long endTime = System.currentTimeMillis();
            log.info("Assembled batch job [{}] in {} ms", job.getId(), endTime - startTime);

            // TODO set properties on the exchange based on job properties
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
            exchange.getIn().setHeader("IngestionMessageType", MessageType.BULK_TRANSFORM_REQUEST.name());

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

            //TODO Double-check, but I no longer think this is necessary.  The
            // filenames are already persisted in the database from the pre-processor step
//            BatchJob job = getJobAssembler()
//                    .assembleJob(cfd, (String) exchange.getIn().getHeader("CamelFileNameOnly"));

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
            // TODO FIX THIS HAS ERRORS SECTION
//            exchange.getIn().setHeader("hasErrors", job.getFaultsReport().hasErrors());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.BULK_TRANSFORM_REQUEST.name());

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

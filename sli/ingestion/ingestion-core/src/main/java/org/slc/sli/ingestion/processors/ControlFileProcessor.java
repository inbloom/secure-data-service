package org.slc.sli.ingestion.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.landingzone.BatchJobAssembler;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.util.performance.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

        try {
            /* TODO JobLogStatus
               // Get the batch job ID from the exchange
               batchJobId = exchange.getIn().getBody(String.class);
               
               // Get the job from the db
               BatchJob job = JobLogStatus.getJob(batchJobId)
               
               // Create the stage and metric
               JobLogStatus.startStage(batchJobId, stageName)
             */
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

            // set the exchange outbound message to the value of the job
            exchange.getIn().setBody(job, BatchJob.class);
            // TODO JobLogStatus.addFile(job.getId(), ctlFile);
            // Pass the Id along
            // exchange.getIn().setBody(job.getId(), String.class);

            // set headers
            exchange.getIn().setHeader("hasErrors", job.getFaultsReport().hasErrors());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.BULK_TRANSFORM_REQUEST.name());

        } catch (Exception exception) {
            exchange.getIn().setHeader("ErrorMessage", exception.toString());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            /*
             * TODO JobLogStatus
             * if (batchJobId)
             * JobLogStatus.log(batchJobId, this.getClass().getName(), "ERROR", exception)
             */
            log.error("Exception:", exception);
        }
    }
    
    public BatchJobAssembler getJobAssembler() {
        return jobAssembler;
    }
    
    public void setJobAssembler(BatchJobAssembler jobAssembler) {
        this.jobAssembler = jobAssembler;
    }
    
}

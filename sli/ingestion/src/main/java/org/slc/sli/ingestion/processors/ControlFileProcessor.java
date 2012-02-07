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
            long startTime = System.currentTimeMillis();

            ControlFileDescriptor cfd = exchange.getIn().getBody(ControlFileDescriptor.class);

            BatchJob job = getJobAssembler()
                    .assembleJob(cfd, (String) exchange.getIn().getHeader("CamelFileNameOnly"));

            long endTime = System.currentTimeMillis();
            log.info("Assembled batch job [{}] in {} ms", job.getId(), endTime - startTime);

            // TODO set properties on the exchange based on job properties
            // TODO set faults on the exchange if the control file sucked (?)

            // set the exchange outbound message to the value of the job
            exchange.getIn().setBody(job, BatchJob.class);
        
            // set headers for ingestion routing
            if (job.getFaultsReport().hasErrors()) {
                exchange.getIn().setHeader("ErrorMessage", "batch job error");
                exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            } else {
                exchange.getIn().setHeader("IngestionMessageType", MessageType.BULK_TRANSFORM_REQUEST.name());
            }
 
        } catch (Exception exception) {
            exchange.getIn().setHeader("ErrorMessage", exception.toString());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        }
    }

    public BatchJobAssembler getJobAssembler() {
        return jobAssembler;
    }

    public void setJobAssembler(BatchJobAssembler jobAssembler) {
        this.jobAssembler = jobAssembler;
    }

}

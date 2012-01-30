package org.slc.sli.ingestion.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.landingzone.BatchJobAssembler;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.queues.MessageType;

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
    @Profiled(tag = "ControlFileProcessor - file {$0.getIn().getHeader(\"CamelFileNameOnly\")} - batch {$0.getExchangeId()}")
    public void process(Exchange exchange) throws Exception {    	
        long startTime = System.currentTimeMillis();

        ControlFileDescriptor cfd = exchange.getIn().getBody(ControlFileDescriptor.class);

        BatchJob job = this.getJobAssembler()
                .assembleJob(cfd, (String) exchange.getIn().getHeader("CamelFileNameOnly"));

        long endTime = System.currentTimeMillis();
        log.info("Assembled batch job [{}] in {} ms", job.getId(), endTime - startTime);

        // TODO set properties on the exchange based on job properties
        // TODO set faults on the exchange if the control file sucked (?)

        // set the exchange outbound message to the value of the job
        exchange.getIn().setBody(job, BatchJob.class);
        exchange.getIn().setHeader("hasErrors", job.getFaultsReport().hasErrors());
        exchange.getIn().setHeader("IngestionMessageType", MessageType.BULK_TRANSFORM_REQUEST.name());

    }

    public BatchJobAssembler getJobAssembler() {
        return jobAssembler;
    }

    public void setJobAssembler(BatchJobAssembler jobAssembler) {
        this.jobAssembler = jobAssembler;
    }

}

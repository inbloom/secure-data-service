package org.slc.sli.ingestion.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.util.performance.Profiled;

/**
 * Camel interface for processing transformed data from edFi to SLI format.
 * 
 * @author ifaybyshev
 *
 */
@Component
public class DataModelTransformationProcessor implements Processor {
    
    private static final Logger LOG = LoggerFactory.getLogger(DataModelTransformationProcessor.class);
    
    
    /**
     * Camel Exchange process callback method
     *
     * @param exchange
     */
    @Override
    @ExtractBatchJobIdToContext
    @Profiled
    public void process(Exchange exchange) {

        BatchJob job = exchange.getIn().getBody(BatchJob.class);

        transformDataModel(job);

        exchange.getIn().setHeader("IngestionMessageType", MessageType.MERGE_REQUEST.name());
    }

    private void transformDataModel(BatchJob job) {
        LOG.info("transforming data model in BatchJob: {}", job);

        // TODO implement

    }
    
}

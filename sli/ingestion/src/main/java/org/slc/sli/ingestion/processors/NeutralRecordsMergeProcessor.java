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
 *
 * This processor is responsible for merging any NeutralRecord (Ed-Fi) whose corresponding record
 * type has been collapsed
 * into another in the sliXsd data model. These 'processed' NeutralRecords will later be mapped to
 * Entity (sliXsd).
 *
 */
@Component
public class NeutralRecordsMergeProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(NeutralRecordsMergeProcessor.class);

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

        mergeNeutralRecordsInBatchJob(job);

        exchange.getIn().setHeader("IngestionMessageType", MessageType.PERSIST_REQUEST.name());
    }

    private void mergeNeutralRecordsInBatchJob(BatchJob job) {
        LOG.info("merging NeutralRecord entries in BatchJob: {}", job);

        // TODO implement

    }

}

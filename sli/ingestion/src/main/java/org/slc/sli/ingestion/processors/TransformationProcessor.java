package org.slc.sli.ingestion.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.transformation.TransformationFactory;
import org.slc.sli.ingestion.transformation.TransformationStrategy;
import org.slc.sli.util.performance.Profiled;

/**
 * Camel interface for processing Transformation of data.
 *
 * @author ifaybyshev
 *
 */
@Component
public class TransformationProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(TransformationProcessor.class);

    private NeutralRecordMongoAccess neutralRecordMongoAccess;

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

        performDataTransformations(job.getId());

        exchange.getIn().setHeader("IngestionMessageType", MessageType.DATA_MODEL_TRANSFORMATION.name());
    }

    /**
     * Invokes transformations strategies
     *
     * @param job
     */
    void performDataTransformations(String jobId) {
        LOG.info("performing data transformation BatchJob: {}", jobId);

        TransformationStrategy<NeutralRecordMongoAccess, String> strategy = (TransformationStrategy<NeutralRecordMongoAccess, String>) TransformationFactory
                .getTransformationStrategy(TransformationFactory.ASSESSMENT_COMBINER);
        strategy.setJobId(jobId);
        strategy.handle(this.neutralRecordMongoAccess);

    }

    public void setNeutralRecordMongoAccess(NeutralRecordMongoAccess neutralRecordMongoAccess) {
        this.neutralRecordMongoAccess = neutralRecordMongoAccess;
    }

}

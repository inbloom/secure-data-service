package org.slc.sli.ingestion.processors;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.transformation.TransformationFactory;
import org.slc.sli.ingestion.transformation.Transmogrifier;
import org.slc.sli.util.performance.Profiled;

/**
 * Camel processor for transformation of data.
 *
 * @author dduran
 *
 */
@Component
public class TransformationProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(TransformationProcessor.class);

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

        Collection<String> collectionNames = defineCollectionsInJob();

        Transmogrifier transmogrifier = TransformationFactory.createTransmogrifier(collectionNames, jobId);

        transmogrifier.execute();

    }

    private Collection<String> defineCollectionsInJob() {

        // TODO: provide proper implementation

        Collection<String> collectionNames = new ArrayList<String>();
        collectionNames.add(TransformationFactory.ASSESSMENT_COMBINER);

        return collectionNames;
    }

}

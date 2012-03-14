package org.slc.sli.ingestion.processors;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private TransformationFactory transformationFactory;

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

        exchange.getIn().setHeader("IngestionMessageType", MessageType.PERSIST_REQUEST.name());
    }

    /**
     * Invokes transformations strategies
     *
     * @param job
     */
    void performDataTransformations(String jobId) {
        LOG.info("performing data transformation BatchJob: {}", jobId);

        List<String> collectionNames = defineCollectionsInJob();

        Transmogrifier transmogrifier = transformationFactory.createTransmogrifier(collectionNames, jobId);

        transmogrifier.executeTransformations();

    }

    private List<String> defineCollectionsInJob() {

        // TODO: provide proper implementation

        List<String> collectionNames = new ArrayList<String>();
        collectionNames.add("assessement");

        return collectionNames;
    }

    public TransformationFactory getTransformationFactory() {
        return transformationFactory;
    }

    public void setTransformationFactory(TransformationFactory transformationFactory) {
        this.transformationFactory = transformationFactory;
    }

}

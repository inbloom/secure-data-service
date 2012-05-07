package org.slc.sli.ingestion.routes;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.WorkNote;

/**
 * @author ifaybyshev
 *
 */
public class SplitMessageAggregationStrategy implements AggregationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(SplitMessageAggregationStrategy.class);

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }

        String batchJobId = oldExchange.getIn().getHeader("BatchJobId", String.class);
        LOG.info("Aggregating work for batch = " + batchJobId);

        WorkNote workNote = oldExchange.getIn().getBody(WorkNote.class);
        WorkNote newWorkNote = newExchange.getIn().getBody(WorkNote.class);

        String orders = oldExchange.getIn().getBody(String.class);
        String newLine = newExchange.getIn().getBody(String.class);

        LOG.info("Aggregate old workNote: " + workNote.getCollection() + " - " + workNote.getRangeMinimum() + "/" + workNote.getRangeMaximum());
        LOG.info("Aggregate new workNote: " + newWorkNote.getCollection() + " - " + newWorkNote.getRangeMinimum() + "/" + newWorkNote.getRangeMaximum());

        workNote.setCollection(workNote.getCollection() + "/" + newWorkNote.getCollection());
        oldExchange.getIn().setBody(workNote);

        return oldExchange;
    }
}
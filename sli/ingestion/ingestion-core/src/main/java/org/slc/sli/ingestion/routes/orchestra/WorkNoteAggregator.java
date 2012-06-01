package org.slc.sli.ingestion.routes.orchestra;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.queues.MessageType;

/**
 * WorkNote aggregator to be used from camel
 *
 * @author dduran
 *
 */
public class WorkNoteAggregator implements AggregationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(WorkNoteAggregator.class);

    @Override
    public Exchange aggregate(Exchange hasBeenAggregated, Exchange toBeAggregated) {

        WorkNote workNote = toBeAggregated.getIn().getBody(WorkNote.class);
        if (workNote != null) {

            // on the first message, hasBeenAggregated will be null so "intialize" it
            if (hasBeenAggregated == null) {
                hasBeenAggregated = initializeAggregationExchange(toBeAggregated);
            }

            @SuppressWarnings("unchecked")
            List<WorkNote> workNoteList = hasBeenAggregated.getIn().getBody(List.class);

            workNoteList.add(workNote);

            LOG.info("aggregated WorkNote: {} ", workNote);
        }

        return hasBeenAggregated;
    }

    private Exchange initializeAggregationExchange(Exchange toBeAggregated) {

        WorkNote workNote = toBeAggregated.getIn().getBody(WorkNote.class);

        Exchange hasBeenAggregated = toBeAggregated;

        if (MessageType.DATA_TRANSFORMATION.name().equals(toBeAggregated.getIn().getHeader("IngestionMessageType"))) {
            LOG.info("Setting {} as the aggregation completion size for {}", workNote.getBatchSize(),
                    workNote.getIngestionStagedEntity());

            hasBeenAggregated.getIn().setHeader("workNoteByEntityCount", workNote.getBatchSize());
            hasBeenAggregated.getIn().setHeader("totalWorkNoteCount", toBeAggregated.getProperty("CamelSplitSize"));
        }
        hasBeenAggregated.getIn().setBody(new ArrayList<WorkNote>(), List.class);

        return hasBeenAggregated;
    }
}

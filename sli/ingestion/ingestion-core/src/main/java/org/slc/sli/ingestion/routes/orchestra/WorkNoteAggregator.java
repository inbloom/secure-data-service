package org.slc.sli.ingestion.routes.orchestra;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.WorkNote;

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

        Exchange hasBeenAggregated = toBeAggregated;

        hasBeenAggregated.getIn().setBody(new ArrayList<WorkNote>(), List.class);

        return hasBeenAggregated;
    }
}

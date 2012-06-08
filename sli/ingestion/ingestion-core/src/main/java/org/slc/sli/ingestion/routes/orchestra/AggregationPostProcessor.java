package org.slc.sli.ingestion.routes.orchestra;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.IngestionStagedEntity;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.WorkNoteImpl;

/**
 *
 * @author dduran
 *
 */
@Component
public class AggregationPostProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(AggregationPostProcessor.class);

    @Autowired
    private StagedEntityTypeDAO stagedEntityTypeDAO;

    @Override
    public void process(Exchange exchange) throws Exception {

        LOG.info("Aggregation completed for current tier. Will now remove entities in tier from processing pool.");

        @SuppressWarnings("unchecked")
        List<WorkNote> workNoteList = exchange.getIn().getBody(List.class);

        String jobId = null;

        for (WorkNote workNote : workNoteList) {

            // all of these WorkNotes should have the same batchjobid, since this is the aggregation
            // criteria. grabbing it out of the first one will suffice.
            if (jobId == null) {
                jobId = workNote.getBatchJobId();
            }

            removeWorkNoteFromRemainingEntities(jobId, workNote);
        }
        exchange.getIn().setHeader("jobId", jobId);

        if (stagedEntityTypeDAO.getStagedEntitiesForJob(jobId).size() == 0) {
            LOG.info("Processing pool is now empty, continue out of orchestra routes.");

            exchange.getIn().setHeader("processedAllStagedEntities", true);
            WorkNote workNote = WorkNoteImpl.createSimpleWorkNote(jobId);
            exchange.getIn().setBody(workNote);
        }
    }

    private void removeWorkNoteFromRemainingEntities(String jobId, WorkNote workNote) {

        IngestionStagedEntity stagedEntityToRemove = workNote.getIngestionStagedEntity();

        if (stagedEntityTypeDAO.removeStagedEntityForJob(stagedEntityToRemove, jobId)) {
            LOG.info("removed EdfiEntity from processing pool: {}", stagedEntityToRemove.getCollectionNameAsStaged());
        }
    }

}

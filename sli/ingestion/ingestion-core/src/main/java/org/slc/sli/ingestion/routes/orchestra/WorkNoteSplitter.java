package org.slc.sli.ingestion.routes.orchestra;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.IngestionStagedEntity;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.WorkNoteImpl;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;

/**
 * WorkNote splitter to be used from camel
 *
 * @author dduran
 *
 */
@Component
public class WorkNoteSplitter {
    private static final Logger LOG = LoggerFactory.getLogger(WorkNoteSplitter.class);

    @Value("${sli.ingestion.splitChunkSize}")
    private int splitChunkSize;

    @Autowired
    private StagedEntityTypeDAO stagedEntityTypeDAO;

    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    /**
     * Splits the work that can be processed in parallel next round into individual WorkNotes.
     *
     * @param exchange
     * @return list of WorkNotes that camel will iterate over, issuing each as a new message
     * @throws IllegalStateException
     */
    public List<WorkNote> split(Exchange exchange) {

        String jobId = exchange.getIn().getHeader("jobId").toString();
        LOG.info("orchestrating splitting for job: {}", jobId);

        Set<IngestionStagedEntity> stagedEntities = stagedEntityTypeDAO.getStagedEntitiesForJob(jobId);

        if (stagedEntities.size() == 0) {
            throw new IllegalStateException(
                    "stagedEntities is empty at splitting stage. should have been redirected prior to this point.");
        }

        Set<IngestionStagedEntity> nextTierEntities = IngestionStagedEntity.cleanse(stagedEntities);

        List<WorkNote> workNoteList = createWorkNotes(nextTierEntities, jobId);

        LOG.info("{} total WorkNotes created and ready for splitting for current tier.", workNoteList.size());

        return workNoteList;
    }

    public List<WorkNote> passThroughSplit(Exchange exchange) {

        @SuppressWarnings("unchecked")
        List<WorkNote> workNoteList = exchange.getIn().getBody(List.class);

        LOG.info("Splitting out (pass-through) list of WorkNotes: {}", workNoteList);

        return workNoteList;
    }

    private List<WorkNote> createWorkNotes(Set<IngestionStagedEntity> stagedEntities, String jobId) {
        LOG.info("creating WorkNotes for processable entities: {}", stagedEntities);

        List<WorkNote> workNoteList = new ArrayList<WorkNote>();
        for (IngestionStagedEntity stagedEntity : stagedEntities) {

            // potentially unsafe cast but it was decided that it is unlikely for us to hit max int
            // size for a staging db collection count.
            int numRecords = (int) neutralRecordMongoAccess.getRecordRepository().countForJob(
                    stagedEntity.getCollectionNameAsStaged(), new NeutralQuery(), jobId);

            LOG.info("Records for collection {}: {}", stagedEntity.getCollectionNameAsStaged(), numRecords);

            if (!stagedEntity.getEdfiEntity().isSelfReferencing() && numRecords > splitChunkSize) {
                int numberOfBatches = (int) Math.ceil((double) numRecords / splitChunkSize);

                LOG.info("Entity split threshold reached. Splitting {} collection into {} batches of WorkNotes.",
                        stagedEntity.getCollectionNameAsStaged(), numberOfBatches);

                for (int i = 0; i < numRecords; i += splitChunkSize) {
                    int chunk = ((i + splitChunkSize) > numRecords) ? (numRecords) : (i + splitChunkSize);
                    WorkNote workNote = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, i, chunk - 1,
                            numberOfBatches);
                    workNoteList.add(workNote);
                }
            } else {
                LOG.info("Creating one WorkNote for collection: {}.", stagedEntity.getCollectionNameAsStaged());
                WorkNote workNote = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, 0, numRecords - 1, 1);
                workNoteList.add(workNote);
            }
        }
        return workNoteList;
    }
}

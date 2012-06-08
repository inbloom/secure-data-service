package org.slc.sli.ingestion.routes.orchestra;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.IngestionStagedEntity;
import org.slc.sli.ingestion.NeutralRecord;
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

    private int thresholdPct = 30;

    @Autowired
    private StagedEntityTypeDAO stagedEntityTypeDAO;

    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    public enum MatchEnumeration {
        good, small, large
    }

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

            long minTime = getMinCreationTimeForEntity(stagedEntity, jobId);
            long maxTime = getMaxCreationTimeForEntity(stagedEntity, jobId);

            if (!stagedEntity.getEdfiEntity().isSelfReferencing() && numRecords > splitChunkSize) {

                List<WorkNote> collectionWorkNotes = constructCollectionWorkNotes(new ArrayList<WorkNote>(), jobId,
                        stagedEntity, minTime, maxTime);

                Iterator<WorkNote> workNoteIterator = collectionWorkNotes.iterator();
                while (workNoteIterator.hasNext()) {
                    WorkNote wn = workNoteIterator.next();
                    wn.setBatchSize(collectionWorkNotes.size());
                    workNoteList.add(wn);
                }

                LOG.info("Entity split threshold reached. Splitting {} collection into {} batches of WorkNotes.",
                        stagedEntity.getCollectionNameAsStaged(), collectionWorkNotes.size());

            } else {
                LOG.info("Creating one WorkNote for collection: {}.", stagedEntity.getCollectionNameAsStaged());

                WorkNote workNote = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, minTime, maxTime, 1);
                workNoteList.add(workNote);
            }
        }
        return workNoteList;
    }

    private List<WorkNote> constructCollectionWorkNotes(List<WorkNote> workNotes, String jobId,
            IngestionStagedEntity stagedEntity, long minTime, long maxTime) {

        String collectionName = stagedEntity.getCollectionNameAsStaged();

        long recordsCountInSegment = getCountOfRecords(collectionName, jobId, minTime, maxTime);

        if (chunkMatch(recordsCountInSegment, MatchEnumeration.good)) {
            // Current chunk is within acceptable threshold, add it to workNotes
            LOG.info("Adding unsplit chunk - it's within acceptable limits");
            WorkNote workNoteUnsplit = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, minTime, maxTime, 0);
            workNotes.add(workNoteUnsplit);

        } else {
            // split time chunk in roughly 2 pieces

            long intervalElapsedTime = maxTime - minTime;
            double split = 0.5;
            boolean done = false;
            long pivot = 0;
            int splitAttempts = 0;

            while (!done) {
                // check right interval
                pivot = minTime + (long) (intervalElapsedTime * split);

                long recordsInRightChunk = getCountOfRecords(collectionName, jobId, pivot, maxTime);
                long recordsInLeftChunk = recordsCountInSegment - recordsInRightChunk;

                LOG.info("Interval = {} / {} ", minTime, maxTime);
                LOG.info("Total ms in interval = {}", intervalElapsedTime);
                LOG.info("Right Interval (Start / End) {} / {} ", pivot, maxTime);
                LOG.info("Splitting with records in left chunk = {}  and records in right chunk = {}, split factor = "
                        + split, recordsInLeftChunk, recordsInRightChunk);

                done = true;

                if (chunkMatch(recordsInRightChunk, MatchEnumeration.good)) {
                    // RIGHT SIDE IS GOOD

                    WorkNote right = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, pivot, maxTime, 0);
                    workNotes.add(right);

                    if (chunkMatch(recordsInLeftChunk, MatchEnumeration.good)
                            || chunkMatch(recordsInLeftChunk, MatchEnumeration.small)) {

                        LOG.info("Adding left + right work notes");

                        WorkNote left = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, minTime, pivot, 0);
                        workNotes.add(left);

                    } else if (chunkMatch(recordsInLeftChunk, MatchEnumeration.large)) {

                        LOG.info("Recursing on left + adding right work note");
                        constructCollectionWorkNotes(workNotes, jobId, stagedEntity, minTime, pivot);
                    }
                } else if (chunkMatch(recordsInRightChunk, MatchEnumeration.small)) {
                    // RIGHT SIDE IS TOO SMALL
                    if (chunkMatch(recordsInLeftChunk, MatchEnumeration.good)) {
                        LOG.info("Adding left + right work notes");
                        WorkNote workNoteLeft = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, minTime, pivot,
                                0);
                        workNotes.add(workNoteLeft);
                        WorkNote workNoteRight = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, pivot,
                                maxTime, 0);
                        workNotes.add(workNoteRight);

                    } else if (chunkMatch(recordsInLeftChunk, MatchEnumeration.small)) {
                        LOG.info("Adding left + right work notes");
                        WorkNote workNoteLeft = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, minTime, pivot,
                                0);
                        workNotes.add(workNoteLeft);
                        WorkNote workNoteRight = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, pivot,
                                maxTime, 0);
                        workNotes.add(workNoteRight);

                    } else if (chunkMatch(recordsInLeftChunk, MatchEnumeration.large)) {
                        LOG.info("Reintervaling - moving interval to the left (left side is too heavy)");
                        split = split - split / 2;
                        done = false;
                    }
                } else {
                    // RIGHT SIDE IS TOO LARGE
                    if (chunkMatch(recordsInLeftChunk, MatchEnumeration.good)) {
                        LOG.info("Adding left work note + recursing on the right");
                        WorkNote workNoteLeft = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, minTime, pivot,
                                0);
                        workNotes.add(workNoteLeft);
                        constructCollectionWorkNotes(workNotes, jobId, stagedEntity, pivot, maxTime);

                    } else if (chunkMatch(recordsInLeftChunk, MatchEnumeration.small)) {
                        LOG.info("Reintervaling - moving interval to the right (right side is too heavy)");
                        split = split + split / 2;
                        done = false;

                    } else if (chunkMatch(recordsInLeftChunk, MatchEnumeration.large)) {
                        LOG.info("Recursing on both left and right");
                        constructCollectionWorkNotes(workNotes, jobId, stagedEntity, minTime, pivot);
                        constructCollectionWorkNotes(workNotes, jobId, stagedEntity, pivot, maxTime);
                    }
                }

                splitAttempts++;

                if (splitAttempts > 20 && done == false) {
                    LOG.info("Split Max reached.");

                    if (chunkMatch(recordsInLeftChunk, MatchEnumeration.large)) {
                        LOG.info("Recursing on left");
                        constructCollectionWorkNotes(workNotes, jobId, stagedEntity, minTime, pivot);
                    } else {
                        LOG.info("Adding left work note");
                        WorkNote workNoteLeft = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, minTime, pivot,
                                0);
                        workNotes.add(workNoteLeft);
                    }

                    if (chunkMatch(recordsInRightChunk, MatchEnumeration.large)) {
                        LOG.info("Recursing on right");
                        constructCollectionWorkNotes(workNotes, jobId, stagedEntity, pivot, maxTime);
                    } else {
                        LOG.info("Adding right work note");
                        WorkNote workNoteRight = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, pivot,
                                maxTime, 0);
                        workNotes.add(workNoteRight);
                    }

                    done = true;
                }
            }
        }

        return workNotes;
    }

    private boolean chunkMatch(long count, MatchEnumeration matchEnum) {
        return matchEnum == checkRecordsInChunk(count);
    }

    private MatchEnumeration checkRecordsInChunk(long count) {
        int splitChunkUpperBound = splitChunkSize * (1 + thresholdPct / 100);
        int splitChunkLowerBound = splitChunkSize * (1 - thresholdPct / 100);

        if (count <= splitChunkUpperBound) {
            if (count >= splitChunkLowerBound) {
                return MatchEnumeration.good;
            }
            return MatchEnumeration.small;
        }
        return MatchEnumeration.large;
    }

    private long getCountOfRecords(String collectionName, String jobId, long min, long max) {
        Criteria limiter = Criteria.where("creationTime").gte(min).lt(max);
        Query query = new Query().addCriteria(limiter);
        return neutralRecordMongoAccess.getRecordRepository().countForJob(collectionName, query, jobId);
    }

    private long getMaxCreationTimeForEntity(IngestionStagedEntity stagedEntity, String jobId) {
        return getCreationTimeForEntity(stagedEntity, jobId, Order.DESCENDING) + 2000;
    }

    private long getMinCreationTimeForEntity(IngestionStagedEntity stagedEntity, String jobId) {
        return getCreationTimeForEntity(stagedEntity, jobId, Order.ASCENDING);
    }

    private long getCreationTimeForEntity(IngestionStagedEntity stagedEntity, String jobId, Order order) {
        Query query = new Query();
        query.sort().on("creationTime", order);
        query.limit(1);
        Iterable<NeutralRecord> nr = neutralRecordMongoAccess.getRecordRepository().findByQueryForJob(
                stagedEntity.getCollectionNameAsStaged(), query, jobId);
        Iterator<NeutralRecord> nrIterator = nr.iterator();

        return nrIterator.next().getCreationTime();
    }

}

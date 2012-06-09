package org.slc.sli.ingestion.routes.orchestra;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.IngestionStagedEntity;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.WorkNoteImpl;
import org.slc.sli.ingestion.dal.NeutralRecordAccess;

/**
 * Implementation of SplitStrategy that uses the "creationTime" of an Entity to partition and create
 * WorkNotes.
 *
 * @author ifaybyshev
 *
 */
@Component
public class TimestampSplitStrategy implements SplitStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(TimestampSplitStrategy.class);

    @Value("${sli.ingestion.split.chunk.size}")
    private int splitChunkSize;

    @Value("${sli.ingestion.split.threshold.percentage}")
    private double thresholdPercentage;

    private NeutralRecordAccess neutralRecordAccess;

    @Override
    public List<WorkNote> splitForEntity(IngestionStagedEntity stagedEntity, String jobId) {
        List<WorkNote> workNotesForEntity = new ArrayList<WorkNote>();

        long numRecords = countRecordsForEntity(stagedEntity, jobId);

        long minTime = neutralRecordAccess.getMinCreationTimeForEntity(stagedEntity, jobId);
        long maxTime = neutralRecordAccess.getMaxCreationTimeForEntity(stagedEntity, jobId);

        if (!stagedEntity.getEdfiEntity().isSelfReferencing() && numRecords > splitChunkSize) {
            LOG.info("Entity split threshold reached. Splitting work for {} collection.",
                    stagedEntity.getCollectionNameAsStaged());

            List<WorkNote> collectionWorkNotes = constructCollectionWorkNotes(new ArrayList<WorkNote>(), jobId,
                    stagedEntity, minTime, maxTime);

            Iterator<WorkNote> workNoteIterator = collectionWorkNotes.iterator();
            while (workNoteIterator.hasNext()) {
                WorkNote wn = workNoteIterator.next();
                wn.setBatchSize(collectionWorkNotes.size());
                workNotesForEntity.add(wn);
            }

            LOG.info("Created {} WorkNotes for collection: {}.", collectionWorkNotes.size(),
                    stagedEntity.getCollectionNameAsStaged());

        } else {
            LOG.info("Creating one WorkNote for collection: {}.", stagedEntity.getCollectionNameAsStaged());

            WorkNote workNote = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, minTime, maxTime, 1);
            workNotesForEntity.add(workNote);
        }
        return workNotesForEntity;
    }

    /**
     * Recursively constructs a list of work notes for the specified job and entity.
     * 
     * @param workNotes current set of work notes for the staged entity.
     * @param jobId current job id.
     * @param stagedEntity entity current in staging database.
     * @param minTime used for performing queries into data store.
     * @param maxTime used for performing queries into data store.
     * @return list of work notes for staged entity.
     */
    private List<WorkNote> constructCollectionWorkNotes(List<WorkNote> workNotes, String jobId,
            IngestionStagedEntity stagedEntity, long minTime, long maxTime) {

        String collectionName = stagedEntity.getCollectionNameAsStaged();

        long recordsCountInSegment = getCountOfRecords(collectionName, minTime, maxTime, jobId);

        if (chunkMatch(recordsCountInSegment, MatchEnumeration.good)) {
            // Current chunk is within acceptable threshold, add it to workNotes
            LOG.info("Adding unsplit chunk - it's within acceptable limits");
            WorkNote workNoteUnsplit = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, minTime, maxTime, 0);
            workNotes.add(workNoteUnsplit);

        } else {

            long intervalElapsedTime = maxTime - minTime;
            double splitFactor = 0.5;
            int splitAttempts = 0;

            boolean done = false;
            while (!done) {
                // check right interval
                long pivot = minTime + (long) (intervalElapsedTime * splitFactor);

                long recordsInRightChunk = getCountOfRecords(collectionName, pivot, maxTime, jobId);
                long recordsInLeftChunk = recordsCountInSegment - recordsInRightChunk;

                LOG.debug("Interval = {} / {} ", minTime, maxTime);
                LOG.debug("Total ms in interval = {}", intervalElapsedTime);
                LOG.debug("Right Interval (Start / End) {} / {} ", pivot, maxTime);
                LOG.debug("Splitting with records in left chunk = {}  and records in right chunk = {}, split factor = "
                        + splitFactor, recordsInLeftChunk, recordsInRightChunk);

                done = true;

                if (chunkMatch(recordsInRightChunk, MatchEnumeration.good)) {
                    // RIGHT SIDE IS GOOD

                    WorkNote right = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, pivot, maxTime, 0);
                    workNotes.add(right);

                    if (chunkMatch(recordsInLeftChunk, MatchEnumeration.good)
                            || chunkMatch(recordsInLeftChunk, MatchEnumeration.small)) {

                        LOG.debug("Adding left + right work notes");

                        WorkNote left = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, minTime, pivot, 0);
                        workNotes.add(left);

                    } else if (chunkMatch(recordsInLeftChunk, MatchEnumeration.large)) {

                        LOG.debug("Recursing on left + adding right work note");
                        constructCollectionWorkNotes(workNotes, jobId, stagedEntity, minTime, pivot);
                    }
                } else if (chunkMatch(recordsInRightChunk, MatchEnumeration.small)) {
                    // RIGHT SIDE IS TOO SMALL
                    if (chunkMatch(recordsInLeftChunk, MatchEnumeration.good)) {
                        LOG.debug("Adding left + right work notes");
                        WorkNote left = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, minTime, pivot, 0);
                        workNotes.add(left);

                        WorkNote right = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, pivot, maxTime, 0);
                        workNotes.add(right);

                    } else if (chunkMatch(recordsInLeftChunk, MatchEnumeration.small)) {
                        LOG.debug("Adding left + right work notes");
                        WorkNote left = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, minTime, pivot, 0);
                        workNotes.add(left);

                        WorkNote right = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, pivot, maxTime, 0);
                        workNotes.add(right);

                    } else if (chunkMatch(recordsInLeftChunk, MatchEnumeration.large)) {
                        LOG.debug("Reintervaling - moving interval to the left (left side is too heavy)");
                        splitFactor = splitFactor / 2;
                        done = false;
                    }
                } else {
                    // RIGHT SIDE IS TOO LARGE
                    if (chunkMatch(recordsInLeftChunk, MatchEnumeration.good)) {
                        LOG.debug("Adding left work note + recursing on the right");
                        WorkNote left = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, minTime, pivot, 0);
                        workNotes.add(left);

                        constructCollectionWorkNotes(workNotes, jobId, stagedEntity, pivot, maxTime);

                    } else if (chunkMatch(recordsInLeftChunk, MatchEnumeration.small)) {
                        LOG.debug("Reintervaling - moving interval to the right (right side is too heavy)");
                        splitFactor = splitFactor + (splitFactor / 2);
                        done = false;

                    } else if (chunkMatch(recordsInLeftChunk, MatchEnumeration.large)) {
                        LOG.debug("Recursing on both left and right");
                        constructCollectionWorkNotes(workNotes, jobId, stagedEntity, minTime, pivot);
                        constructCollectionWorkNotes(workNotes, jobId, stagedEntity, pivot, maxTime);
                    }
                }

                splitAttempts++;

                if (splitAttempts > 20 && !done) {
                    LOG.debug("Split Max reached.");

                    if (chunkMatch(recordsInLeftChunk, MatchEnumeration.large)) {
                        LOG.debug("Recursing on left");
                        constructCollectionWorkNotes(workNotes, jobId, stagedEntity, minTime, pivot);
                    } else {
                        LOG.debug("Adding left work note");
                        WorkNote left = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, minTime, pivot, 0);
                        workNotes.add(left);
                    }

                    if (chunkMatch(recordsInRightChunk, MatchEnumeration.large)) {
                        LOG.debug("Recursing on right");
                        constructCollectionWorkNotes(workNotes, jobId, stagedEntity, pivot, maxTime);
                    } else {
                        LOG.debug("Adding right work note");
                        WorkNote right = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, pivot, maxTime, 0);
                        workNotes.add(right);
                    }

                    done = true;
                }
            }
        }
        return workNotes;
    }

    /**
     * Matches whether the current record count will return the specified enumerated value.
     * 
     * @param count Current record count.
     * @param matchEnum Evaluation of record count translated into enumeration.
     * @return true (if record count returns specified enumeration), false otherwise.
     */
    private boolean chunkMatch(long count, MatchEnumeration matchEnum) {
        return matchEnum == checkRecordsInChunk(count);
    }

    /**
     * Evaluates the size of the chunk (given the number of records).
     * 
     * @param count Number of records in chunk.
     * @return Enumeration that signals whether more work is required in splitting the chunk.
     */
    private MatchEnumeration checkRecordsInChunk(long count) {
        int splitChunkUpperBound = (int) (splitChunkSize * (1 + thresholdPercentage));
        int splitChunkLowerBound = (int) (splitChunkSize * (1 - thresholdPercentage));

        if (count <= splitChunkUpperBound) {
            if (count >= splitChunkLowerBound) {
                return MatchEnumeration.good;
            }
            return MatchEnumeration.small;
        }
        return MatchEnumeration.large;
    }

    private long getCountOfRecords(String collectionName, long min, long max, String jobId) {
        return neutralRecordAccess.countCreationTimeWithinRange(collectionName, min, max, jobId);
    }

    /**
     * Get the count of the number of records in the staging database for the specified entity.
     * 
     * @param stagedEntity entity to perform count() on in staging database.
     * @param jobId current job id.
     * @return long representing the number of records in the staging database.
     */
    private long countRecordsForEntity(IngestionStagedEntity stagedEntity, String jobId) {
        long numRecords = neutralRecordAccess.collectionCountForJob(stagedEntity.getCollectionNameAsStaged(),
                jobId);
        LOG.info("Records for collection {}: {}", stagedEntity.getCollectionNameAsStaged(), numRecords);
        return numRecords;
    }

    /**
     * Private enumeration for evaluating chunk sizes.
     */
    private enum MatchEnumeration {
        good, small, large
    }

    /**
     * Sets neutral record access for entities to be chunked.
     * 
     * @param neutralRecordAccess access into data store for retrieving records.
     */
    public void setNeutralRecordAccess(NeutralRecordAccess neutralRecordAccess) {
        this.neutralRecordAccess = neutralRecordAccess;
    }

    /**
     * Sets the split chunk size for entities when creating work notes.
     * 
     * @param splitChunkSize how many entities each work note should specify.
     */
    public void setSplitChunkSize(int splitChunkSize) {
        this.splitChunkSize = splitChunkSize;
    }

}

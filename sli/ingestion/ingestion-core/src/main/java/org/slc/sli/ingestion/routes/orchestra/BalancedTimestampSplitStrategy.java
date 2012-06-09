package org.slc.sli.ingestion.routes.orchestra;

import java.util.ArrayList;
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
 * WorkNotes. A good pivot point is settled upon before recursively partitioning.
 *
 * @author dduran
 *
 */
@Component
public class BalancedTimestampSplitStrategy implements SplitStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(BalancedTimestampSplitStrategy.class);

    @Value("${sli.ingestion.split.chunk.size}")
    private int splitChunkSize;

    private double thresholdPct = 0.30;

    private NeutralRecordAccess neutralRecordAccess;

    @Override
    public List<WorkNote> splitForEntity(IngestionStagedEntity stagedEntity, String jobId) {
        List<WorkNote> workNotesForEntity;

        long minTime = neutralRecordAccess.getMinCreationTimeForEntity(stagedEntity, jobId);
        long maxTime = neutralRecordAccess.getMaxCreationTimeForEntity(stagedEntity, jobId);

        long numRecords = getCountOfRecords(stagedEntity.getCollectionNameAsStaged(), minTime, maxTime, jobId);

        if (!stagedEntity.getEdfiEntity().isSelfReferencing() && numRecords > splitChunkSize) {
            LOG.info("Creating many WorkNotes for {} collection.", stagedEntity.getCollectionNameAsStaged());

            workNotesForEntity = partitionWorkNotes(minTime, maxTime, stagedEntity, jobId);

            for (WorkNote workNote : workNotesForEntity) {
                workNote.setBatchSize(workNotesForEntity.size());
            }

            LOG.info("Created {} WorkNotes for {}.", workNotesForEntity.size(),
                    stagedEntity.getCollectionNameAsStaged());
        } else {
            LOG.info("Creating one WorkNote for collection: {}.", stagedEntity.getCollectionNameAsStaged());
            workNotesForEntity = singleWorkNoteList(minTime, maxTime, stagedEntity, jobId);
        }
        return workNotesForEntity;
    }

    /**
     * Recursively partition the insertion time ranges for the provided entity. Once within a margin
     * of the requested split chunk size, create a WorkNote. If we are unable to partition further
     * and have still not achieved the desired split chunk size, create a WorkNote anyway.
     *
     * @param min
     * @param max
     * @param stagedEntity
     * @param jobId
     * @return
     */
    private List<WorkNote> partitionWorkNotes(long min, long max, IngestionStagedEntity stagedEntity, String jobId) {
        String collectionName = stagedEntity.getCollectionNameAsStaged();
        long recordsInRange = getCountOfRecords(collectionName, min, max, jobId);

        if (recordsInRange <= splitChunkSize || (max - min <= 1)) {
            LOG.info("Creating WorkNote for {} with time range that contains {} records", stagedEntity, recordsInRange);
            return singleWorkNoteList(min, max, stagedEntity, jobId);
        }

        long pivot = findGoodPivot(min, max, recordsInRange, collectionName, jobId);

        List<WorkNote> leftWorkNotes = partitionWorkNotes(min, pivot, stagedEntity, jobId);
        List<WorkNote> rightWorkNotes = partitionWorkNotes(pivot, max, stagedEntity, jobId);

        leftWorkNotes.addAll(rightWorkNotes);

        return leftWorkNotes;
    }

    /**
     * Try and bisect the provided time range such that the time ranges on either side of the pivot
     * have roughly equal number of records.
     *
     * @param minTime
     * @param maxTime
     * @param recordsInRange
     * @param collectionName
     * @param jobId
     * @return
     */
    private long findGoodPivot(long minTime, long maxTime, long recordsInRange, String collectionName, String jobId) {
        boolean pivotIsGood = false;
        long targetRecordCount = recordsInRange / 2;
        long pivotLower = minTime;
        long pivotUpper = maxTime;

        long pivot = minTime + ((maxTime - minTime) / 2);
        while (!pivotIsGood) {
            long previousPivot = pivot;
            long recordsLeftOfPivot = getCountOfRecords(collectionName, minTime, pivot, jobId);

            if (recordsLeftOfPivot <= targetRecordCount * (1.0 - thresholdPct)) {
                // move pivot right
                pivotLower = pivot;
                pivot = pivot + ((pivotUpper - pivot) / 2);
            } else if (recordsLeftOfPivot >= targetRecordCount * (1.0 + thresholdPct)) {
                // move pivot left
                pivotUpper = pivot;
                pivot = pivot - ((pivot - pivotLower) / 2);
            } else {
                pivotIsGood = true;
            }

            if (!pivotIsGood && previousPivot == pivot) {
                // exit strategy when we can't find a 'truly' good pivot
                pivotIsGood = true;
            }
        }
        return pivot;
    }

    private List<WorkNote> singleWorkNoteList(long min, long max, IngestionStagedEntity entity, String jobId) {
        List<WorkNote> workNoteList = new ArrayList<WorkNote>(1);
        workNoteList.add(WorkNoteImpl.createBatchedWorkNote(jobId, entity, min, max, 1));
        return workNoteList;
    }

    private long getCountOfRecords(String collectionName, long min, long max, String jobId) {
        return neutralRecordAccess.countCreationTimeWithinRange(collectionName, min, max, jobId);
    }

    public void setNeutralRecordAccess(NeutralRecordAccess neutralRecordAccess) {
        this.neutralRecordAccess = neutralRecordAccess;
    }

    public void setSplitChunkSize(int splitChunkSize) {
        this.splitChunkSize = splitChunkSize;
    }

}

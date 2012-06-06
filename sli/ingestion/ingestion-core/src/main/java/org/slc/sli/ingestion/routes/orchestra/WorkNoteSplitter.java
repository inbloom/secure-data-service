package org.slc.sli.ingestion.routes.orchestra;

import java.util.ArrayList;
import java.util.Date;
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

    public enum MatchEnumeration { good, small, large }

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

                Query queryEarliest = new Query();
                queryEarliest.sort().on("creationTime", Order.ASCENDING);
                queryEarliest.limit(1);
                Iterable<NeutralRecord> nrEarliest = neutralRecordMongoAccess.getRecordRepository().findByQueryForJob(stagedEntity.getCollectionNameAsStaged(), queryEarliest, jobId);
                Iterator<NeutralRecord> nrEarliestIterator = nrEarliest.iterator();

                Query queryLatest = new Query();
                queryLatest.sort().on("creationTime", Order.DESCENDING);
                queryLatest.limit(1);
                Iterable<NeutralRecord> nrLatest = neutralRecordMongoAccess.getRecordRepository().findByQueryForJob(stagedEntity.getCollectionNameAsStaged(), queryLatest, jobId);
                Iterator<NeutralRecord> nrLatestIterator = nrLatest.iterator();

                Date startDate =  nrEarliestIterator.next().getCreationTime();
                Date endDate = nrLatestIterator.next().getCreationTime();
                endDate.setTime(endDate.getTime() + 2000);

                List<WorkNote> collectionWorkNotes = constructCollectionWorkNotes(new ArrayList<WorkNote>(), jobId, stagedEntity, startDate, endDate);
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

                Query queryEarliest = new Query();
                queryEarliest.sort().on("creationTime", Order.ASCENDING);
                queryEarliest.limit(1);
                Iterable<NeutralRecord> nrEarliest = neutralRecordMongoAccess.getRecordRepository().findByQueryForJob(stagedEntity.getCollectionNameAsStaged(), queryEarliest, jobId);
                Iterator<NeutralRecord> nrEarliestIterator = nrEarliest.iterator();

                Query queryLatest = new Query();
                queryLatest.sort().on("creationTime", Order.DESCENDING);
                queryLatest.limit(1);
                Iterable<NeutralRecord> nrLatest = neutralRecordMongoAccess.getRecordRepository().findByQueryForJob(stagedEntity.getCollectionNameAsStaged(), queryLatest, jobId);
                Iterator<NeutralRecord> nrLatestIterator = nrLatest.iterator();

                Date startTime = nrEarliestIterator.next().getCreationTime();
                Date endTime = nrLatestIterator.next().getCreationTime();
                endTime.setTime(endTime.getTime() + 2000);

                WorkNote workNote = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, startTime, endTime, 1);
                workNoteList.add(workNote);
            }
        }
        return workNoteList;
    }

    private List<WorkNote> constructCollectionWorkNotes(List<WorkNote> workNotes, String jobId, IngestionStagedEntity stagedEntity, Date startTime, Date endTime) {

        String collectionName = stagedEntity.getCollectionNameAsStaged();
        long recordsCountInSegment = getCountOfRecords(collectionName, jobId, startTime, endTime);
        if ((recordsCountInSegment <= (splitChunkSize * (1 + thresholdPct/100))) && (recordsCountInSegment >= (splitChunkSize * (1 - thresholdPct/100)))) {
            //Current chunk is within acceptable threshold, add it to workNotes
        } else {
            //split time chunk in roughly 2 pieces

            long recordsInRightChunk;
            long millies = (endTime.getTime() - startTime.getTime());
            double split = 0.5;
            boolean done = false;
            Date start = new Date();
            int splitAttempts = 0;

            while (!done) {
                //check right interval
                start.setTime(startTime.getTime() + (long) (millies * split));
                recordsInRightChunk = getCountOfRecords(collectionName, jobId, start, endTime);

                LOG.info("Total ms in interval = " + millies + " in the right side = " + (millies * split));
                LOG.info("Right Interval (Start / End) " + start.toString() + " / " + endTime.toString());
                LOG.info("Splitting with records in left chunk = {}  and records in right chunk = {}, split factor = " + split, recordsInRightChunk, (recordsCountInSegment - recordsInRightChunk));

                done = true;

                /*
                How to process results of records in chunk:
                left        right
                ok          ok              add both to work notes
                ok          smaller         add both to work notes
                ok          higher          add left to work note, recurse on right

                smaller     ok              add both to work notes
                smaller     smaller         add both to work notes
                smaller     higher          move interval to the right

                higher      ok              add right to work note, recurse on left
                higher      smaller         move interval to the left
                higher      higher          recurse on both left and right
                */

                if (checkRecordsInChunk(recordsInRightChunk).equals(MatchEnumeration.good)) {
                    //RIGHT SIDE IS GOOD
                    if (checkRecordsInChunk(recordsCountInSegment - recordsInRightChunk).equals(MatchEnumeration.good) ||
                            checkRecordsInChunk(recordsCountInSegment - recordsInRightChunk).equals(MatchEnumeration.small)) {
                        LOG.info("Adding left + right work notes");
                        WorkNote workNoteLeft = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, startTime, start, 0);
                        workNotes.add(workNoteLeft);
                        WorkNote workNoteRight = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, start, endTime, 0);
                        workNotes.add(workNoteRight);

                    } else if (checkRecordsInChunk(recordsCountInSegment - recordsInRightChunk).equals(MatchEnumeration.large)) {
                        LOG.info("Recursing on left + adding right work note");
                        constructCollectionWorkNotes(workNotes, jobId, stagedEntity, startTime, start);
                        WorkNote workNoteRight = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, start, endTime, 0);
                        workNotes.add(workNoteRight);
                    }
                } else if (checkRecordsInChunk(recordsInRightChunk).equals(MatchEnumeration.small)) {
                    //RIGHT SIDE IS TOO SMALL
                    if (checkRecordsInChunk(recordsCountInSegment - recordsInRightChunk).equals(MatchEnumeration.good)) {
                        LOG.info("Adding left + right work notes");
                        WorkNote workNoteLeft = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, startTime, start, 0);
                        workNotes.add(workNoteLeft);
                        WorkNote workNoteRight = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, start, endTime, 0);
                        workNotes.add(workNoteRight);

                    } else if (checkRecordsInChunk(recordsCountInSegment - recordsInRightChunk).equals(MatchEnumeration.small)) {
                        LOG.info("Adding left + right work notes");
                        WorkNote workNoteLeft = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, startTime, start, 0);
                        workNotes.add(workNoteLeft);
                        WorkNote workNoteRight = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, start, endTime, 0);
                        workNotes.add(workNoteRight);

                    } else if (checkRecordsInChunk(recordsCountInSegment - recordsInRightChunk).equals(MatchEnumeration.large)) {
                        LOG.info("Reintervaling - moving interval to the left (left side is too heavy)");
                        split = split - split / 2;
                        done = false;
                    }
                } else {
                    //RIGHT SIDE IS TOO LARGE
                    if (checkRecordsInChunk(recordsCountInSegment - recordsInRightChunk).equals(MatchEnumeration.good)) {
                        LOG.info("Adding left work note + recursing on the right");
                        WorkNote workNoteLeft = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, startTime, start, 0);
                        workNotes.add(workNoteLeft);
                        constructCollectionWorkNotes(workNotes, jobId, stagedEntity, start, endTime);

                    } else if (checkRecordsInChunk(recordsCountInSegment - recordsInRightChunk).equals(MatchEnumeration.small)) {
                        LOG.info("Reintervaling - moving interval to the right (right side is too heavy)");
                        split = split + split / 2;
                        done = false;

                    } else if (checkRecordsInChunk(recordsCountInSegment - recordsInRightChunk).equals(MatchEnumeration.large)) {
                        LOG.info("Recursing on both left and right");
                        constructCollectionWorkNotes(workNotes, jobId, stagedEntity, startTime, start);
                        constructCollectionWorkNotes(workNotes, jobId, stagedEntity, start, endTime);
                    }
                }
            }

            splitAttempts++;

            if (splitAttempts > 20 && done == false) {
                LOG.info("Split Max reached. Adding left + right work notes");
                WorkNote workNoteLeft = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, startTime, start, 0);
                workNotes.add(workNoteLeft);
                WorkNote workNoteRight = WorkNoteImpl.createBatchedWorkNote(jobId, stagedEntity, start, endTime, 0);
                workNotes.add(workNoteRight);

                done = true;
            }
        }

        return workNotes;
    }


    private MatchEnumeration checkRecordsInChunk(long count) {
        if ((count <= (splitChunkSize * (1 + thresholdPct/100))) && (count >= (splitChunkSize * (1 - thresholdPct/100)))) {
            return MatchEnumeration.good;
        } else if (count <= (splitChunkSize * (1 + thresholdPct/100))) {
            return MatchEnumeration.small;
        }

        return MatchEnumeration.large;
    }


    private long getCountOfRecords(String collectionName, String jobId, Date min, Date max) {
        Query query = new Query();
        Criteria limiter = Criteria.where("creationTime").gte(min).lte(max);
        query.addCriteria(limiter);

        return neutralRecordMongoAccess.getRecordRepository().countForJob(collectionName, query, jobId);
    }

}

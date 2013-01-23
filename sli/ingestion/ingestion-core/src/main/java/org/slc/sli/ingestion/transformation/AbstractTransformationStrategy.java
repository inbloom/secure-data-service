/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.ingestion.transformation;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.RangedWorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.MessageCode;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;

/**
 * Base TransformationStrategy.
 *
 * @author dduran
 * @author shalka
 */
public abstract class AbstractTransformationStrategy implements TransformationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTransformationStrategy.class);

    protected static final String BATCH_JOB_ID_KEY = "batchJobId";
    protected static final String CREATION_TIME = "creationTime";
    protected static final String TYPE_KEY = "type";

    private String batchJobId;
    private Job job;
    private RangedWorkNote workNote;

    @Value("${sli.ingestion.totalRetries}")
    private int numberOfRetries;

    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    @Autowired
    private MongoEntityRepository mongoEntityRepository;

    @Autowired
    protected AbstractMessageReport databaseMessageReport;

    @Override
    public void perform(Job job) {
        this.setJob(job);
        this.setBatchJobId(job.getId());
        this.performTransformation();
    }

    @Override
    public void perform(Job job, RangedWorkNote workNote) {
        this.setJob(job);
        this.setBatchJobId(job.getId());
        this.setWorkNote(workNote);
        this.performTransformation();
    }

    protected abstract void performTransformation();

    /**
     * @return the neutralRecordMongoAccess
     */
    public NeutralRecordMongoAccess getNeutralRecordMongoAccess() {
        return neutralRecordMongoAccess;
    }

    /**
     * @param neutralRecordMongoAccess
     *            the neutralRecordMongoAccess to set
     */
    public void setNeutralRecordMongoAccess(NeutralRecordMongoAccess neutralRecordMongoAccess) {
        this.neutralRecordMongoAccess = neutralRecordMongoAccess;
    }

    public String getBatchJobId() {
        return batchJobId;
    }

    public ReportStats getReportStats(String fileName) {
        ReportStats reportStats = new SimpleReportStats();
        return reportStats;
    }

    public void setBatchJobId(String batchJobId) {
        this.batchJobId = batchJobId;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    /**
     * Gets the Work Note corresponding to this job.
     *
     * @return Work Note containing information about collection and range to operate on.
     */
    public RangedWorkNote getWorkNote() {
        return workNote;
    }

    /**
     * Sets the Work Note for this job.
     *
     * @param workNote
     *            Work Note containing information about collection and range to operate on.
     */
    public void setWorkNote(RangedWorkNote workNote) {
        this.workNote = workNote;
    }

    /**
     * Returns collection entities found in staging ingestion database. If a work note was not
     * provided for
     * the job, then all entities in the collection will be returned.
     *
     * @param collectionName
     *            name of collection to be queried for.
     */
    public Map<Object, NeutralRecord> getCollectionFromDb(String collectionName) {

        Iterable<NeutralRecord> data = getCollectionIterableFromDb(collectionName);

        Map<Object, NeutralRecord> collection = iterableResultsToMap(data);

        if (collection.size() != workNote.getRecordsInRange()) {
            LOG.error("Number of records in creationTime query result ({}) does not match resultsInRange of {} ",
                    collection.size(), workNote);
        }

        return collection;
    }

    public Iterable<NeutralRecord> getCollectionIterableFromDb(String collectionName) {
        RangedWorkNote jobWorkNote = getWorkNote();

        Query query = buildCreationTimeQuery(jobWorkNote);

        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findAllByQuery(
                collectionName, query);

        if (!data.iterator().hasNext()) {
            LOG.warn("Found no records in collection: {} for batch job id: {}", collectionName, getJob().getId());
        }

        return data;
    }

    /**
     * Invokes the 'insert' mongo operation. Use when concurrent writes are known to provide
     * uniqueness (one-to-one mapping between original and _transformed collection).
     *
     * @param record
     *            Neutral Record to be written to data store.
     */
    public void insertRecord(NeutralRecord record) {
        neutralRecordMongoAccess.getRecordRepository().insertWithRetries(record, numberOfRetries);
    }

    /**
     * Invokes the 'insert' mongo operation (for multiple records). Use when concurrent writes are
     * known to provide uniqueness (one-to-one mapping between original and _transformed
     * collection).
     *
     * @param records
     *            Neutral Records to be written to data store.
     * @param collectionName
     *            Collection to write Neutral Records to in data store.
     */
    public void insertRecords(List<NeutralRecord> records, String collectionName) {
        neutralRecordMongoAccess.getRecordRepository().insertAllWithRetries(records, collectionName, numberOfRetries);
        LOG.info("Successfully persisted {} records for collection: {}", records.size(), collectionName);
    }

    /**
     * Invokes the 'upsert' mongo operation. Use when concurrent writes fail to provide uniqueness
     * (for instance, when many record are being condensed into a small subset of records).
     *
     * @param record
     *            Neutral Record to be written to data store.
     */
    public void createRecord(NeutralRecord record) {
        neutralRecordMongoAccess.getRecordRepository().createForJob(record);
    }

    /**
     * Creates a neutral query that will query the data store based on 'creationTime' field.
     *
     * @param note
     *            WorkNote used to determine creation time ranges.
     * @return Neutral Query used to find all records in the data store that were created within the
     *         specified range.
     */
    private Query buildCreationTimeQuery(RangedWorkNote note) {
        Query query = new Query().limit(0);
        query.addCriteria(Criteria.where(BATCH_JOB_ID_KEY).is(note.getBatchJobId()));

        if (note.getBatchSize() == 1) {
            query.addCriteria(Criteria.where(CREATION_TIME).gt(0));
        } else {
            query.addCriteria(Criteria.where(CREATION_TIME).gte(note.getRangeMinimum()).lt(note.getRangeMaximum()));
        }

        return query;
    }

    /**
     * Converts the result of a Query to MongoDB from Iterable<NeutralRecord> to Map<Object,
     * NeutralRecord>, where the 'Object' key is the neutral record's UUID in the data store.
     *
     * @param data
     *            Set of iterable Neutral Records.
     * @return Map of { Neutral Record UUID --> Neutral Record }
     */
    private Map<Object, NeutralRecord> iterableResultsToMap(Iterable<NeutralRecord> data) {
        Map<Object, NeutralRecord> collection = new TreeMap<Object, NeutralRecord>();
        NeutralRecord tempNr = null;

        Iterator<NeutralRecord> neutralRecordIterator = data.iterator();
        while (neutralRecordIterator.hasNext()) {
            tempNr = neutralRecordIterator.next();
            collection.put(tempNr.getRecordId(), tempNr);
        }
        return collection;
    }

    public MongoEntityRepository getMongoEntityRepository() {
        return mongoEntityRepository;
    }

    public void setMongoEntityRepository(MongoEntityRepository mongoEntityRepository) {
        this.mongoEntityRepository = mongoEntityRepository;
    }

    /**
     * This method is for cases where ReportStats is not important to the context to save
     * the code to create a ReportStats all the time, which is true for many transformers.
     *
     * @param fileName
     * @param source
     * @param code
     * @param args
     */
    public void reportError(String fileName, Source source, MessageCode code, Object... args) {
        databaseMessageReport.error(getReportStats(fileName), source, code, args);
    }

    public void reportWarnings(String fileName, Source source, MessageCode code, Object... args) {
        databaseMessageReport.warning(getReportStats(fileName), source, code, args);
    }

}

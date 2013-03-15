/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

package org.slc.sli.ingestion.processors;

import static org.slc.sli.ingestion.util.NeutralRecordUtils.getByPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mongodb.MongoException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.BatchJobStage;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.RangedWorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.dal.NeutralRecordReadConverter;
import org.slc.sli.ingestion.delta.SliDeltaManager;
import org.slc.sli.ingestion.handler.AbstractIngestionHandler;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.RecordHash;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.AggregatedSource;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.ElementSourceImpl;
import org.slc.sli.ingestion.reporting.impl.ProcessorSource;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.transformation.EdFi2SLITransformer;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;

/**
 * Ingestion Persistence Processor.
 *
 * Specific Ingestion Persistence Processor which provides specific SLI Ingestion instance
 * persistence behavior.
 * Persists data from Staged Database.
 *
 * @author ifaybyshev
 * @author dduran
 * @author shalka
 */
@Component
public class PersistenceProcessor implements Processor, BatchJobStage {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceProcessor.class);

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.PERSISTENCE_PROCESSOR;

    private static final String BATCH_JOB_STAGE_DESC = "Persists records to sli database";

    private static final String BATCH_JOB_ID = "batchJobId";
    private static final String CREATION_TIME = "creationTime";

    private EdFi2SLITransformer transformer;

    private Map<String, Set<String>> entityPersistTypeMap;

    private AbstractIngestionHandler<SimpleEntity, Entity> entityPersistHandler;

    @Autowired
    private NeutralRecordReadConverter neutralRecordReadConverter;

    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    @Autowired
    private BatchJobDAO batchJobDAO;

    private Set<String> recordLvlHashNeutralRecordTypes;

    @Autowired
    private AbstractMessageReport databaseMessageReport;

    // Paths for id field and ref fields for self-referencing entities (for DE1950)
    // TODO: make it work for entities with multiple field keys.
    // TODO: make it configurable. From schema, maybe.
    /**
     * represents the configuration of a self-referencing entity schema
     *
     */
    static class SelfRefEntityConfig {
        String idPath;              // path to the id field
        // Exactly one of the following fields can be non-null:
        String parentAttributePath; // if parent reference is stored in attribute, path to
                                            // the parent reference field,
        String localParentIdKey;    // if parent reference is stored in localParentId map, key
                                         // to the parent reference field

        SelfRefEntityConfig(String i, String p, String k) {
            idPath = i;
            parentAttributePath = p;
            localParentIdKey = k;
        }
    }

    public static final Map<String, SelfRefEntityConfig> SELF_REF_ENTITY_CONFIG;
    static {
        HashMap<String, SelfRefEntityConfig> m = new HashMap<String, SelfRefEntityConfig>();
        // localEducationAgency's parent reference is stored in a field an attribute
        m.put("localEducationAgency", new SelfRefEntityConfig("stateOrganizationId", "localEducationAgencyReference",
                null));
        SELF_REF_ENTITY_CONFIG = Collections.unmodifiableMap(m);
    }

    // End Self-referencing entity

    /**
     * Camel Exchange process callback method
     *
     * @param exchange
     *            camel exchange.
     */
    @Override
    public void process(Exchange exchange) {
        RangedWorkNote workNote = exchange.getIn().getBody(RangedWorkNote.class);

        if (workNote == null || workNote.getBatchJobId() == null) {
            handleNoBatchJobIdInExchange(exchange);
        } else {
            processPersistence(workNote, exchange);
        }
    }

    /**
     * Process the persistence of the entity specified by the work note.
     *
     * @param workNote
     *            specifies the entity to be persisted.
     * @param exchange
     *            camel exchange.
     */
    private void processPersistence(RangedWorkNote workNote, Exchange exchange) {
        Stage stage = initializeStage(workNote);

        String batchJobId = workNote.getBatchJobId();
        NewBatchJob newJob = null;
        try {
            newJob = batchJobDAO.findBatchJobById(batchJobId);

            TenantContext.setTenantId(newJob.getTenantId());
            TenantContext.setJobId(batchJobId);

            LOG.debug("processing persistence: {}", newJob);

            processWorkNote(workNote, newJob, stage);

        } catch (Exception exception) {
            handleProcessingExceptions(exception, exchange, batchJobId);
        } finally {
            if (newJob != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, newJob);
                batchJobDAO.saveBatchJobStage(batchJobId, stage);
            }
        }
    }

    /**
     * Initialize the current (persistence) stage.
     *
     * @param workNote
     *            specifies the entity to be persisted.
     * @return current (started) stage.
     */
    private Stage initializeStage(RangedWorkNote workNote) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE, BATCH_JOB_STAGE_DESC);
        stage.setProcessingInformation("stagedEntity="
                + workNote.getIngestionStagedEntity().getCollectionNameAsStaged() + ", rangeMin="
                + workNote.getRangeMinimum() + ", rangeMax=" + workNote.getRangeMaximum() + ", batchSize="
                + workNote.getBatchSize());
        return stage;
    }

    /**
     * Processes the work note by persisting the entity (with range) specified in the work note.
     *
     * @param workNote
     *            specifies the entity (and range) to be persisted.
     * @param job
     *            current batch job.
     * @param stage
     *            persistence stage.
     */
    private void processWorkNote(RangedWorkNote workNote, NewBatchJob job, Stage stage) {
        String collectionNameAsStaged = workNote.getIngestionStagedEntity().getCollectionNameAsStaged();

        EntityPipelineType entityPipelineType = getEntityPipelineType(collectionNameAsStaged);
        String collectionToPersistFrom = getCollectionToPersistFrom(collectionNameAsStaged, entityPipelineType);

        LOG.info("PERSISTING DATA IN COLLECTION: {} (staged as: {})", collectionToPersistFrom, collectionNameAsStaged);

        Map<String, Metrics> perFileMetrics = new HashMap<String, Metrics>();
        ReportStats reportStatsForCollection = createReportStats(job.getId(), collectionNameAsStaged,
                stage.getStageName());
        try {
            ReportStats reportStatsForNrEntity = null;

            Iterable<NeutralRecord> records = null;
            AggregatedSource source = new AggregatedSource(collectionNameAsStaged);
            try {
                records = queryBatchFromDb(collectionToPersistFrom, job.getId(), workNote);
                for (NeutralRecord nr : records) {
                    source.addSource(new ElementSourceImpl(nr));
                }
            } catch (MongoException me) {
                // Add collection name to job resources for later error reporting, if not already
                // there.
                NewBatchJob savedJob = batchJobDAO.findBatchJobById(job.getId());
                if (savedJob.getResourceEntry(collectionNameAsStaged) == null) {
                    ResourceEntry resourceEntry = new ResourceEntry();
                    resourceEntry.setResourceId(collectionNameAsStaged);
                    job.addResourceEntry(resourceEntry);
                    batchJobDAO.saveBatchJob(job);
                }
                databaseMessageReport.error(reportStatsForCollection, source, CoreMessageCode.CORE_0015,
                        collectionNameAsStaged);
                LogUtil.error(LOG, "MongoException when attempting to extract " + collectionNameAsStaged
                        + " NeutralRecords from staging db", me);
                throw (me);
            }

            List<NeutralRecord> recordHashStore = new ArrayList<NeutralRecord>();

            // UN: Added the records to the recordHashStore
            for (NeutralRecord neutralRecord : records) {
                if (reportStatsForNrEntity == null) {
                    reportStatsForNrEntity = createReportStats(job.getId(), neutralRecord.getSourceFile(),
                            stage.getStageName());
                }
                recordHashStore.add(neutralRecord);
            }

            // TODO: make this generic for all self-referencing entities
            if (SELF_REF_ENTITY_CONFIG.containsKey(collectionNameAsStaged)) {

                reportStatsForCollection = persistSelfReferencingEntity(workNote, job, perFileMetrics,
                        stage.getStageName(), reportStatsForCollection, reportStatsForNrEntity, records);

            } else {

                List<NeutralRecord> recordStore = new ArrayList<NeutralRecord>();
                List<SimpleEntity> persist = new ArrayList<SimpleEntity>();
                for (NeutralRecord neutralRecord : records) {
                    reportStatsForCollection = createReportStats(job.getId(), neutralRecord.getSourceFile(),
                            stage.getStageName());
                    Metrics currentMetric = getOrCreateMetric(perFileMetrics, neutralRecord, workNote);

                    if (entityPipelineType.equals(EntityPipelineType.PASSTHROUGH)
                            || entityPipelineType.equals(EntityPipelineType.TRANSFORMED)) {

                        SimpleEntity xformedEntity = transformNeutralRecord(neutralRecord, job,
                                reportStatsForCollection);

                        if (xformedEntity != null) {

                            recordStore.add(neutralRecord);

                            // queue up for bulk insert
                            persist.add(xformedEntity);

                        } else {
                            currentMetric.setErrorCount(currentMetric.getErrorCount() + 1);
                        }
                        currentMetric.setRecordCount(currentMetric.getRecordCount() + 1);
                    }
                    perFileMetrics.put(currentMetric.getResourceId(), currentMetric);
                }

                try {
                    if (persist.size() > 0) {
                        List<Entity> failed = entityPersistHandler.handle(persist, databaseMessageReport,
                                reportStatsForNrEntity);
                        for (Entity entity : failed) {
                            NeutralRecord record = recordStore.get(persist.indexOf(entity));
                            Metrics currentMetric = getOrCreateMetric(perFileMetrics, record, workNote);
                            currentMetric.setErrorCount(currentMetric.getErrorCount() + 1);

                            if (recordHashStore.contains(record)) {
                                recordHashStore.remove(record);
                            }
                        }
                    }
                    for (NeutralRecord neutralRecord2 : recordHashStore) {
                        upsertRecordHash(neutralRecord2);

                    }
                } catch (DataAccessResourceFailureException darfe) {
                    LOG.error("Exception processing record with entityPersistentHandler", darfe);
                }
            }
        } catch (Exception e) {
            databaseMessageReport.error(reportStatsForCollection, new ProcessorSource(collectionNameAsStaged),
                    CoreMessageCode.CORE_0005, collectionNameAsStaged);
            LogUtil.error(LOG, "Exception when attempting to ingest NeutralRecords in: " + collectionNameAsStaged, e);
        } finally {
            Iterator<Metrics> it = perFileMetrics.values().iterator();
            while (it.hasNext()) {
                Metrics m = it.next();
                stage.getMetrics().add(m);
            }
        }
    }

    /*
     * persist self-referencing entities immediately, rather than bulk (and sort in
     * dependency-honoring
     * order) because this
     * entity can have references to entities of the same type.
     * otherwise, id normalization could be attempted while the dependent entity is waiting for
     * insertion in queue.
     */
    // FIXME: remove once deterministic ids are in place.
    private ReportStats persistSelfReferencingEntity(RangedWorkNote workNote, NewBatchJob job, Map<String, Metrics> perFileMetrics,
            String stageName, ReportStats reportStatsForCollection, ReportStats reportStatsForNrEntity,
            Iterable<NeutralRecord> records) {

        List<NeutralRecord> sortedNrList = iterableToList(records);
        String collectionNameAsStaged = workNote.getIngestionStagedEntity().getCollectionNameAsStaged();
        try {
            sortedNrList = sortNrListByDependency(sortedNrList, collectionNameAsStaged);
        } catch (IllegalStateException e) {
            LOG.error("Illegal state encountered during dependency-sort of self-referencing neutral records", e);
        }

        ReportStats returnValue = reportStatsForCollection;
        for (NeutralRecord neutralRecord : sortedNrList) {
            LOG.info("transforming and persisting {}: {}", collectionNameAsStaged,
                    getByPath(SELF_REF_ENTITY_CONFIG.get(collectionNameAsStaged).idPath, neutralRecord.getAttributes()));

            // returnValue = createDbErrorReport(job.getId(), neutralRecord.getSourceFile());
            returnValue = createReportStats(job.getId(), neutralRecord.getSourceFile(), stageName);
            Metrics currentMetric = getOrCreateMetric(perFileMetrics, neutralRecord, workNote);

            SimpleEntity xformedEntity = transformNeutralRecord(neutralRecord, job, returnValue);

            if (xformedEntity != null) {
                try {
                    Entity saved = entityPersistHandler.handle(xformedEntity, databaseMessageReport,
                            reportStatsForNrEntity);
                    if (saved != null) {
                        upsertRecordHash(neutralRecord);
                    }
                } catch (DataAccessResourceFailureException darfe) {
                    LOG.error("Exception processing record with entityPersistentHandler", darfe);
                    currentMetric.setErrorCount(currentMetric.getErrorCount() + 1);
                }

            } else {
                currentMetric.setErrorCount(currentMetric.getErrorCount() + 1);
            }
            currentMetric.setRecordCount(currentMetric.getRecordCount() + 1);
            perFileMetrics.put(currentMetric.getResourceId(), currentMetric);
        }
        return returnValue;
    }

    /**
     * Sort records in dependency-honoring order since they are self-referencing.
     *
     * @param records
     * @param collectionName
     * @return
     */
    // TODO: make this generic for all self-referencing entities
    protected static List<NeutralRecord> sortNrListByDependency(List<NeutralRecord> unsortedRecords,
            String collectionNameAsStaged) throws IllegalStateException {

        List<NeutralRecord> sortedRecords = new ArrayList<NeutralRecord>();

        for (NeutralRecord me : unsortedRecords) {
            insertMyDependenciesAndMe(me, unsortedRecords, sortedRecords, new HashSet<String>(), collectionNameAsStaged);
        }
        return sortedRecords;
    }

    private static List<NeutralRecord> iterableToList(Iterable<NeutralRecord> records) {
        List<NeutralRecord> unsortedRecords = new ArrayList<NeutralRecord>();
        for (NeutralRecord neutralRecord : records) {
            unsortedRecords.add(neutralRecord);
        }
        return unsortedRecords;
    }

    // FIXME: make this algo iterative rather than recursive
    private static void insertMyDependenciesAndMe(NeutralRecord me, List<NeutralRecord> unsortedRecords,
            List<NeutralRecord> sortedRecords, Set<String> idsInStack, String collectionNameAsStaged)
            throws IllegalStateException {
        SelfRefEntityConfig selfRefConfig = SELF_REF_ENTITY_CONFIG.get(collectionNameAsStaged);
        if (me != null && !sortedRecords.contains(me)) {

            String myId = getByPath(selfRefConfig.idPath, me.getAttributes());
            idsInStack.add(myId);

            // look up parent reference
            String parentId = null;
            if (selfRefConfig.parentAttributePath != null) {
                parentId = getByPath(selfRefConfig.parentAttributePath, me.getAttributes());
            } else if (selfRefConfig.localParentIdKey != null) {
                parentId = (String) me.getLocalParentIds().get(selfRefConfig.localParentIdKey);
            }

            // detect cycles
            if (parentId != null && idsInStack.contains(parentId)) {
                LOG.error(
                        "cycle detected in "
                                + collectionNameAsStaged
                                + " reference hierarchy. {} references an entity already a part of this dependency hierarchy {}",
                        myId, idsInStack);
                throw new IllegalStateException("cycle detected in " + collectionNameAsStaged + " reference hierarchy.");
            } else {

                // insert my parent
                NeutralRecord parent = findNeutralRecordByIdAttr(parentId, unsortedRecords, selfRefConfig.idPath);
                insertMyDependenciesAndMe(parent, unsortedRecords, sortedRecords, idsInStack, collectionNameAsStaged);
            }

            // insert me
            sortedRecords.add(me);

            idsInStack.remove(myId);
        }
    }

    private static NeutralRecord findNeutralRecordByIdAttr(String objectiveId, List<NeutralRecord> records,
            String idPath) {
        if (objectiveId != null) {
            for (NeutralRecord sortedNr : records) {
                if (objectiveId.equals(getByPath(idPath, sortedNr.getAttributes()))) {
                    return sortedNr;
                }
            }
        }
        return null;
    }

    private SimpleEntity transformNeutralRecord(NeutralRecord record, NewBatchJob job, ReportStats reportStats) {
        LOG.debug("processing transformable neutral record of type: {}", record.getRecordType());

        String tenantId = job.getTenantId();
        record.setRecordType(record.getRecordType().replaceFirst("_transformed", ""));
        record.setSourceId(tenantId);

        transformer.setBatchJobId(job.getId());
        List<SimpleEntity> transformed = transformer.handle(record, databaseMessageReport, reportStats);

        if (transformed == null || transformed.isEmpty()) {
            databaseMessageReport.error(reportStats, new ElementSourceImpl(record), CoreMessageCode.CORE_0004, record.getRecordType());
            return null;
        }
        transformed.get(0).setSourceFile(record.getSourceFile());
        return transformed.get(0);
    }

    /**
     * Creates metrics for persistence of work note.
     *
     * @param perFileMetrics
     *            current metrics on a per file basis.
     * @param neutralRecord
     *            neutral record to be persisted.
     * @param workNote
     *            work note specifying entities to be persisted.
     * @return
     */
    private static Metrics getOrCreateMetric(Map<String, Metrics> perFileMetrics, NeutralRecord neutralRecord,
            RangedWorkNote workNote) {

        String sourceFile = neutralRecord.getSourceFile();
        if (sourceFile == null) {
            sourceFile = "unknown_" + workNote.getIngestionStagedEntity().getEdfiEntity() + "_file";
        }

        Metrics currentMetric = perFileMetrics.get(sourceFile);
        if (currentMetric == null) {
            // establish new metrics
            currentMetric = Metrics.newInstance(sourceFile);
        }
        return currentMetric;
    }

    /**
     * Creates an error source for the specified batch job id and resource id.
     *
     * @param batchJobId
     *            current batch job.
     * @param resourceId
     *            current resource id.
     * @return database logging error report.
     */
    private static ReportStats createReportStats(String batchJobId, String resourceId, String stageName) {
        return new SimpleReportStats();
    }

    private static String getCollectionToPersistFrom(String collectionNameAsStaged, EntityPipelineType entityPipelineType) {
        String collectionToPersistFrom = collectionNameAsStaged;
        if (entityPipelineType == EntityPipelineType.TRANSFORMED) {
            collectionToPersistFrom = collectionNameAsStaged + "_transformed";
        }
        return collectionToPersistFrom;
    }

    private EntityPipelineType getEntityPipelineType(String collectionName) {
        EntityPipelineType entityPipelineType = EntityPipelineType.NONE;
        if (entityPersistTypeMap.get("passthroughEntities").contains(collectionName)) {
            entityPipelineType = EntityPipelineType.PASSTHROUGH;
        } else if (entityPersistTypeMap.get("transformedEntities").contains(collectionName)) {
            entityPipelineType = EntityPipelineType.TRANSFORMED;
        }
        return entityPipelineType;
    }

    /**
     * Handles the absence of a batch job id in the camel exchange.
     *
     * @param exchange
     *            camel exchange.
     */
    private void handleNoBatchJobIdInExchange(Exchange exchange) {
        LOG.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }

    /**
     * Handles the existence of any processing exceptions in the exchange.
     *
     * @param exception
     *            processing exception in camel exchange.
     * @param exchange
     *            camel exchange.
     * @param batchJobId
     *            current batch job id.
     */
    private void handleProcessingExceptions(Exception exception, Exchange exchange, String batchJobId) {
        LogUtil.error(LOG, "Error persisting batch job " + batchJobId, exception);

        Error error = Error.createIngestionError(batchJobId, null, BATCH_JOB_STAGE.getName(), null, null, null,
                FaultType.TYPE_ERROR.getName(), "Exception", exception.getMessage());
        batchJobDAO.saveError(error);
    }

    public Map<String, Set<String>> getEntityPersistTypeMap() {
        return entityPersistTypeMap;
    }

    public void setEntityPersistTypeMap(Map<String, Set<String>> entityPersistTypeMap) {
        this.entityPersistTypeMap = entityPersistTypeMap;
    }

    public void setTransformer(EdFi2SLITransformer transformer) {
        this.transformer = transformer;
    }

    public void setDefaultEntityPersistHandler(
            AbstractIngestionHandler<SimpleEntity, Entity> defaultEntityPersistHandler) {
        this.entityPersistHandler = defaultEntityPersistHandler;
    }

    public NeutralRecordReadConverter getNeutralRecordReadConverter() {
        return neutralRecordReadConverter;
    }

    public void setNeutralRecordReadConverter(NeutralRecordReadConverter neutralRecordReadConverter) {
        this.neutralRecordReadConverter = neutralRecordReadConverter;
    }

    public void setRecordLvlHashNeutralRecordTypes(Set<String> recordLvlHashNeutralRecordTypes) {
        this.recordLvlHashNeutralRecordTypes = recordLvlHashNeutralRecordTypes;
    }

    public Iterable<NeutralRecord> queryBatchFromDb(String collectionName, String jobId, RangedWorkNote workNote) {
        Criteria batchJob = Criteria.where(BATCH_JOB_ID).is(jobId);
        @SuppressWarnings("boxing")
        Criteria limiter = Criteria.where(CREATION_TIME).gte(workNote.getRangeMinimum()).lt(workNote.getRangeMaximum());

        Query query = new Query().limit(0);
        query.addCriteria(batchJob);
        query.addCriteria(limiter);

        return neutralRecordMongoAccess.getRecordRepository().findAllByQuery(collectionName, query);
    }

    private static enum EntityPipelineType {
        PASSTHROUGH, TRANSFORMED, NONE;
    }

    @SuppressWarnings({ "unchecked" })
    void upsertRecordHash(NeutralRecord nr) throws DataAccessResourceFailureException {

        /*
         * metaData: {
         * ...
         * "rhData": [ {"rhId": <blahId0>, "rhHash": <blahHash0>}, {"rhId": <blahId1>, "rhHash":
         * <blahHash1>}, ... ],
         * "rhTenantId": <tenantId>
         * }
         */
        if (!recordLvlHashNeutralRecordTypes.contains(nr.getRecordType())) {
            return;
        }

        Object rhDataObj = nr.getMetaDataByName(SliDeltaManager.RECORDHASH_DATA);

        // Make sure complete metadata is present
        if (null == rhDataObj) {
            return;
        }

        List<Map<String, Object>> rhData = (List<Map<String, Object>>) rhDataObj;

        for (Map<String, Object> rhDataElement : rhData) {

            String newHashValue = (String) rhDataElement.get(SliDeltaManager.RECORDHASH_HASH);
            String recordId = (String) rhDataElement.get(SliDeltaManager.RECORDHASH_ID);
            Map<String, Object> rhCurrentHash = (Map<String, Object>) rhDataElement
                    .get(SliDeltaManager.RECORDHASH_CURRENT);

            // Make sure complete metadata is present
            if ((null == recordId || null == newHashValue) || recordId.isEmpty() || newHashValue.isEmpty()) {
                continue;
            }

            // Consider DE2002, removing a query per record vs. tracking version
            // RecordHash rh = batchJobDAO.findRecordHash(tenantId, recordId);
            if (rhCurrentHash == null) {
                batchJobDAO.insertRecordHash(recordId, newHashValue);
            } else {
                RecordHash rh = new RecordHash();
                rh.importFromSerializableMap(rhCurrentHash);
                batchJobDAO.updateRecordHash(rh, newHashValue);
            }
        }

    }

    public void setBatchJobDAO(BatchJobDAO batchJobDAO) {
        this.batchJobDAO = batchJobDAO;
    }

    public BatchJobDAO getBatchJobDAO() {
        return this.batchJobDAO;
    }

    @Override
    public String getStageName() {
        return BATCH_JOB_STAGE.getName();
    }

}

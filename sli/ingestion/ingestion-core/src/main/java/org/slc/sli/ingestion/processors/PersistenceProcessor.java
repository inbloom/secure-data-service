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

package org.slc.sli.ingestion.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.dal.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.dal.NeutralRecordReadConverter;
import org.slc.sli.ingestion.handler.AbstractIngestionHandler;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.transformation.EdFi2SLITransformer;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;
import org.slc.sli.ingestion.util.spring.MessageSourceHelper;
import org.slc.sli.ingestion.validation.DatabaseLoggingErrorReport;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.ProxyErrorReport;

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
public class PersistenceProcessor implements Processor, MessageSourceAware {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceProcessor.class);

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.PERSISTENCE_PROCESSOR;
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

    private MessageSource messageSource;

    /**
     * Camel Exchange process callback method
     *
     * @param exchange
     *            camel exchange.
     */
    @Override
    public void process(Exchange exchange) {
        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);

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
    private void processPersistence(WorkNote workNote, Exchange exchange) {
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
    private Stage initializeStage(WorkNote workNote) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE);
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
    private void processWorkNote(WorkNote workNote, Job job, Stage stage) {
        String collectionNameAsStaged = workNote.getIngestionStagedEntity().getCollectionNameAsStaged();

        EntityPipelineType entityPipelineType = getEntityPipelineType(collectionNameAsStaged);
        String collectionToPersistFrom = getCollectionToPersistFrom(collectionNameAsStaged, entityPipelineType);

        LOG.info("PERSISTING DATA IN COLLECTION: {} (staged as: {})", collectionToPersistFrom, collectionNameAsStaged);

        Map<String, Metrics> perFileMetrics = new HashMap<String, Metrics>();
        ErrorReport errorReportForCollection = createDbErrorReport(job.getId(), collectionNameAsStaged);

        try {
            List<NeutralRecord> recordStore = new ArrayList<NeutralRecord>();
            List<SimpleEntity> persist = new ArrayList<SimpleEntity>();
            Iterable<NeutralRecord> records = queryBatchFromDb(collectionToPersistFrom, job.getId(), workNote);

            ErrorReport errorReportForNrEntity = new ProxyErrorReport(errorReportForCollection);

            for (NeutralRecord neutralRecord : records) {
                errorReportForCollection = createDbErrorReport(job.getId(), neutralRecord.getSourceFile());
                Metrics currentMetric = getOrCreateMetric(perFileMetrics, neutralRecord, workNote);

                if (entityPipelineType.equals(EntityPipelineType.PASSTHROUGH)
                        || entityPipelineType.equals(EntityPipelineType.TRANSFORMED)) {

                    SimpleEntity xformedEntity = transformNeutralRecord(neutralRecord, getTenantId(job),
                            errorReportForCollection);

                    if (xformedEntity != null) {

                        if ("learningObjective".equals(xformedEntity.getType())) {
                            // persist immediately because this entity can have references to
                            // entities of the same type. otherwise, id normalization could be
                            // attempted while the dependent entity is waiting for insertion in
                            // queue.
                            // FIXME: remove once deterministic ids are in place.
                            try {
                                entityPersistHandler.handle(xformedEntity, errorReportForNrEntity);
                            } catch (DataAccessResourceFailureException darfe) {
                                LOG.error("Exception processing record with entityPersistentHandler", darfe);
                                currentMetric.setErrorCount(currentMetric.getErrorCount() + 1);
                            }
                        } else {
                            recordStore.add(neutralRecord);

                            // queue up for bulk insert
                            persist.add(xformedEntity);

                        }
                    } else {
                        currentMetric.setErrorCount(currentMetric.getErrorCount() + 1);
                    }
                    currentMetric.setRecordCount(currentMetric.getRecordCount() + 1);
                }
                perFileMetrics.put(currentMetric.getResourceId(), currentMetric);
            }

            try {
                if (persist.size() > 0) {
                    List<Entity> failed = entityPersistHandler.handle(persist, errorReportForNrEntity);
                    for (Entity entity : failed) {
                        NeutralRecord record = recordStore.get(persist.indexOf(entity));
                        Metrics currentMetric = getOrCreateMetric(perFileMetrics, record, workNote);
                        currentMetric.setErrorCount(currentMetric.getErrorCount() + 1);
                    }
                }
            } catch (DataAccessResourceFailureException darfe) {
                LOG.error("Exception processing record with entityPersistentHandler", darfe);
            }
        } catch (Exception e) {
            String fatalErrorMessage = "Fatal problem saving records to database: \n" + "\tEntity\t"
                    + collectionNameAsStaged + "\n";
            errorReportForCollection.fatal(fatalErrorMessage, PersistenceProcessor.class);
            LogUtil.error(LOG, "Exception when attempting to ingest NeutralRecords in: " + collectionNameAsStaged, e);
        } finally {
            Iterator<Metrics> it = perFileMetrics.values().iterator();
            while (it.hasNext()) {
                Metrics m = it.next();
                stage.getMetrics().add(m);
            }
        }
    }

    private SimpleEntity transformNeutralRecord(NeutralRecord record, String tenantId, ErrorReport errorReport) {
        LOG.debug("processing transformable neutral record of type: {}", record.getRecordType());

        record.setRecordType(record.getRecordType().replaceFirst("_transformed", ""));
        record.setSourceId(tenantId);

        List<SimpleEntity> transformed = transformer.handle(record, errorReport);

        if (transformed == null || transformed.isEmpty()) {
            errorReport
                    .error(MessageSourceHelper
                            .getMessage(messageSource, "PERSISTPROC_ERR_MSG4", record.getRecordType()),
                            this);
            return null;
        }
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
    private Metrics getOrCreateMetric(Map<String, Metrics> perFileMetrics, NeutralRecord neutralRecord,
            WorkNote workNote) {

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
     * Creates an error report for the specified batch job id and resource id.
     *
     * @param batchJobId
     *            current batch job.
     * @param resourceId
     *            current resource id.
     * @return database logging error report.
     */
    private DatabaseLoggingErrorReport createDbErrorReport(String batchJobId, String resourceId) {
        DatabaseLoggingErrorReport dbErrorReport = new DatabaseLoggingErrorReport(batchJobId, BATCH_JOB_STAGE,
                resourceId, batchJobDAO);
        return dbErrorReport;
    }

    /**
     * Gets the tenant id of the current batch job.
     *
     * @param job
     *            current batch job.
     * @return tenant id.
     */
    private static String getTenantId(Job job) {
        // TODO this should be determined based on the sourceId
        String tenantId = job.getProperty("tenantId");
        if (tenantId == null) {
            tenantId = "SLI";
        }
        return tenantId;
    }

    private String getCollectionToPersistFrom(String collectionNameAsStaged, EntityPipelineType entityPipelineType) {
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
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
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
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
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

    public Iterable<NeutralRecord> queryBatchFromDb(String collectionName, String jobId, WorkNote workNote) {
        Criteria batchJob = Criteria.where(BATCH_JOB_ID).is(jobId);
        Criteria limiter = Criteria.where(CREATION_TIME).gte(workNote.getRangeMinimum()).lt(workNote.getRangeMaximum());

        Query query = new Query().limit(0);
        query.addCriteria(batchJob);
        query.addCriteria(limiter);

        return neutralRecordMongoAccess.getRecordRepository().findAllByQuery(collectionName, query);
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private static enum EntityPipelineType {
        PASSTHROUGH, TRANSFORMED, NONE;
    }
}

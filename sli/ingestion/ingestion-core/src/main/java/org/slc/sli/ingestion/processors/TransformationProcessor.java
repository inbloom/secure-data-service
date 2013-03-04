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

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.RangedWorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.ProcessorSource;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.transformation.TransformationFactory;
import org.slc.sli.ingestion.transformation.Transmogrifier;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;

/**
 * Camel processor for transformation of data.
 *
 * @author dduran
 *
 */
@Component
public class TransformationProcessor implements Processor {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.TRANSFORMATION_PROCESSOR;

    private static final String BATCH_JOB_STAGE_DESC = "Transforms ed-fi entities to sli data model";

    private static final Logger LOG = LoggerFactory.getLogger(TransformationProcessor.class);

    @Autowired
    private TransformationFactory transformationFactory;

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    @Autowired
    private AbstractMessageReport databaseMessageReport;

    private ReportStats reportStats;

    /**
     * Camel Exchange process callback method
     *
     * @param exchange
     */
    @Override
    public void process(Exchange exchange) {
        RangedWorkNote workNote = exchange.getIn().getBody(RangedWorkNote.class);

        reportStats = new SimpleReportStats();

        if (workNote == null || workNote.getBatchJobId() == null) {
            handleNoBatchJobId(exchange);
        } else {
            processTransformations(workNote, exchange);
        }
    }

    private void processTransformations(RangedWorkNote workNote, Exchange exchange) {

        Stage stage = initializeStage(workNote);
        String batchJobId = workNote.getBatchJobId();
        NewBatchJob newJob = null;
        try {
            newJob = batchJobDAO.findBatchJobById(batchJobId);

            TenantContext.setTenantId(newJob.getTenantId());
            TenantContext.setJobId(batchJobId);

            addMetricsToStage(workNote, stage);

            performDataTransformations(workNote, newJob);

        } catch (Exception e) {
            handleProcessingExceptions(exchange, batchJobId, e);
        } finally {
            if (newJob != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, newJob);
                batchJobDAO.saveBatchJobStage(batchJobId, stage);
            }
        }
    }

    private void addMetricsToStage(RangedWorkNote workNote, Stage stage) {
        Metrics metrics = Metrics.newInstance(workNote.getIngestionStagedEntity().getCollectionNameAsStaged());
        NeutralQuery query = new NeutralQuery(0);
        query.addCriteria(new NeutralCriteria("creationTime", NeutralCriteria.CRITERIA_GTE, workNote.getRangeMinimum(),
                false));
        query.addCriteria(new NeutralCriteria("creationTime", NeutralCriteria.CRITERIA_LT, workNote.getRangeMaximum(),
                false));

        long recordsToProcess = neutralRecordMongoAccess.getRecordRepository().countForJob(
                workNote.getIngestionStagedEntity().getCollectionNameAsStaged(), query);
        metrics.setRecordCount(recordsToProcess);
        stage.getMetrics().add(metrics);
    }

    private Stage initializeStage(RangedWorkNote workNote) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE, BATCH_JOB_STAGE_DESC);
        stage.setProcessingInformation("stagedEntity="
                + workNote.getIngestionStagedEntity().getCollectionNameAsStaged() + ", rangeMin="
                + workNote.getRangeMinimum() + ", rangeMax=" + workNote.getRangeMaximum() + ", batchSize="
                + workNote.getBatchSize());
        return stage;
    }

    /**
     * Invokes transformations strategies
     *
     * @param workNote
     * @param job
     */
    void performDataTransformations(RangedWorkNote workNote, Job job) {
        LOG.info("performing data transformation BatchJob: {}", job.getId());

        Transmogrifier transmogrifier = transformationFactory.createTransmogrifier(workNote, job);

        transmogrifier.executeTransformations();

    }

    private void handleNoBatchJobId(Exchange exchange) {
        LOG.error("No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }

    private void handleProcessingExceptions(Exchange exchange, String batchJobId, Exception exception) {
        LogUtil.error(LOG, "Error processing batch job " + batchJobId, exception);
        if (batchJobId != null) {
            databaseMessageReport.error(reportStats, new ProcessorSource(BATCH_JOB_STAGE.getName()),
                    CoreMessageCode.CORE_0027, exception.getMessage());
        }
    }

    public TransformationFactory getTransformationFactory() {
        return transformationFactory;
    }

    public void setTransformationFactory(TransformationFactory transformationFactory) {
        this.transformationFactory = transformationFactory;
    }

}

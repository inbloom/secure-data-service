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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.ControlFileWorkNote;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordWorkNote;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.delta.SliDeltaManager;
import org.slc.sli.ingestion.landingzone.AttributeType;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.RecordHash;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.ProcessorSource;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;

/**
 *
 * @author npandey
 *
 */
@Component
public class DeltaProcessor implements Processor {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.DELTA_PROCESSOR;

    private static final String BATCH_JOB_STAGE_DESC = "Filter out records that have been detected as duplicates";

    private static final String INGESTION_MESSAGE_TYPE = "IngestionMessageType";

    private static final Logger LOG = LoggerFactory.getLogger(DeltaProcessor.class);

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Autowired
    private AbstractMessageReport databaseMessageReport;

    private Map<String, Long> duplicateCounts = new HashMap<String, Long>();

    private DeterministicUUIDGeneratorStrategy dIdStrategy;
    private DeterministicIdResolver dIdResolver;

    @Autowired
    private Set<String> recordLvlHashNeutralRecordTypes;

    @Override
    public void process(Exchange exchange) throws Exception {


        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE, BATCH_JOB_STAGE_DESC);

        ReportStats reportStats = new SimpleReportStats();

        List<NeutralRecord> filteredRecords = null;

        NewBatchJob currentJob = null;

        String batchJobId = null;

        try {
            NeutralRecordWorkNote workNote = exchange.getIn().getMandatoryBody(NeutralRecordWorkNote.class);
            currentJob = getJob(workNote);

            batchJobId = currentJob.getId();

            Metrics metrics = Metrics.newInstance(batchJobId);
            stage.addMetrics(metrics);

            filteredRecords = filterRecords(workNote, reportStats);

            processMetrics(metrics, duplicateCounts, filteredRecords.size(), reportStats);

            setExchangeHeaders(exchange, reportStats);
            setExchangeBody(exchange, filteredRecords, currentJob, reportStats);

        } catch (InvalidPayloadException e) {
            exchange.getIn().setHeader("hasErrors", true);
            LOG.error("Cannot retrieve a work note to process.");
        } catch (Exception exception) {
            handleProcessingExceptions(exchange, batchJobId, exception);
        } finally {
            if (currentJob != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, currentJob);
                batchJobDAO.saveBatchJob(currentJob);
            }
        }

    }

    private List<NeutralRecord> filterRecords(NeutralRecordWorkNote workNote, ReportStats reportStats) {

        List<NeutralRecord> filteredRecords = new ArrayList<NeutralRecord>();

        for (NeutralRecord neutralRecord : workNote.getNeutralRecords()) {

            if (!recordLvlHashNeutralRecordTypes.contains(neutralRecord.getRecordType())) {
                filteredRecords.add(neutralRecord);
            } else {
                // Handle record hash checking according to various modes.
                String rhMode = TenantContext.getBatchProperty(AttributeType.DUPLICATE_DETECTION.getName());
                boolean modeDisable = (null != rhMode) && rhMode.equalsIgnoreCase(RecordHash.RECORD_HASH_MODE_DISABLE);
                boolean modeDebugDrop = (null != rhMode) && rhMode.equalsIgnoreCase(RecordHash.RECORD_HASH_MODE_DEBUG_DROP);

                if (modeDisable || (!modeDebugDrop && !SliDeltaManager.isPreviouslyIngested(neutralRecord, batchJobDAO, dIdStrategy, dIdResolver, databaseMessageReport, reportStats))) {
                    filteredRecords.add(neutralRecord);
                } else {
                    String type = neutralRecord.getRecordType();
                    Long count = duplicateCounts.containsKey(type) ? duplicateCounts
                            .get(type) : Long.valueOf(0);
                    duplicateCounts.put(type,
                            Long.valueOf(count.longValue() + 1));
                }
            }
        }
        return filteredRecords;
    }

    private void processMetrics(Metrics metrics, Map<String, Long> duplicateCount, int recordCount, ReportStats rs) {
        metrics.setDuplicateCounts(duplicateCount);
        metrics.setRecordCount(recordCount);
        metrics.setErrorCount(rs.getErrorCount());
    }

    public void setDIdGeneratorStrategy(DeterministicUUIDGeneratorStrategy dIdGeneratorStrategy) {
        this.dIdStrategy = dIdGeneratorStrategy;
    }

    public void setDIdResolver(DeterministicIdResolver dIdResolver) {
        this.dIdResolver = dIdResolver;
    }

    public void setRecordLevelDeltaEnabledEntities(Set<String> entities) {
        this.recordLvlHashNeutralRecordTypes = entities;
    }


    private void handleProcessingExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.ERROR.name());
        LogUtil.error(LOG, "Error processing batch job " + batchJobId, exception);

        if (batchJobId != null) {
            ReportStats reportStats = new SimpleReportStats();
            databaseMessageReport.error(reportStats, new ProcessorSource(BATCH_JOB_STAGE.getName()),
                    CoreMessageCode.CORE_0060, batchJobId, exception.getMessage());
        }
    }

    private void setExchangeHeaders(Exchange exchange, ReportStats reportStats) {
        if (reportStats.hasErrors()) {
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        }
    }

    private NewBatchJob getJob(WorkNote work) {
        NewBatchJob job = batchJobDAO.findBatchJobById(work.getBatchJobId());

        String tenantId = job.getTenantId();
        TenantContext.setTenantId(tenantId);
        TenantContext.setJobId(job.getId());
        TenantContext.setBatchProperties(job.getBatchProperties());

        return job;
    }

    private void setExchangeBody(Exchange exchange, List<NeutralRecord> records, NewBatchJob job, ReportStats reportStats) {
        WorkNote workNote = new NeutralRecordWorkNote(records, job.getId(), job.getTenantId(), reportStats.hasErrors());
        exchange.getIn().setBody(workNote, ControlFileWorkNote.class);
}

}

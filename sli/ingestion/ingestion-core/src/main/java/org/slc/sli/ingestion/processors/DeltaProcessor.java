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

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordWorkNote;
import org.slc.sli.ingestion.Resource;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.delta.SliDeltaManager;
import org.slc.sli.ingestion.landingzone.AttributeType;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.RecordHash;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;

/**
 *
 * @author npandey
 *
 */
public class DeltaProcessor extends IngestionProcessor<NeutralRecordWorkNote, Resource> {

    private static final String BATCH_JOB_STAGE_DESC = "Filter out records that have been detected as duplicates";

    private Map<String, Long> duplicateCounts = new HashMap<String, Long>();

    private DeterministicUUIDGeneratorStrategy dIdStrategy;

    private DeterministicIdResolver dIdResolver;

    private Set<String> recordLevelDeltaEnabledEntities;

    @Override
    public void process(Exchange exchange, ProcessorArgs<NeutralRecordWorkNote> args) {

        List<NeutralRecord> filteredRecords = null;
        List<NeutralRecord> records = args.workNote.getNeutralRecords();
        if (records != null) {
            // All neutral records in a batch belong to the same interchange
            String resourceId = records.get(0).getSourceFile();
            Metrics metrics = Metrics.newInstance(resourceId);
            args.stage.addMetrics(metrics);

            filteredRecords = filterRecords(args.workNote, args.reportStats);
            processMetrics(metrics, duplicateCounts, filteredRecords.size(), args.reportStats);
        }

        setExchangeBody(exchange, filteredRecords, args.job, args.reportStats);
    }

    private List<NeutralRecord> filterRecords(NeutralRecordWorkNote workNote, ReportStats reportStats) {

        List<NeutralRecord> filteredRecords = new ArrayList<NeutralRecord>();

        for (NeutralRecord neutralRecord : workNote.getNeutralRecords()) {

            if (!recordLevelDeltaEnabledEntities.contains(neutralRecord.getRecordType())) {
                filteredRecords.add(neutralRecord);
            } else {
                // Handle record hash checking according to various modes.
                String rhMode = TenantContext.getBatchProperty(AttributeType.DUPLICATE_DETECTION.getName());
                boolean modeDisable = (null != rhMode) && rhMode.equalsIgnoreCase(RecordHash.RECORD_HASH_MODE_DISABLE);
                boolean modeDebugDrop = (null != rhMode)
                        && rhMode.equalsIgnoreCase(RecordHash.RECORD_HASH_MODE_DEBUG_DROP);

                if (modeDisable
                        || (!modeDebugDrop && !SliDeltaManager.isPreviouslyIngested(neutralRecord, batchJobDAO,
                                dIdStrategy, dIdResolver, getMessageReport(), reportStats))) {
                    filteredRecords.add(neutralRecord);
                } else {
                    String type = neutralRecord.getRecordType();
                    Long count = duplicateCounts.containsKey(type) ? duplicateCounts.get(type) : Long.valueOf(0);
                    duplicateCounts.put(type, Long.valueOf(count.longValue() + 1));
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

    private void setExchangeBody(Exchange exchange, List<NeutralRecord> records, NewBatchJob job,
            ReportStats reportStats) {
        WorkNote workNote = new NeutralRecordWorkNote(records, job.getId(), reportStats.hasErrors());
        exchange.getIn().setBody(workNote, NeutralRecordWorkNote.class);
    }

    @Override
    protected BatchJobStageType getStage() {
        return BatchJobStageType.DELTA_PROCESSOR;
    }

    @Override
    protected String getStageDescription() {
        return BATCH_JOB_STAGE_DESC;
    }

    public void setdIdStrategy(DeterministicUUIDGeneratorStrategy dIdStrategy) {
        this.dIdStrategy = dIdStrategy;
    }

    public void setdIdResolver(DeterministicIdResolver dIdResolver) {
        this.dIdResolver = dIdResolver;
    }

    public void setRecordLevelDeltaEnabledEntities(Set<String> recordLevelDeltaEnabledEntities) {
        this.recordLevelDeltaEnabledEntities = recordLevelDeltaEnabledEntities;
    }
}

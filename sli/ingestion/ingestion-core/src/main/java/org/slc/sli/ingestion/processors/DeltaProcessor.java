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
import org.slc.sli.ingestion.delta.SliDeltaManager;
import org.slc.sli.ingestion.landingzone.AttributeType;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.RecordHash;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;

/**
 * Processor to filter out neutral records previously ingested
 *
 * @author npandey
 *
 */
public class DeltaProcessor extends IngestionProcessor<NeutralRecordWorkNote, Resource> {

    private static final String BATCH_JOB_STAGE_DESC = "Filter out records that have been detected as duplicates";

    private DeterministicUUIDGeneratorStrategy dIdStrategy;

    private DeterministicIdResolver dIdResolver;

    private Set<String> recordLevelDeltaEnabledEntities;

    /**
     * Camel Exchange process callback method
     *
     * @param exchange Camel exchange.
     */
    @Override
    public void process(Exchange exchange, ProcessorArgs<NeutralRecordWorkNote> args) {

        List<NeutralRecord> filteredRecords = null;
        List<NeutralRecord> records = args.workNote.getNeutralRecords();
        if (records != null && records.size() > 0) {
            Metrics metrics = Metrics.newInstance(records.get(0).getSourceFile());

            args.stage.addMetrics(metrics);

            if (isDeltaEnabled()) {
                filteredRecords = filterRecords(metrics, args.workNote, args.reportStats);
                args.workNote.setNeutralRecords(filteredRecords);

                exchange.getIn().setBody(args.workNote);
            }
        }
    }

    /**
     * Given a list of neutral records filters out records that have been previously ingested.
     *
     * @param metrics
     * @param reportStats reportStats is used to keep track of errors and warnings
     *
     * @return returns list of neutral records that have to be processed further
     */
    private List<NeutralRecord> filterRecords(Metrics metrics, NeutralRecordWorkNote workNote, ReportStats reportStats) {
        Map<String, Long> duplicateCounts = new HashMap<String, Long>();

        List<NeutralRecord> filteredRecords = new ArrayList<NeutralRecord>();

        for (NeutralRecord neutralRecord : workNote.getNeutralRecords()) {
            boolean isDuplicate = false;

            if (isDeltafiable(neutralRecord)) {
                isDuplicate = SliDeltaManager.isPreviouslyIngested(neutralRecord, batchJobDAO, dIdStrategy, dIdResolver, getMessageReport(), reportStats);
            }

            if (isDuplicate) {
                String type = neutralRecord.getRecordType();
                long count = duplicateCounts.containsKey(type) ? duplicateCounts.get(type) : 0L;
                duplicateCounts.put(type, count + 1);
            } else {
                filteredRecords.add(neutralRecord);
            }
        }

        metrics.setDuplicateCounts(duplicateCounts);
        metrics.setRecordCount(filteredRecords.size());
        metrics.setErrorCount(reportStats.getErrorCount());

        return filteredRecords;
    }

    /**
     * Is delta processing supported for this record type.
     *
     * @param neutralRecord Record to check
     * @return True if the record is support
     */
    private boolean isDeltafiable(NeutralRecord neutralRecord) {
        return recordLevelDeltaEnabledEntities.contains(neutralRecord.getRecordType());
    }

    private boolean isDeltaEnabled() {
        String rhMode = TenantContext.getBatchProperty(AttributeType.DUPLICATE_DETECTION.getName());

        if (rhMode == null) {
            return true;
        }

        boolean modeDisable = rhMode.equalsIgnoreCase(RecordHash.RECORD_HASH_MODE_DISABLE);
        boolean modeDebugDrop = rhMode.equalsIgnoreCase(RecordHash.RECORD_HASH_MODE_DEBUG_DROP);

        return !modeDisable && !modeDebugDrop;
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

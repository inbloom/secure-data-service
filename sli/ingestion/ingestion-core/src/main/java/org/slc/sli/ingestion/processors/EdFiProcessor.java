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

import java.io.FileNotFoundException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.ResourceEntryWorkNote;
import org.slc.sli.ingestion.handler.AbstractIngestionHandler;
import org.slc.sli.ingestion.landingzone.AttributeType;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.RecordHash;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.ProcessorSource;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;

/**
 * Camel interface for processing our EdFi batch job.
 * Processes one XML file at a time, streaming directly from zip file into neutral records for
 * staging.
 *
 * @author tshewchuk
 *
 */
@Component
public class EdFiProcessor implements Processor {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.EDFI_PROCESSOR;

    private static final String BATCH_JOB_STAGE_DESC = "Reads records from the interchanges and persists to the staging database";

    private static final String INGESTION_MESSAGE_TYPE = "IngestionMessageType";

    private static final Logger LOG = LoggerFactory.getLogger(EdFiProcessor.class);

    private AbstractIngestionHandler<ResourceEntry, FileProcessStatus> smooksFileHandler;

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Autowired
    private AbstractMessageReport databaseMessageReport;

    @Override
    public void process(Exchange exchange) throws Exception {
        ResourceEntryWorkNote feWorkNote = exchange.getIn().getBody(ResourceEntryWorkNote.class);
        if (feWorkNote == null) {
            handleNoBatchJobIdInExchange(exchange);
        } else {
            processEdFi(exchange, feWorkNote);
        }
    }

    private void processEdFi(Exchange exchange, ResourceEntryWorkNote feWorkNote) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE, BATCH_JOB_STAGE_DESC);

        NewBatchJob newJob = null;
        String batchJobId = feWorkNote.getBatchJobId();
        try {
            newJob = batchJobDAO.findBatchJobById(batchJobId);

            String tenantId = initTenantContext(newJob);

            ResourceEntry re = getResourceEntry(feWorkNote, newJob);

            Metrics metrics = Metrics.newInstance(re.getResourceName());
            stage.addMetrics(metrics);

            potentiallyRemoveRecordHash(tenantId);

            ReportStats rs = new SimpleReportStats();

            // Convert XML file to neutral records, and stage.
            FileProcessStatus fileProcessStatus = smooksFileHandler.handle(re, databaseMessageReport, rs);

            processMetrics(metrics, fileProcessStatus, rs);

            setExchangeHeaders(exchange, rs.hasErrors());
        } catch (Exception exception) {
            handleProcessingExceptions(exchange, batchJobId, exception);
        } finally {
            if (newJob != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, newJob);
                batchJobDAO.saveBatchJob(newJob);
            }
        }
    }

    private ResourceEntry getResourceEntry(ResourceEntryWorkNote reWorkNote, NewBatchJob job)
            throws FileNotFoundException {
        String resourceId = reWorkNote.getResourceId();
        ResourceEntry re = job.getResourceEntry(resourceId);
        return re;
    }

    private void potentiallyRemoveRecordHash(String tenantId) {
        // Check for record hash purge option given
        String rhMode = TenantContext.getBatchProperty(AttributeType.DUPLICATE_DETECTION.getName());
        if ((null != rhMode)
                && (rhMode.equalsIgnoreCase(RecordHash.RECORD_HASH_MODE_DISABLE) || rhMode
                        .equalsIgnoreCase(RecordHash.RECORD_HASH_MODE_RESET))) {
            LOG.info("@duplicate-detection mode '" + rhMode + "' given: resetting recordHash");
            batchJobDAO.removeRecordHashByTenant(tenantId);
        }
    }

    private String initTenantContext(NewBatchJob newJob) {
        String tenantId = newJob.getTenantId();
        TenantContext.setTenantId(tenantId);
        TenantContext.setJobId(newJob.getId());
        TenantContext.setBatchProperties(newJob.getBatchProperties());
        return tenantId;
    }


    private void processMetrics(Metrics metrics, FileProcessStatus fileProcessStatus,
            ReportStats rs) {
        metrics.setDuplicateCounts(fileProcessStatus.getDuplicateCounts());
        metrics.setRecordCount(fileProcessStatus.getTotalRecordCount());
        metrics.setErrorCount(rs.getErrorCount());
    }

    private void handleProcessingExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.ERROR.name());
        LogUtil.error(LOG, "Error processing batch job " + batchJobId, exception);

        if (batchJobId != null) {
            ReportStats reportStats = new SimpleReportStats();
            databaseMessageReport.error(reportStats, new ProcessorSource(BATCH_JOB_STAGE.getName()),
                    CoreMessageCode.CORE_0021, batchJobId,
                    exception.getMessage());
        }
    }

    private void setExchangeHeaders(Exchange exchange, boolean hasError) {
        if (hasError) {
            exchange.getIn().setHeader("hasErrors", hasError);
            exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.ERROR.name());
        } else {
            exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.DATA_TRANSFORMATION.name());
        }
    }

    private void handleNoBatchJobIdInExchange(Exchange exchange) {
        exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.ERROR.name());
        LOG.error("No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }

    public void setSmooksFileHandler(AbstractIngestionHandler<ResourceEntry, FileProcessStatus> smooksFileHandler) {
        this.smooksFileHandler = smooksFileHandler;
    }

}

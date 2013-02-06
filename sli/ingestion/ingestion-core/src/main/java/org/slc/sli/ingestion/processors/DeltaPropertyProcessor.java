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
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.WorkNote;
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
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;

/**
 *
 * @author npandey
 *
 */

@Component
public class DeltaPropertyProcessor implements Processor {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.DELTA_PROPERTY_PROCESSOR;

    private static final String BATCH_JOB_STAGE_DESC = "Process the duplicate detection prooperty";

    private static final String INGESTION_MESSAGE_TYPE = "IngestionMessageType";

    private static final Logger LOG = LoggerFactory.getLogger(DeltaProcessor.class);

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Autowired
    private AbstractMessageReport databaseMessageReport;

    @Override
    public void process(Exchange exchange) throws Exception {


        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE, BATCH_JOB_STAGE_DESC);

        NewBatchJob currentJob = null;
        String batchJobId = null;


        try {
            WorkNote workNote = exchange.getIn().getMandatoryBody(WorkNote.class);
            currentJob = getJob(workNote);
            batchJobId = currentJob.getId();

            Metrics metrics = Metrics.newInstance(batchJobId);
            stage.addMetrics(metrics);

            String tenantId = TenantContext.getTenantId();

            potentiallyRemoveRecordHash(currentJob, tenantId);

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

    private void potentiallyRemoveRecordHash(NewBatchJob job, String tenantId) {
        String rhMode = job.getProperty(AttributeType.DUPLICATE_DETECTION.getName());
        if ((null != rhMode)
            && (rhMode.equalsIgnoreCase(RecordHash.RECORD_HASH_MODE_DISABLE) || rhMode
                    .equalsIgnoreCase(RecordHash.RECORD_HASH_MODE_RESET))) {
            LOG.info("@duplicate-detection mode '" + rhMode + "' given: resetting recordHash");
            batchJobDAO.removeRecordHashByTenant(tenantId);
        }
    }

    private void handleProcessingExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.ERROR.name());
        LogUtil.error(LOG, "Error processing batch job " + batchJobId, exception);

        if (batchJobId != null) {
            ReportStats reportStats = new SimpleReportStats();
            databaseMessageReport.error(reportStats, new ProcessorSource(BATCH_JOB_STAGE.getName()),
                    CoreMessageCode.CORE_0061, exception.getMessage());
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
}

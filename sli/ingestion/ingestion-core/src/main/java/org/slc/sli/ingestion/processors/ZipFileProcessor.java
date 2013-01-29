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

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.handler.ZipFileHandler;
import org.slc.sli.ingestion.landingzone.FileResource;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.reporting.impl.ZipFileSource;
import org.slc.sli.ingestion.util.BatchJobUtils;

/**
 * Zip file handler.
 *
 * @author okrook
 *
 */
@Component
public class ZipFileProcessor implements Processor {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.ZIP_FILE_PROCESSOR;

    private static final String BATCH_JOB_STAGE_DESC = "Validates and unzips the zip file";

    private static final String INGESTION_MESSAGE_TYPE = "IngestionMessageType";

    private static final Logger LOG = LoggerFactory.getLogger(ZipFileProcessor.class);

    @Autowired
    private ZipFileHandler zipFileHandler;

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Autowired
    private AbstractMessageReport databaseMessageReport;

    @Override
    public void process(Exchange exchange) throws Exception {
        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);

        String batchJobId = workNote.getBatchJobId();

        if (batchJobId == null) {
            handleNoBatchJobIdInExchange(exchange);
        } else {
            processZipFile(exchange, batchJobId);
        }
    }

    private void processZipFile(Exchange exchange, String batchJobId) throws Exception {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE, BATCH_JOB_STAGE_DESC);

        NewBatchJob currentJob = null;
        ReportStats reportStats = null;
        File zipFile = null;

        try {
            LOG.info("Received zip file: " + exchange.getIn());

            currentJob = batchJobDAO.findBatchJobById(batchJobId);
            ResourceEntry zipResourceEntry = currentJob.getZipResourceEntry();
            zipFile = new File(zipResourceEntry.getResourceName());


            FileResource zipFileResource = new FileResource(zipFile.getAbsolutePath());

            TenantContext.setTenantId(currentJob.getTenantId());
            TenantContext.setJobId(currentJob.getId());

            reportStats = new SimpleReportStats();

            String ctlFile = zipFileHandler.handle(zipFileResource, databaseMessageReport, reportStats);

            if (ctlFile != null) {
                currentJob.setSourceId(zipFile.getCanonicalPath());
            }

            setExchangeHeaders(exchange, reportStats);
            setExchangeBody(exchange, currentJob, reportStats.hasErrors());

        } catch (Exception exception) {
            handleProcessingException(exchange, batchJobId, zipFile, exception, reportStats);
        } finally {
            if (currentJob != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, currentJob);
                batchJobDAO.saveBatchJob(currentJob);
            }
        }
    }

    private void handleProcessingException(Exchange exchange, String batchJobId, File zipFile,
            Exception exception, ReportStats reportStats) {
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("Error processing batch job " + batchJobId, exception);
        if (batchJobId != null) {
            databaseMessageReport.error(reportStats, new ZipFileSource(zipFile), CoreMessageCode.CORE_0014, exception.toString());
        }
    }

    private void setExchangeHeaders(Exchange exchange, ReportStats reportStats) {
        if (reportStats.hasErrors()) {
            exchange.getIn().setHeader("hasErrors", reportStats.hasErrors());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.BATCH_REQUEST.name());
        }
    }

    private void setExchangeBody(Exchange exchange, NewBatchJob job, boolean hasErrors) {
        WorkNote workNote = new WorkNote(job.getId(), job.getTenantId(), hasErrors);
        exchange.getIn().setBody(workNote, WorkNote.class);
    }

    private void handleNoBatchJobIdInExchange(Exchange exchange) {
        exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.ERROR.name());
        LOG.error("No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }

    public ZipFileHandler getHandler() {
        return zipFileHandler;
    }

    public void setHandler(ZipFileHandler handler) {
        this.zipFileHandler = handler;
    }

}

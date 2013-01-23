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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.dal.NeutralRecordAccess;
import org.slc.sli.ingestion.handler.XmlFileHandler;
import org.slc.sli.ingestion.landingzone.AttributeType;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
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
import org.slc.sli.ingestion.service.IngestionExecutor;
import org.slc.sli.ingestion.smooks.SliSmooksFactory;
import org.slc.sli.ingestion.smooks.SmooksCallable;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;

/**
 * Camel interface for processing our EdFi batch job.
 * Derives the handler to use based on the file format of the files in the batch job and delegates
 * the processing to it.
 *
 * @author dduran
 *
 */
@Component
public class ConcurrentEdFiProcessor implements Processor, ApplicationContextAware {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.EDFI_PROCESSOR;

    private static final String BATCH_JOB_STAGE_DESC = "Reads records from the interchanges and persists to the staging database";

    private static final String INGESTION_MESSAGE_TYPE = "IngestionMessageType";

    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentEdFiProcessor.class);

    private ApplicationContext context;

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Autowired
    private SliSmooksFactory sliSmooksFactory;

    @Autowired
    private NeutralRecordAccess neutralRecordMongoAccess;

    @Autowired
    private AbstractMessageReport databaseMessageReport;

    @Override
    public void process(Exchange exchange) throws Exception {
        String batchJobId = exchange.getIn().getHeader("BatchJobId", String.class);
        if (batchJobId == null) {
            handleNoBatchJobIdInExchange(exchange);
        } else {
            processEdFi(exchange, batchJobId);
        }
    }

    private void processEdFi(Exchange exchange, String batchJobId) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE, BATCH_JOB_STAGE_DESC);

        NewBatchJob currentJob = null;
        try {
            currentJob = batchJobDAO.findBatchJobById(batchJobId);

            String tenantId = currentJob.getTenantId();
            TenantContext.setTenantId(tenantId);
            TenantContext.setJobId(batchJobId);
            TenantContext.setBatchProperties(currentJob.getBatchProperties());

            // Check for record hash purge option given
            String rhMode = TenantContext.getBatchProperty(AttributeType.DUPLICATE_DETECTION.getName());
            if ((null != rhMode)
                    && (rhMode.equalsIgnoreCase(RecordHash.RECORD_HASH_MODE_DISABLE) || rhMode
                            .equalsIgnoreCase(RecordHash.RECORD_HASH_MODE_RESET))) {
                LOG.info("@duplicate-detection mode '" + rhMode + "' given: resetting recordHash");
                batchJobDAO.removeRecordHashByTenant(tenantId);
            }

            indexStagingDB();

            List<FutureTask<Boolean>> smooksFutureTaskList = processJobInFuture(currentJob, stage);
            boolean anyErrorsProcessingFiles = aggregateFutureResults(smooksFutureTaskList);

            setExchangeHeaders(exchange, anyErrorsProcessingFiles);

        } catch (Exception exception) {
            handleProcessingExceptions(exchange, batchJobId, exception);
        } finally {
            if (currentJob != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, currentJob);
                batchJobDAO.saveBatchJob(currentJob);
            }
        }
    }

    private void indexStagingDB() {
        String jobId = TenantContext.getJobId();
        String dbName = BatchJobUtils.jobIdToDbName(jobId);

        LOG.info("Indexing staging db {} for job {}", dbName, jobId);
        neutralRecordMongoAccess.ensureIndexes();
    }

    private List<FutureTask<Boolean>> processJobInFuture(NewBatchJob job, Stage stage) {
        List<IngestionFileEntry> fileEntryList = extractFileEntryList(job);

        List<FutureTask<Boolean>> smooksFutureTaskList = new ArrayList<FutureTask<Boolean>>(fileEntryList.size());

        for (IngestionFileEntry fe : fileEntryList) {
            XmlFileHandler xmlFileHandler = context.getBean("XmlFileHandler", XmlFileHandler.class);

            Callable<Boolean> smooksCallable = new SmooksCallable(job, xmlFileHandler, fe,
                    databaseMessageReport, new SimpleReportStats(), stage, sliSmooksFactory);
            FutureTask<Boolean> smooksFutureTask = IngestionExecutor.execute(smooksCallable);
            smooksFutureTaskList.add(smooksFutureTask);
        }
        return smooksFutureTaskList;
    }

    private boolean aggregateFutureResults(List<FutureTask<Boolean>> smooksFutureTaskList) throws InterruptedException,
            ExecutionException {

        boolean anyErrorsProcessingFiles = false;
        for (FutureTask<Boolean> smooksFutureTask : smooksFutureTaskList) {
            // will block on FutureTask.get until task finishes
            if (smooksFutureTask.get()) {
                anyErrorsProcessingFiles = true;
            }
        }
        return anyErrorsProcessingFiles;
    }

    private List<IngestionFileEntry> extractFileEntryList(NewBatchJob job) {
        List<IngestionFileEntry> fileEntryList = new ArrayList<IngestionFileEntry>();

        List<ResourceEntry> resourceList = job.getResourceEntries();
        for (ResourceEntry resource : resourceList) {
            if (FileFormat.EDFI_XML.getCode().equalsIgnoreCase(resource.getResourceFormat())) {

                FileFormat fileFormat = FileFormat.findByCode(resource.getResourceFormat());
                FileType fileType = FileType.findByNameAndFormat(resource.getResourceType(), fileFormat);
                String fileName = resource.getResourceId();
                String checksum = resource.getChecksum();
                String zipParent = resource.getResourceZipParent();

                IngestionFileEntry fe = new IngestionFileEntry(zipParent, fileFormat, fileType, fileName, checksum);
                fe.setBatchJobId(job.getId());

                fileEntryList.add(fe);
            }
        }
        return fileEntryList;
    }

    private void handleProcessingExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.ERROR.name());
        LogUtil.error(LOG, "Error processing batch job " + batchJobId, exception);

        if (batchJobId != null) {
            ReportStats reportStats = new SimpleReportStats();
            databaseMessageReport.error(reportStats, new ProcessorSource(BATCH_JOB_STAGE.getName()),
                    CoreMessageCode.CORE_0021, batchJobId, exception.getMessage());
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}

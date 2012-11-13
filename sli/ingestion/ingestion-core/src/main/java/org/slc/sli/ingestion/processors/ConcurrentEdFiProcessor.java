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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.dal.NeutralRecordAccess;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
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
public class ConcurrentEdFiProcessor implements Processor {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.EDFI_PROCESSOR;

    private static final String BATCH_JOB_STAGE_DESC = "Reads records from the interchanges and persists to the staging database";

    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentEdFiProcessor.class);

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Autowired
    private SliSmooksFactory sliSmooksFactory;

    @Autowired
    private NeutralRecordAccess neutralRecordAccess;

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

        NewBatchJob newJob = null;
        try {
            newJob = batchJobDAO.findBatchJobById(batchJobId);

            TenantContext.setTenantId(newJob.getTenantId());
            TenantContext.setJobId(batchJobId);

            indexStagingDB();

            List<IngestionFileEntry> fileEntryList = extractFileEntryList(batchJobId, newJob);
            List<FutureTask<Boolean>> smooksFutureTaskList = processFilesInFuture(fileEntryList, newJob, stage);
            boolean anyErrorsProcessingFiles = aggregateFutureResults(smooksFutureTaskList);

            setExchangeHeaders(exchange, anyErrorsProcessingFiles);

        } catch (Exception exception) {
            handleProcessingExceptions(exchange, batchJobId, exception);
        } finally {
            if (newJob != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, newJob);
                batchJobDAO.saveBatchJob(newJob);
            }
        }
    }

    private void indexStagingDB() {
        String jobId = TenantContext.getJobId();
        String dbName = BatchJobUtils.jobIdToDbName(jobId);

        LOG.info("Indexing staging db {} for job {}", dbName, jobId);
        neutralRecordAccess.ensureIndexes();
    }

    private List<FutureTask<Boolean>> processFilesInFuture(List<IngestionFileEntry> fileEntryList, NewBatchJob newJob,
            Stage stage) {

        List<FutureTask<Boolean>> smooksFutureTaskList = new ArrayList<FutureTask<Boolean>>(fileEntryList.size());

        for (IngestionFileEntry fe : fileEntryList) {

            if (fe.getFile().length() > 0) {
                Callable<Boolean> smooksCallable = new SmooksCallable(newJob, fe, stage, batchJobDAO, sliSmooksFactory);
                FutureTask<Boolean> smooksFutureTask = IngestionExecutor.execute(smooksCallable);
                smooksFutureTaskList.add(smooksFutureTask);
            }
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

    private List<IngestionFileEntry> extractFileEntryList(String batchJobId, NewBatchJob newJob) {
        List<IngestionFileEntry> fileEntryList = new ArrayList<IngestionFileEntry>();

        List<ResourceEntry> resourceList = newJob.getResourceEntries();
        for (ResourceEntry resource : resourceList) {
            if (FileFormat.EDFI_XML.getCode().equalsIgnoreCase(resource.getResourceFormat())) {

                FileFormat fileFormat = FileFormat.findByCode(resource.getResourceFormat());
                FileType fileType = FileType.findByNameAndFormat(resource.getResourceType(), fileFormat);
                String fileName = resource.getResourceId();
                String checksum = resource.getChecksum();

                String lzPath = resource.getTopLevelLandingZonePath();

                IngestionFileEntry fe = new IngestionFileEntry(fileFormat, fileType, fileName, checksum, lzPath);
                fe.setFile(new File(resource.getResourceName()));
                fe.setBatchJobId(batchJobId);

                fileEntryList.add(fe);
            }
        }
        return fileEntryList;
    }

    private void handleProcessingExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LogUtil.error(LOG, "Error processing batch job " + batchJobId, exception);
        if (batchJobId != null) {
            Error error = Error.createIngestionError(batchJobId, null, BATCH_JOB_STAGE.getName(), null, null, null,
                    FaultType.TYPE_ERROR.getName(), null, exception.toString());
            batchJobDAO.saveError(error);
        }
    }

    private void setExchangeHeaders(Exchange exchange, boolean hasError) {
        if (hasError) {
            exchange.getIn().setHeader("hasErrors", hasError);
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        } else {
            exchange.getIn().setHeader("IngestionMessageType", MessageType.DATA_TRANSFORMATION.name());
        }
    }

    private void handleNoBatchJobIdInExchange(Exchange exchange) {
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }
}

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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.AbstractReportStats;
import org.slc.sli.ingestion.reporting.SimpleReportStats;
import org.slc.sli.ingestion.service.IngestionExecutor;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;
import org.slc.sli.ingestion.xml.idref.IdRefResolutionCallable;
import org.slc.sli.ingestion.xml.idref.IdRefResolutionHandler;

/**
 * Concurrently processes XML files.
 *
 * @author shalka
 */
@Component
public class ConcurrentXmlFileProcessor implements Processor, ApplicationContextAware {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.XML_FILE_PROCESSOR;

    private static final String BATCH_JOB_STAGE_DESC = "Processes XML files";

    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentXmlFileProcessor.class);

    private ApplicationContext context;

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Autowired
    private AbstractMessageReport databaseMessageReport;

    @Override
    public void process(Exchange exchange) throws Exception {

        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);

        if (workNote == null || workNote.getBatchJobId() == null) {
            missingBatchJobIdError(exchange);
        }

//        if (exchange.getIn().getHeader(AttributeType.NO_ID_REF.name()) != null) {
//            LOG.info("Skipping id ref resolution (specified by @no-id-ref in control file).");
        LOG.info("Skipping id ref resolution.");
/*            skipXmlFile(workNote, exchange);
        } else {
            LOG.info("Entering concurrent id ref resolution.");*/
        processXmlFile(workNote, exchange);
//        }
    }

/*    private void skipXmlFile(WorkNote workNote, Exchange exchange) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE, BATCH_JOB_STAGE_DESC);

        String batchJobId = workNote.getBatchJobId();
        NewBatchJob newJob = batchJobDAO.findBatchJobById(batchJobId);

        boolean hasErrors = false;
        setExchangeHeaders(exchange, hasErrors);

        BatchJobUtils.stopStageAndAddToJob(stage, newJob);
        batchJobDAO.saveBatchJob(newJob);
    }*/

    private void processXmlFile(WorkNote workNote, Exchange exchange) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE, BATCH_JOB_STAGE_DESC);

        String batchJobId = workNote.getBatchJobId();
        NewBatchJob newJob = null;
        try {
            newJob = batchJobDAO.findBatchJobById(batchJobId);

            TenantContext.setTenantId(newJob.getTenantId());
            TenantContext.setJobId(batchJobId);

            List<FutureTask<Boolean>> futureResolutions = resolveFilesInFuture(newJob);
            boolean hasErrors = aggregateFutureResults(futureResolutions);

            setExchangeHeaders(exchange, hasErrors);
        } catch (Exception exception) {
            handleProcessingExceptions(exchange, batchJobId, exception);
        } finally {
            if (newJob != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, newJob);
                batchJobDAO.saveBatchJob(newJob);
            }
        }
    }

    private List<FutureTask<Boolean>> resolveFilesInFuture(NewBatchJob newJob) {
        List<FutureTask<Boolean>> resolutionTaskList = new ArrayList<FutureTask<Boolean>>();

        for (ResourceEntry resource : newJob.getResourceEntries()) {
            // TODO change the Abstract handler to work with ResourceEntry so we can avoid
            // this kludge here and elsewhere
            if (resource.getResourceFormat() != null
                    && resource.getResourceFormat().equalsIgnoreCase(FileFormat.EDFI_XML.getCode())) {
                FileFormat format = FileFormat.findByCode(resource.getResourceFormat());
                FileType type = FileType.findByNameAndFormat(resource.getResourceType(), format);

                IngestionFileEntry fileEntry = new IngestionFileEntry(format, type, resource.getResourceId(),
                        resource.getChecksum());
                fileEntry.setBatchJobId(newJob.getId());
                fileEntry.setFile(new File(resource.getResourceName()));
                fileEntry.setMessageReport(databaseMessageReport);

                AbstractReportStats reportStats = new SimpleReportStats(newJob.getId(), resource.getResourceId(), BATCH_JOB_STAGE.getName());
                fileEntry.setReportStats(reportStats);

                IdRefResolutionHandler idRefResolutionHandler = context.getBean("IdReferenceResolutionHandler",
                        IdRefResolutionHandler.class);

                Callable<Boolean> idRefCallable = new IdRefResolutionCallable(idRefResolutionHandler, fileEntry,
                        newJob, databaseMessageReport);
                FutureTask<Boolean> resolutionTask = IngestionExecutor.execute(idRefCallable);
                resolutionTaskList.add(resolutionTask);
            }
        }

        return resolutionTaskList;
    }

    private boolean aggregateFutureResults(List<FutureTask<Boolean>> resolutionTaskList) throws InterruptedException,
            ExecutionException {
        boolean anyErrorsProcessingFiles = false;
        for (FutureTask<Boolean> resolutionTask : resolutionTaskList) {
            // will block on FutureTask.get until task finishes
            if (resolutionTask.get()) {
                anyErrorsProcessingFiles = true;
            }
        }
        return anyErrorsProcessingFiles;
    }

    private void setExchangeHeaders(Exchange exchange, boolean hasErrors) {
        exchange.getIn().setHeader("hasErrors", hasErrors);
        exchange.getIn().setHeader("IngestionMessageType", MessageType.XML_FILE_PROCESSED.name());
    }

    private void handleProcessingExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LogUtil.error(LOG, "Error processing batch job " + batchJobId, exception);
        Error error = Error.createIngestionError(batchJobId, null, BatchJobStageType.XML_FILE_PROCESSOR.getName(),
                null, null, null, FaultType.TYPE_ERROR.getName(), null, exception.toString());
        batchJobDAO.saveError(error);
    }

    private void missingBatchJobIdError(Exchange exchange) {
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}

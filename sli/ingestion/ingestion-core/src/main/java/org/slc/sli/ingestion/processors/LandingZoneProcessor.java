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

import java.io.File;
import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.BatchJobStatusType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.DirectorySource;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.tenant.TenantDA;
import org.slc.sli.ingestion.util.BatchJobUtils;

/**
 *
 * @author tshewchuk
 *
 */
@Component
public class LandingZoneProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(LandingZoneProcessor.class);
    private static final String ZIP_EXTENSION = ".zip";
    public static final BatchJobStageType LZ_STAGE = BatchJobStageType.LANDING_ZONE_PROCESSOR;
    private static final String LZ_STAGE_DESC = "Validates landing zone and lz file name";

    @Autowired
    private TenantDA tenantDA;

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Autowired
    private AbstractMessageReport databaseMessageReport;

    @Override
    public void process(Exchange exchange) throws Exception {
        Stage stage = Stage.createAndStartStage(LZ_STAGE, LZ_STAGE_DESC);
        String batchJobId = null;
        ReportStats reportStats = new SimpleReportStats();
        NewBatchJob currentJob = null;

        File lzFile = exchange.getIn().getHeader("filePath", File.class);
        boolean hasErrors = false;

        // Verify that the landing zone is valid.
        String lzDirectoryPathName = lzFile.getParent();
        if (!isValidLandingZone(lzDirectoryPathName)) {
            hasErrors = true;
            LOG.error("LandingZoneProcessor: {} is not a valid landing zone.", lzDirectoryPathName);
            reportStats.incError();  // Can't report, but note the error in the exchange header.
        } else {
            currentJob = createNewBatchJob(lzFile);
            createResourceEntryAndAddToJob(lzFile, currentJob);
            batchJobId = currentJob.getId();

            // Verify that the landing zone file is a zip file.
            String lzFileName = lzFile.getName();
            if (!isZipFile(lzFileName)) {
                hasErrors = true;
                handleProcessingError(exchange, batchJobId, lzFileName, lzDirectoryPathName, reportStats);
            }

            BatchJobUtils.stopStageAndAddToJob(stage, currentJob);
            batchJobDAO.saveBatchJob(currentJob);
        }

        setExchangeBody(exchange, reportStats, currentJob, hasErrors);
    }

    /**
     * Determine if the landing zone is valid.
     *
     * @param lzDirectoryPathName
     *            Landing zone directory pathname.
     *
     * @return true if valid lz, false otherwise.
     */
    private boolean isValidLandingZone(String lzDirectoryPathName) {
        return tenantDA.getLzPaths().contains(lzDirectoryPathName);
    }

    /**
     * Determine if the file is a zip file.
     *
     * @param lzFileName
     *            Landing zone filename.
     *
     * @return true if zip file, false otherwise.
     */
    private boolean isZipFile(String lzFileName) {
        return lzFileName.endsWith(ZIP_EXTENSION);
    }

    /**
     * Create a new batch job.
     *
     * @param lzFile
     *            Landing zone file.
     *
     * @return NewBatchJob
     */
    private NewBatchJob createNewBatchJob(File lzFile) {
        String batchJobId = NewBatchJob.createId(lzFile.getName());
        String tenantId = tenantDA.getTenantId(lzFile.getParent());
        TenantContext.setTenantId(tenantId);
        TenantContext.setJobId(batchJobId);

        NewBatchJob newJob = new NewBatchJob(batchJobId, tenantId);
        newJob.setStatus(BatchJobStatusType.RUNNING.getName());
        newJob.setTopLevelSourceId(lzFile.getParentFile().getAbsolutePath());

        LOG.info("Created job [{}]", newJob.getId());

        return newJob;
    }

    private void createResourceEntryAndAddToJob(File lzFile, NewBatchJob newJob) throws IOException {
        ResourceEntry resourceName = new ResourceEntry();
        resourceName.setResourceName(lzFile.getCanonicalPath());
        resourceName.setResourceId(lzFile.getName());
        resourceName.setExternallyUploadedResourceId(lzFile.getName());
        resourceName.setResourceFormat(FileFormat.ZIP_FILE.getCode());
        newJob.getResourceEntries().add(resourceName);
    }

    private void setExchangeBody(Exchange exchange, ReportStats reportStats, NewBatchJob job, boolean hasErrors) {
        WorkNote workNote = null;
        if (job != null) {
            workNote = new WorkNote(job.getId(), hasErrors);
            exchange.getIn().setBody(workNote, WorkNote.class);
        } else {
            workNote = new WorkNote(null, hasErrors);
        }
        exchange.getIn().setBody(workNote, WorkNote.class);
    }

    private void handleProcessingError(Exchange exchange, String batchJobId, String lzFileName, String lzDirectoryPathName, ReportStats reportStats) {
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("LandingZoneProcessor: {} is not a zip file.", lzFileName);
        if (batchJobId != null) {
            databaseMessageReport.error(reportStats, new DirectorySource(lzDirectoryPathName, lzFileName), CoreMessageCode.CORE_0058, lzFileName);
        }
    }

}

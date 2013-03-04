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
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.tenantdb.TenantIdToDbName;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.ControlFileWorkNote;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.RangedWorkNote;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.ZipFileUtil;
import org.slc.sli.ingestion.landingzone.validation.IngestionException;
import org.slc.sli.ingestion.landingzone.validation.SubmissionLevelException;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.ControlFileSource;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.tenant.TenantDA;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;
import org.slc.sli.ingestion.util.MongoCommander;
import org.slc.sli.ingestion.validation.indexes.TenantDBIndexValidator;

/**
 * Transforms body from ControlFile to ControlFileDescriptor type.
 *
 * @author okrook
 *
 */
@Component
public class ControlFilePreProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(ControlFilePreProcessor.class);

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.CONTROL_FILE_PREPROCESSOR;

    private static final String BATCH_JOB_STAGE_DESC = "Parses the control file";

    public static final String INDEX_SCRIPT = "tenantDB_indexes.txt";

    @Resource
    private Set<String> shardCollections;

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Autowired
    private TenantDA tenantDA;

    @Autowired
    private AbstractMessageReport databaseMessageReport;


    @Autowired
    private TenantDBIndexValidator tenantDBIndexValidator;

    @Autowired
    private MongoTemplate mongoTemplate;

    private enum TenantStatus {TENANT_READY, TENANT_NOT_READY, TENANT_SPINUP_FAILED}

    /**
     * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        processUsingNewBatchJob(exchange);
    }

    private void processUsingNewBatchJob(Exchange exchange) throws Exception {

        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE, BATCH_JOB_STAGE_DESC);

        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);
        String batchJobId = workNote.getBatchJobId();


        TenantContext.setJobId(batchJobId);

        // TODO handle invalid control file (user error)
        // TODO handle IOException or other system error
        NewBatchJob currentJob = null;
        ControlFileDescriptor controlFileDescriptor = null;

        ReportStats reportStats = new SimpleReportStats();
        Source source = null;

        try {
            currentJob = batchJobDAO.findBatchJobById(batchJobId);

            ResourceEntry zipResource = currentJob.getZipResourceEntry();

            String controlFileName = ZipFileUtil.getControlFileName(new File(zipResource.getResourceName()));

            source = new ControlFileSource(controlFileName);

            TenantContext.setJobId(currentJob.getId());
            TenantContext.setTenantId(currentJob.getTenantId());

            ControlFile controlFile = parseControlFile(currentJob, controlFileName);

            TenantStatus status = ensureTenantDbIsReady(currentJob.getTenantId());
            if (status == TenantStatus.TENANT_READY) {

                controlFileDescriptor = new ControlFileDescriptor(controlFile, currentJob.getSourceId());

                auditSecurityEvent(currentJob, controlFile);

            } else if(status == TenantStatus.TENANT_NOT_READY){
                databaseMessageReport.error(reportStats, source, CoreMessageCode.CORE_0001);
            } else {
                databaseMessageReport.error(reportStats, source, CoreMessageCode.CORE_0059);
            }

            String tenant = currentJob.getTenantId();
            String tenantDbName = TenantIdToDbName.convertTenantIdToDbName(tenant);
            boolean indicesOK = tenantDBIndexValidator.isValid(mongoTemplate.getDb() , Arrays.asList(tenantDbName), databaseMessageReport, reportStats, source);
            LOG.info( "{} Index Validation for Tenant {} ", indicesOK?"Passed": "Failed", tenant);

            setExchangeHeaders(exchange, reportStats);
            setExchangeBody(exchange, reportStats, controlFile, currentJob);


        } catch (SubmissionLevelException exception) {
            String id = "null";
            if (currentJob != null) {
                id = currentJob.getId();
            }
            handleExceptions(exchange, id, exception, reportStats, source);
        } catch (Exception exception) {
            String id = "null";
            if (currentJob != null) {
                id = currentJob.getId();
            }
            handleExceptions(exchange, id, exception, reportStats, source);
        } finally {
            if (currentJob != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, currentJob);
                batchJobDAO.saveBatchJob(currentJob);
            }
        }
    }

    protected TenantStatus ensureTenantDbIsReady(String tenantId) {

        if (tenantDA.tenantDbIsReady(tenantId)) {

            LOG.info("Tenant db for {} is flagged as 'ready'.", tenantId);
            return TenantStatus.TENANT_READY;

        } else {

            LOG.info("Tenant db for {} is not flagged as 'ready'. Running spin up scripts now.", tenantId);
            boolean onboardingLockIsAcquired = tenantDA.updateAndAquireOnboardingLock(tenantId);
            TenantStatus isNowReady = TenantStatus.TENANT_NOT_READY;

            if (onboardingLockIsAcquired) {

                String result = runDbSpinUpScripts(tenantId);
                if (result != null) {
                    //Unset isReady field so that future run of the spinup script works
                    tenantDA.unsetTenantReadyFlag(tenantId);
                    isNowReady = TenantStatus.TENANT_SPINUP_FAILED;
                    LOG.error("Spinup scripts failed for {}, not setting tenant as ready", tenantId);
                } else {
                    boolean ready = tenantDA.tenantDbIsReady(tenantId);
                    if(ready){
                        isNowReady = TenantStatus.TENANT_READY;
                    }
                    LOG.info("Tenant ready flag for {} now marked: {}", tenantId, ready);
                }
            }

            return isNowReady;

        }
    }

    private String runDbSpinUpScripts(String tenantId) {

        String jsEscapedTenantId = StringEscapeUtils.escapeJavaScript(tenantId);
        String dbName = TenantIdToDbName.convertTenantIdToDbName(jsEscapedTenantId);

        LOG.info("Running tenant indexing script for tenant: {} db: {}", tenantId, dbName);
        String result = MongoCommander.ensureIndexes(INDEX_SCRIPT, dbName, batchJobDAO.getMongoTemplate());
        if (result != null) {
            return result;
        }

        LOG.info("Running tenant presplit script for tenant: {} db: {}", tenantId, dbName);
        result = MongoCommander.preSplit(shardCollections, dbName, batchJobDAO.getMongoTemplate());
        if (result != null) {
            return result;
        }

        tenantDA.setTenantReadyFlag(tenantId);
        return null;
    }

    private void setExchangeBody(Exchange exchange, ReportStats reportStats, ControlFile controlFile, NewBatchJob job) {
            WorkNote workNote = new ControlFileWorkNote(controlFile, job.getId(), reportStats.hasErrors());
            exchange.getIn().setBody(workNote, ControlFileWorkNote.class);
    }

    private ControlFile parseControlFile(NewBatchJob batchJob, String controlFileName) throws IOException,
            IngestionException {

        registerResourceEntry(batchJob, controlFileName);

        ControlFile controlFile = ControlFile.parse(batchJob.getSourceId(), controlFileName, databaseMessageReport);

        batchJob.setTotalFiles(controlFile.getFileEntries().size());

        for(IngestionFileEntry fe : controlFile.getFileEntries()) {
            fe.setBatchJobId(batchJob.getId());
        }

        return controlFile;
    }

    /**
     * Handles errors associated with the control file.
     *
     * @param exchange
     *            Camel exchange.
     * @param batchJobId
     *            String representing current batch job id.
     * @param exception
     *            Exception thrown during control file parsing.
     * @param controlFileName
     */
    private void handleExceptions(Exchange exchange, String batchJobId, Exception exception, ReportStats reportStats,
            Source source) {
        exchange.getIn().setHeader("BatchJobId", batchJobId);
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LogUtil.error(LOG, "Error processing batch job " + batchJobId, exception);
        if (batchJobId != null) {

            databaseMessageReport.error(reportStats, source, CoreMessageCode.CORE_0003, exception.getMessage());

            // TODO: we should be creating WorkNote at the very first point of processing.
            // this will require some routing changes
            RangedWorkNote workNote = RangedWorkNote.createSimpleWorkNote(batchJobId);
            exchange.getIn().setBody(workNote, RangedWorkNote.class);
        }
    }

    private void setExchangeHeaders(Exchange exchange, ReportStats reportStats) {
        if (reportStats.hasErrors()) {
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        } else {
            exchange.getIn().setHeader("IngestionMessageType", MessageType.BATCH_REQUEST.name());
        }
    }

    private void registerResourceEntry(NewBatchJob batchJob, String controlFile) {
        ResourceEntry resourceEntry = new ResourceEntry();
        resourceEntry.setResourceId(controlFile);
        resourceEntry.setExternallyUploadedResourceId(controlFile);
        resourceEntry.setResourceName(controlFile);
        resourceEntry.setResourceZipParent(batchJob.getSourceId());
        resourceEntry.setResourceFormat(FileFormat.CONTROL_FILE.getCode());
        resourceEntry.setTopLevelLandingZonePath(batchJob.getTopLevelSourceId());

        batchJob.getResourceEntries().add(resourceEntry);
    }

    private void auditSecurityEvent(NewBatchJob currentJob, ControlFile controlFile) {
        byte[] ipAddr = null;
        try {
            InetAddress addr = InetAddress.getLocalHost();

            // Get IP Address
            ipAddr = addr.getAddress();

        } catch (UnknownHostException e) {
            LogUtil.error(LOG, "Error getting local host", e);
        }

        String edOrg = tenantDA.getTenantEdOrg(currentJob.getTopLevelSourceId());
        if ( edOrg == null ) {
			edOrg = "";
		}

        List<String> userRoles = Collections.emptyList();
        SecurityEvent event = new SecurityEvent();
        event.setTenantId(controlFile.getConfigProperties().getProperty("tenantId"));
        event.setUser("");
        event.setUserEdOrg(edOrg);
        event.setTargetEdOrg(edOrg);
        event.setTargetEdOrgList(Arrays.asList(edOrg));
        event.setActionUri("processUsingNewBatchJob");
        event.setAppId("Ingestion");
        event.setOrigin("");
        event.setExecutedOn(ipAddr[0] + "." + ipAddr[1] + "." + ipAddr[2] + "." + ipAddr[3]);
        event.setCredential("");
        event.setUserOrigin("");
        event.setTimeStamp(new Date());
        event.setProcessNameOrId(ManagementFactory.getRuntimeMXBean().getName());
        event.setClassName(this.getClass().getName());
        event.setLogLevel(LogLevelType.TYPE_INFO);
        event.setRoles(userRoles);
        event.setLogMessage("Ingestion process started.");

        audit(event);
    }

    public Set<String> getShardCollections() {
        return shardCollections;
    }

    public void setShardCollections(Set<String> shardCollections) {
        this.shardCollections = shardCollections;
    }

    public void setBatchJobDAO(BatchJobDAO batchJobDAO) {
        this.batchJobDAO = batchJobDAO;
    }

    public void setTenantDA(TenantDA tenantDA) {
        this.tenantDA = tenantDA;
    }
}

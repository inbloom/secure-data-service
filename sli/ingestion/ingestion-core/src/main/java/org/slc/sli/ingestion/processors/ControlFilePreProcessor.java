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
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.tenantdb.TenantIdToDbName;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.BatchJobStatusType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.landingzone.validation.IngestionException;
import org.slc.sli.ingestion.landingzone.validation.SubmissionLevelException;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.tenant.TenantDA;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;
import org.slc.sli.ingestion.util.MongoCommander;
import org.slc.sli.ingestion.util.spring.MessageSourceHelper;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Transforms body from ControlFile to ControlFileDescriptor type.
 *
 * @author okrook
 *
 */
@Component
public class ControlFilePreProcessor implements Processor, MessageSourceAware {

    private static final Logger LOG = LoggerFactory.getLogger(ControlFilePreProcessor.class);

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.CONTROL_FILE_PREPROCESSOR;

    private static final String BATCH_JOB_STAGE_DESC = "Parses the control file";

    public static final String INDEX_SCRIPT = "tenantDB_indexes.js";
    public static final String PRE_SPLITTING_SCRIPT = "sli-shard-presplit.js";

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Autowired
    private TenantDA tenantDA;

    @Value("${sli.ingestion.tenant.deriveTenants}")
    private boolean deriveTenantId;

    private MessageSource messageSource;

    /**
     * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        processUsingNewBatchJob(exchange);
    }

    private void processUsingNewBatchJob(Exchange exchange) throws Exception {

        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE, BATCH_JOB_STAGE_DESC);

        String batchJobId = exchange.getIn().getHeader("BatchJobId", String.class);
        String controlFileName = "control_file";

        FaultsReport errorReport = new FaultsReport();

        // TODO handle invalid control file (user error)
        // TODO handle IOException or other system error
        NewBatchJob newBatchJob = null;
        File fileForControlFile = null;
        ControlFileDescriptor controlFileDescriptor = null;

        try {
            fileForControlFile = exchange.getIn().getBody(File.class);
            controlFileName = fileForControlFile.getName();

            newBatchJob = getOrCreateNewBatchJob(batchJobId, fileForControlFile);
            createResourceEntryAndAddToJob(fileForControlFile, newBatchJob);

            ControlFile controlFile = parseControlFile(newBatchJob, fileForControlFile);

            if (newBatchJob.getTenantId() != null) {

                if (ensureTenantDbIsReady(newBatchJob.getTenantId())) {

                    controlFileDescriptor = createControlFileDescriptor(newBatchJob, controlFile);

                    auditSecurityEvent(controlFile);

                } else {
                    LOG.info(MessageSourceHelper.getMessage(messageSource, "SL_ERR_MSG17"));
                    errorReport.error(MessageSourceHelper.getMessage(messageSource, "SL_ERR_MSG17"), this);
                }
            } else {
                LOG.info(MessageSourceHelper.getMessage(messageSource, "SL_ERR_MSG19"));
                errorReport.error(MessageSourceHelper.getMessage(messageSource, "SL_ERR_MSG19"), this);
            }

            setExchangeHeaders(exchange, newBatchJob, errorReport);

            setExchangeBody(exchange, controlFileDescriptor, errorReport, newBatchJob.getId());

        } catch (SubmissionLevelException exception) {
            String id = "null";
            if (newBatchJob != null) {
                id = newBatchJob.getId();
                if (newBatchJob.getResourceEntries().size() == 0) {
                    LOG.info(MessageSourceHelper.getMessage(messageSource, "CTLFILEPROC_WRNG_MSG1"));
                    errorReport.warning(MessageSourceHelper.getMessage(messageSource, "CTLFILEPROC_WRNG_MSG1"), this);
                }
            }
            handleExceptions(exchange, id, exception, controlFileName);
        } catch (Exception exception) {
            String id = "null";
            if (newBatchJob != null) {
                id = newBatchJob.getId();
            }
            handleExceptions(exchange, id, exception, controlFileName);
        } finally {
            if (newBatchJob != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, newBatchJob);
                batchJobDAO.saveBatchJob(newBatchJob);
                BatchJobUtils.writeErrorsWithDAO(newBatchJob.getId(), fileForControlFile.getName(), BATCH_JOB_STAGE,
                        errorReport, batchJobDAO);
            }
        }
    }

    protected boolean ensureTenantDbIsReady(String tenantId) {

        if (tenantDA.tenantDbIsReady(tenantId)) {

            LOG.info("Tenant db for {} is flagged as 'ready'.", tenantId);
            return true;

        } else {

            LOG.info("Tenant db for {} is not flagged as 'ready'. Running spin up scripts now.", tenantId);
            boolean onboardingLockIsAcquired = tenantDA.updateAndAquireOnboardingLock(tenantId);
            boolean isNowReady = false;

            if (onboardingLockIsAcquired) {

                runDbSpinUpScripts(tenantId);

                isNowReady = tenantDA.tenantDbIsReady(tenantId);
                LOG.info("Tenant ready flag for {} now marked: {}", tenantId, isNowReady);
            }

            return isNowReady;

        }
    }

    private void runDbSpinUpScripts(String tenantId) {

        String jsEscapedTenantId = StringEscapeUtils.escapeJavaScript(tenantId);
        String dbName = TenantIdToDbName.convertTenantIdToDbName(jsEscapedTenantId);

        LOG.info("Running tenant indexing script for tenant: {} db: {}", tenantId, dbName);
        MongoCommander.exec(dbName, INDEX_SCRIPT, " ");

        LOG.info("Running tenant presplit script for tenant: {} db: {}", tenantId, dbName);
        MongoCommander.exec("admin", PRE_SPLITTING_SCRIPT, "tenant=\'" + dbName + "\';");

        tenantDA.setTenantReadyFlag(tenantId);
    }

    private void setExchangeBody(Exchange exchange, ControlFileDescriptor controlFileDescriptor,
            ErrorReport errorReport, String batchJobId) {
        if (!errorReport.hasErrors() && controlFileDescriptor != null) {
            exchange.getIn().setBody(controlFileDescriptor, ControlFileDescriptor.class);
        } else {
            WorkNote workNote = WorkNote.createSimpleWorkNote(batchJobId);
            exchange.getIn().setBody(workNote, WorkNote.class);
        }
    }

    private NewBatchJob getOrCreateNewBatchJob(String batchJobId, File cf) {
        NewBatchJob job = null;
        if (batchJobId != null) {
            job = batchJobDAO.findBatchJobById(batchJobId);
        } else {
            job = createNewBatchJob(cf);
        }

        TenantContext.setJobId(job.getId());

        return job;
    }

    private ControlFile parseControlFile(NewBatchJob newBatchJob, File fileForControlFile) throws IOException,
            IngestionException {
        File lzFile = new File(newBatchJob.getTopLevelSourceId());
        LandingZone topLevelLandingZone = new LocalFileSystemLandingZone(lzFile);

        ControlFile controlFile = ControlFile.parse(fileForControlFile, topLevelLandingZone, messageSource);

        newBatchJob.setTotalFiles(controlFile.getFileEntries().size());

        // determine whether to override the tenantId property with a LZ derived value
        if (deriveTenantId) {
            // derive the tenantId property from the landing zone directory with a mongo lookup
            String tenantId = setTenantIdFromDb(controlFile, lzFile.getAbsolutePath());
            newBatchJob.setTenantId(tenantId);
        }

        TenantContext.setTenantId(newBatchJob.getTenantId());

        return controlFile;
    }

    private ControlFileDescriptor createControlFileDescriptor(NewBatchJob newBatchJob, ControlFile controlFile) {
        File sourceFile = new File(newBatchJob.getSourceId());
        LandingZone resolvedLandingZone = new LocalFileSystemLandingZone(sourceFile);
        ControlFileDescriptor controlFileDescriptor = new ControlFileDescriptor(controlFile, resolvedLandingZone);
        return controlFileDescriptor;
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
    private void handleExceptions(Exchange exchange, String batchJobId, Exception exception, String controlFileName) {
        exchange.getIn().setHeader("BatchJobId", batchJobId);
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LogUtil.error(LOG, "Error processing batch job " + batchJobId, exception);
        if (batchJobId != null) {
            Error error = Error.createIngestionError(batchJobId, controlFileName, BATCH_JOB_STAGE.getName(), null,
                    null, null, FaultType.TYPE_ERROR.getName(), null, exception.getMessage());
            batchJobDAO.saveError(error);

            // TODO: we should be creating WorkNote at the very first point of processing.
            // this will require some routing changes
            WorkNote workNote = WorkNote.createSimpleWorkNote(batchJobId);
            exchange.getIn().setBody(workNote, WorkNote.class);
        }
    }

    private void setExchangeHeaders(Exchange exchange, NewBatchJob newJob, ErrorReport errorReport) {
        exchange.getIn().setHeader("BatchJobId", newJob.getId());
        if (errorReport.hasErrors()) {
            exchange.getIn().setHeader("hasErrors", errorReport.hasErrors());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        } else {
            exchange.getIn().setHeader("IngestionMessageType", MessageType.BATCH_REQUEST.name());
        }
    }

    private NewBatchJob createNewBatchJob(File controlFile) {
        NewBatchJob newJob = NewBatchJob.createJobForFile(controlFile.getName());
        newJob.setSourceId(controlFile.getParentFile().getAbsolutePath() + File.separator);
        newJob.setStatus(BatchJobStatusType.RUNNING.getName());
        LOG.info("Created job [{}]", newJob.getId());
        return newJob;
    }

    private void createResourceEntryAndAddToJob(File cf, NewBatchJob newJob) {
        ResourceEntry resourceEntry = new ResourceEntry();
        resourceEntry.setResourceId(cf.getName());
        resourceEntry.setExternallyUploadedResourceId(cf.getName());
        resourceEntry.setResourceName(newJob.getSourceId() + cf.getName());
        resourceEntry.setResourceFormat(FileFormat.CONTROL_FILE.getCode());
        resourceEntry.setTopLevelLandingZonePath(newJob.getTopLevelSourceId());
        newJob.getResourceEntries().add(resourceEntry);
    }

    /**
     * Derive the tenantId using a database look up based on the LZ path
     * and override the property on the ControlFile with he derived value.
     *
     * Throws an IngestionException if a tenantId could not be resolved.
     */
    private String setTenantIdFromDb(ControlFile cf, String lzPath) throws IngestionException {
        lzPath = new File(lzPath).getAbsolutePath();
        // TODO add user facing error report for no tenantId found
        String tenantId = tenantDA.getTenantId(lzPath);
        if (tenantId != null) {
            cf.getConfigProperties().put("tenantId", tenantId);
        } else {
            throw new IngestionException("Could not find tenantId for landing zone: " + lzPath);
        }
        return tenantId;
    }

    private void auditSecurityEvent(ControlFile controlFile) {
        byte[] ipAddr = null;
        try {
            InetAddress addr = InetAddress.getLocalHost();

            // Get IP Address
            ipAddr = addr.getAddress();

        } catch (UnknownHostException e) {
            LogUtil.error(LOG, "Error getting local host", e);
        }
        List<String> userRoles = Collections.emptyList();
        SecurityEvent event = new SecurityEvent();
        event.setTenantId(controlFile.getConfigProperties().getProperty("tenantId"));
        event.setUser("");
        event.setTargetEdOrg("");
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

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}

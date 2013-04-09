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

import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.email.SendEmail;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.aspect.MongoTrackingAspect;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.ControlFileWorkNote;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.landingzone.AttributeType;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.validation.ControlFileValidator;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.RecordHash;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.ControlFileSource;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;

/**
 * Control file processor.
 *
 * @author okrook
 *
 */
@Component
public class ControlFileProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(ControlFileProcessor.class);

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.CONTROL_FILE_PROCESSOR;

    private static final String BATCH_JOB_STAGE_DESC = "Validates the control file";

    private static final String INGESTION_MESSAGE_TYPE = "IngestionMessageType";

    @Autowired
    private ControlFileValidator validator;

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Autowired
    private AbstractMessageReport databaseMessageReport;

    @Value("${sli.ingestion.notify.fromEmailAddress}")
    private String fromEmailAddress;

    @Override
    public void process(Exchange exchange) throws Exception {

        processUsingNewBatchJob(exchange);

        MongoTrackingAspect.aspectOf().reset();
    }

    private void processUsingNewBatchJob(Exchange exchange) throws Exception {

        ControlFileWorkNote workNote = (ControlFileWorkNote) exchange.getIn().getBody(WorkNote.class);
        String batchJobId = workNote.getBatchJobId();
        ControlFile controlFile = workNote.getControlFile();

        if (batchJobId == null) {

            handleNoBatchJobIdInExchange(exchange);
        } else {

            processControlFile(exchange, batchJobId, controlFile);
        }
    }

    private void handleNoBatchJobIdInExchange(Exchange exchange) {
        exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.ERROR.name());
        LOG.error("No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }

    private void processControlFile(Exchange exchange, String batchJobId, ControlFile controlFile) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE, BATCH_JOB_STAGE_DESC);

        NewBatchJob currentJob = null;
        try {

            currentJob = batchJobDAO.findBatchJobById(batchJobId);
            TenantContext.setTenantId(currentJob.getTenantId());
            TenantContext.setJobId(batchJobId);

            ControlFileDescriptor cfd = exchange.getIn().getBody(ControlFileDescriptor.class);

            ControlFile cf = cfd.getFileItem();
            cf.setBatchJobId(batchJobId);
            aggregateBatchJobProperties(newJob, cf);

            ReportStats reportStats = new SimpleReportStats();

            boolean goodData = false;
            if ((currentJob.getProperty(AttributeType.PURGE.getName()) == null)
                    && (currentJob.getProperty(AttributeType.PURGE_KEEP_EDORGS.getName()) == null)) {
                if (validator.isValid(controlFile, databaseMessageReport, reportStats, new ControlFileSource(controlFile.getFileName()))) {
                    createAndAddResourceEntries(newJob, cf);
                    goodData = true;
                } else {
                    boolean isZipFile = false;
                    for (ResourceEntry resourceEntry : currentJob.getResourceEntries()) {
                        if (FileFormat.ZIP_FILE.getCode().equalsIgnoreCase(resourceEntry.getResourceFormat())) {
                            isZipFile = true;
                        }
                    }
                    if (!isZipFile) {
                        databaseMessageReport.warning(reportStats, new ControlFileSource(controlFile.getFileName()), CoreMessageCode.CORE_0051);

                    }
                }
            }

            setExchangeHeaders(exchange, currentJob, reportStats);

            // Notify about job start
            notifyStart(exchange, cf, goodData);

            setExchangeBody( exchange, currentJob, reportStats.hasErrors());
        } catch (Exception exception) {
            handleExceptions(exchange, batchJobId, exception);
        } finally {
            if (currentJob != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, currentJob);
                batchJobDAO.saveBatchJob(currentJob);
            }
        }
    }

    private void handleExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.ERROR.name());
        LogUtil.error(LOG, "Error processing batch job " + batchJobId, exception);
        if (batchJobId != null) {
            Error error = Error.createIngestionError(batchJobId, null, BATCH_JOB_STAGE.getName(), null, null, null,
                    FaultType.TYPE_ERROR.getName(), null, exception.toString());
            batchJobDAO.saveError(error);
        }
    }

    private void setExchangeHeaders(Exchange exchange, NewBatchJob newJob, ReportStats reportStats) {
        if (reportStats.hasErrors()) {
            exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.ERROR.name());
        } else if ((newJob.getProperty(AttributeType.PURGE.getName()) != null)
                || (newJob.getProperty(AttributeType.PURGE_KEEP_EDORGS.getName()) != null)) {
            exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.PURGE.name());
        } else {
            exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.CONTROL_FILE_PROCESSED.name());
        }

        if (newJob.getProperty(AttributeType.DRYRUN.getName()) != null) {
            LOG.debug("Matched @dry-run tag from control file parsing.");
        } else {
            LOG.debug("Did not match @dry-run tag in control file.");
        }

        String ddProp = newJob.getProperty(AttributeType.DUPLICATE_DETECTION.getName());
        if (ddProp != null) {
            LOG.debug("Matched @duplicate-detection tag from control file parsing.");
            // Make sure it is one of the known values
            String[] allowed = { RecordHash.RECORD_HASH_MODE_DEBUG_DROP, RecordHash.RECORD_HASH_MODE_DISABLE,
                    RecordHash.RECORD_HASH_MODE_RESET };
            boolean found = false;
            for (int i = 0; i < allowed.length; i++) {
                if (allowed[i].equalsIgnoreCase(ddProp)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                exchange.getIn().setHeader(AttributeType.DUPLICATE_DETECTION.name(), ddProp);
            } else {
                LOG.error("Value '" + ddProp + "' given for @duplicate-detection is invalid: ignoring");
            }
        } else {
            LOG.debug("Did not match @duplicate-detection tag in control file.");
        }

        String emailProp = newJob.getProperty(AttributeType.EMAIL_NOTIFY.getName());
        if (emailProp != null) {
            LOG.info("Matched @notify tag from control file parsing.");
            exchange.getIn().setHeader(AttributeType.EMAIL_NOTIFY.name(), emailProp);
        } else {
            LOG.info("Did not match @notify tag in control file.");
        }
    }

    private void setExchangeBody(Exchange exchange, NewBatchJob job, boolean hasErrors) {
        WorkNote workNote = new WorkNote(job.getId(), hasErrors);
        exchange.getIn().setBody(workNote, WorkNote.class);
    }

    private void createAndAddResourceEntries(NewBatchJob newJob, ControlFile cf) {
        String zipResource = null;
        if (newJob.getZipResourceEntry() != null) {
            zipResource = newJob.getZipResourceEntry().getResourceName();
        }

        for (IngestionFileEntry file : cf.getFileEntries()) {
            ResourceEntry resourceEntry = new ResourceEntry();
            resourceEntry.setResourceId(file.getFileName());
            resourceEntry.setExternallyUploadedResourceId(file.getFileName());
            resourceEntry.setResourceName(file.getFileName());
            resourceEntry.setResourceFormat(file.getFileFormat().getCode());
            resourceEntry.setResourceType(file.getFileType().getName());
            resourceEntry.setChecksum(file.getChecksum());
            resourceEntry.setTopLevelLandingZonePath(newJob.getTopLevelSourceId());
            resourceEntry.setResourceZipParent(zipResource);

            newJob.getResourceEntries().add(resourceEntry);
        }
    }

    private Map<String, String> aggregateBatchJobProperties(NewBatchJob job, ControlFile cf) {
        Map<String, String> batchProperties = job.getBatchProperties();
        Enumeration<Object> keys = cf.getConfigProperties().keys();
        Enumeration<Object> elements = cf.getConfigProperties().elements();

        while (keys.hasMoreElements()) {
            String key = keys.nextElement().toString();
            String element = elements.nextElement().toString();
            batchProperties.put(key, element);
        }
        return batchProperties;
    }

    private void notifyStart(Exchange exchange, ControlFile cf, boolean goodData) {
    	String distro = (String) exchange.getIn().getHeader(AttributeType.EMAIL_NOTIFY.name());
		if ( null != distro && !distro.isEmpty()) {
			String subject = "";
			String body = "";

			body += "At " + new Date().toString() + "\n";
			if ( goodData ) {
				subject += "InBloom ingestion started successfully";
				body += "The following ingestion files were received in good condition and are now being processed:\n\n" + cf.summaryString();
			}
			else {
				subject += "InBloom ingestion failed";
				body += "There was a problem processing the job.  Please check the Landing Zone area for logs.";
			}
	    	LOG.info("SENDING EMAIL to '" + distro + "':\n\nSubject: " + subject + "\n" + body);
	    	SendEmail se = new SendEmail();
	    	try {
	    		se.sendMail(distro, fromEmailAddress, subject, body);
	    	} catch( Exception ex ) {
	    		LOG.warn("Failed sending Email to '" + distro + "'\n:" + ex.getMessage());
	    	}
		}
    }

}

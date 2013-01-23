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

import java.util.Enumeration;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.aspect.MongoTrackingAspect;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.ControlFileWorkNote;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.landingzone.AttributeType;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
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

    @Override
    public void process(Exchange exchange) throws Exception {

        processUsingNewBatchJob(exchange);

        MongoTrackingAspect.aspectOf().reset();
    }

    private void processUsingNewBatchJob(Exchange exchange) throws Exception {

        String batchJobId = exchange.getIn().getHeader("BatchJobId", String.class);
        if (batchJobId == null) {

            handleNoBatchJobIdInExchange(exchange);
        } else {

            processControlFile(exchange, batchJobId);
        }
    }

    private void handleNoBatchJobIdInExchange(Exchange exchange) {
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
        exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.ERROR.name());
        LOG.error("No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }

    private void processControlFile(Exchange exchange, String batchJobId) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE, BATCH_JOB_STAGE_DESC);

        NewBatchJob newJob = null;
        try {

            newJob = batchJobDAO.findBatchJobById(batchJobId);
            TenantContext.setTenantId(newJob.getTenantId());
            TenantContext.setJobId(batchJobId);

            ControlFileDescriptor cfd = exchange.getIn().getBody(ControlFileDescriptor.class);

            ControlFile cf = cfd.getFileItem();

            aggregateBatchJobProperties(newJob, cf);

            ReportStats reportStats = new SimpleReportStats();

            if ((newJob.getProperty(AttributeType.PURGE.getName()) == null)
                    && (newJob.getProperty(AttributeType.PURGE_KEEP_EDORGS.getName()) == null)) {
                if (validator.isValid(cfd, databaseMessageReport, reportStats, new ControlFileSource(cf.getFileName()))) {
                    createAndAddResourceEntries(newJob, cf);
                } else {
                    boolean isZipFile = false;
                    for (ResourceEntry resourceEntry : newJob.getResourceEntries()) {
                        if (FileFormat.ZIP_FILE.getCode().equalsIgnoreCase(resourceEntry.getResourceFormat())) {
                            isZipFile = true;
                        }
                    }
                    if (!isZipFile) {
                        databaseMessageReport.warning(reportStats, new ControlFileSource(cf.getFileName()), CoreMessageCode.CORE_0051);

                    }
                }
            }

            setExchangeHeaders(exchange, newJob, reportStats);
            /*LOG.info("Control file is {}", cf.toString());
           exchange.getIn().setHeader("controlFile", cf);
*/
            setExchangeBody( exchange, cf,batchJobId);
        } catch (Exception exception) {
            handleExceptions(exchange, batchJobId, exception);
        } finally {
            if (newJob != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, newJob);
                batchJobDAO.saveBatchJob(newJob);
            }
        }
    }

    private void handleExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
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
            exchange.getIn().setHeader("hasErrors", reportStats.hasErrors());
            exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.ERROR.name());
        } else if ((newJob.getProperty(AttributeType.PURGE.getName()) != null)
                || (newJob.getProperty(AttributeType.PURGE_KEEP_EDORGS.getName()) != null)) {
            exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.PURGE.name());
            exchange.getIn().setHeader("batchJobId", newJob.getId());
        } else {
            exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.CONTROL_FILE_PROCESSED.name());
        }

        if (newJob.getProperty(AttributeType.DRYRUN.getName()) != null) {
            LOG.debug("Matched @dry-run tag from control file parsing.");
            exchange.getIn().setHeader(AttributeType.DRYRUN.getName(), true);
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
    }

    private void setExchangeBody(Exchange exchange, ControlFile cf, String batchJobId) {
        ControlFileWorkNote workNote = new ControlFileWorkNote(cf,batchJobId, "");
        exchange.getIn().setBody(workNote, ControlFileWorkNote.class);
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

}

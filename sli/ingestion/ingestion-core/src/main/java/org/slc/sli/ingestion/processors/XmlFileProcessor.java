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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.dal.TenantContext;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.landingzone.AttributeType;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.xml.idref.IdRefResolutionHandler;

/**
 * Processes a XML file
 *
 * @author ablum
 *
 */
@Component
public class XmlFileProcessor implements Processor {
    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.XML_FILE_PROCESSOR;

    @Autowired
    private IdRefResolutionHandler idRefResolutionHandler;

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Override
    public void process(Exchange exchange) throws Exception {
        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);

        if (workNote == null || workNote.getBatchJobId() == null) {
            missingBatchJobIdError(exchange);
        }

        if (exchange.getIn().getHeader(AttributeType.NO_ID_REF.name()) != null) {
            info("Skipping id ref resolution (specified by @no-id-ref in control file).");
            skipXmlFile(workNote, exchange);
        } else {
            info("Entering concurrent id ref resolution.");
            processXmlFile(workNote, exchange);
        }
    }

    private void skipXmlFile(WorkNote workNote, Exchange exchange) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE);

        String batchJobId = workNote.getBatchJobId();
        NewBatchJob newJob = batchJobDAO.findBatchJobById(batchJobId);

        boolean hasErrors = false;
        setExchangeHeaders(exchange, hasErrors);

        BatchJobUtils.stopStageAndAddToJob(stage, newJob);
        batchJobDAO.saveBatchJob(newJob);
    }

    private void processXmlFile(WorkNote workNote, Exchange exchange) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE);

        String batchJobId = workNote.getBatchJobId();
        NewBatchJob newJob = batchJobDAO.findBatchJobById(batchJobId);
        TenantContext.setTenantId(newJob.getTenantId());

        try {
            boolean hasErrors = false;
            for (ResourceEntry resource : newJob.getResourceEntries()) {

                // TODO change the Abstract handler to work with ResourceEntry so we can avoid
                // this kludge here and elsewhere
                if (resource.getResourceFormat() != null
                        && resource.getResourceFormat().equalsIgnoreCase(FileFormat.EDFI_XML.getCode())) {
                    FileFormat format = FileFormat.findByCode(resource.getResourceFormat());
                    FileType type = FileType.findByNameAndFormat(resource.getResourceType(), format);
                    IngestionFileEntry fe = new IngestionFileEntry(format, type, resource.getResourceId(),
                            resource.getChecksum());

                    fe.setFile(new File(resource.getResourceName()));

                    info("Starting ID ref resolution for file entry: {} ", fe.getFileName());
                    idRefResolutionHandler.handle(fe, fe.getErrorReport());
                    info("Finished ID ref resolution for file entry: {} ", fe.getFileName());

                    hasErrors = aggregateAndPersistErrors(batchJobId, fe);
                } else {
                    warn("Warning: The resource {} is not an EDFI format.", resource.getResourceName());
                }
            }

            setExchangeHeaders(exchange, hasErrors);

        } catch (Exception exception) {
            handleProcessingExceptions(exchange, batchJobId, exception);
        } finally {
            BatchJobUtils.stopStageAndAddToJob(stage, newJob);
            batchJobDAO.saveBatchJob(newJob);
        }
    }

    private void setExchangeHeaders(Exchange exchange, boolean hasErrors) {
        exchange.getIn().setHeader("hasErrors", hasErrors);
        exchange.getIn().setHeader("IngestionMessageType", MessageType.XML_FILE_PROCESSED.name());
    }

    private void handleProcessingExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        piiClearedError("Error processing batch job " + batchJobId, exception);
        Error error = Error.createIngestionError(batchJobId, null, BatchJobStageType.XML_FILE_PROCESSOR.getName(),
                null, null, null, FaultType.TYPE_ERROR.getName(), null, exception.toString());
        batchJobDAO.saveError(error);
    }

    private boolean aggregateAndPersistErrors(String batchJobId, IngestionFileEntry fe) {

        for (Fault fault : fe.getFaultsReport().getFaults()) {
            String faultMessage = fault.getMessage();
            String faultLevel = fault.isError() ? FaultType.TYPE_ERROR.getName()
                    : fault.isWarning() ? FaultType.TYPE_WARNING.getName() : "Unknown";

            Error error = Error.createIngestionError(batchJobId, fe.getFileName(),
                    BatchJobStageType.XML_FILE_PROCESSOR.getName(), null, null, null, faultLevel, faultLevel,
                    faultMessage);
            batchJobDAO.saveError(error);
        }

        return fe.getErrorReport().hasErrors();
    }

    private void missingBatchJobIdError(Exchange exchange) {
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }

    public IdRefResolutionHandler getIdRefResolutionHandler() {
        return idRefResolutionHandler;
    }

    public void setIdRefResolutionHandler(IdRefResolutionHandler idRefResolutionHandler) {
        this.idRefResolutionHandler = idRefResolutionHandler;
    }

    public BatchJobDAO getBatchJobDAO() {
        return batchJobDAO;
    }

    public void setBatchJobDAO(BatchJobDAO batchJobDAO) {
        this.batchJobDAO = batchJobDAO;
    }
}

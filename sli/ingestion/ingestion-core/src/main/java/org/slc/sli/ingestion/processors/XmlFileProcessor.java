package org.slc.sli.ingestion.processors;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.Fault;
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

    private static final Logger LOG = LoggerFactory.getLogger(XmlFileProcessor.class);

    @Autowired
    private IdRefResolutionHandler idRefResolutionHandler;

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Override
    public void process(Exchange exchange) throws Exception {

        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);

        if (workNote == null || workNote.getBatchJobId() == null) {
            missingBatchJobIdError(exchange);
        } else {
            processXmlFile(workNote, exchange);
        }
    }

    private void processXmlFile(WorkNote workNote, Exchange exchange) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE);

        String batchJobId = workNote.getBatchJobId();
        NewBatchJob newJob = null;
        try {
            newJob = batchJobDAO.findBatchJobById(batchJobId);

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

                    idRefResolutionHandler.handle(fe, fe.getErrorReport());

                    hasErrors = aggregateAndPersistErrors(batchJobId, fe);
                } else {
                    LOG.warn("Warning: The resource {} is not an EDFI format.", resource.getResourceName());
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
        LOG.error("Exception:", exception);
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

    public IdRefResolutionHandler getIdRefResolutionHandler() {
        return idRefResolutionHandler;
    }

    public void setIdRefResolutionHandler(
            IdRefResolutionHandler idRefResolutionHandler) {
        this.idRefResolutionHandler = idRefResolutionHandler;
    }

    private void missingBatchJobIdError(Exchange exchange) {
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }

}

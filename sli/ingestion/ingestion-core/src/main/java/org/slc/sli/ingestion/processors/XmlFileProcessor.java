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
import org.slc.sli.ingestion.handler.ReferenceResolutionHandler;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.util.BatchJobUtils;

/**
 * Processes a XML file
 *
 * @author ablum
 *
 */
@Component
public class XmlFileProcessor implements Processor {
    private Logger log = LoggerFactory.getLogger(XmlFileProcessor.class);

    @Autowired
    private ReferenceResolutionHandler referenceResolutionHandler;

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Override
    public void process(Exchange exchange) throws Exception {
        boolean hasErrors = false;

        String batchJobId = getBatchJobId(exchange);

        if (batchJobId != null) {

            NewBatchJob newJob = batchJobDAO.findBatchJobById(batchJobId);

            Stage stage = startAndGetStage(newJob);

            batchJobDAO.saveBatchJob(newJob);

            try {

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

                        referenceResolutionHandler.handle(fe, fe.getErrorReport());

                        aggregateAndPersistErrors(batchJobId, fe);

                        if (fe.getErrorReport().hasErrors()) {
                            hasErrors = true;
                        }
                    }
                }
                exchange.getIn().setHeader("hasErrors", hasErrors);
                exchange.getIn().setHeader("IngestionMessageType", MessageType.XML_FILE_PROCESSED.name());

            } catch (Exception exception) {
                exchange.getIn().setHeader("ErrorMessage", exception.toString());
                exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
                log.error("Exception:", exception);
                Error error = Error.createIngestionError(batchJobId, BatchJobStageType.XML_FILE_PROCESSOR.getName(),
                        null, null, null, null, FaultType.TYPE_ERROR.getName(), null, exception.toString());
                batchJobDAO.saveError(error);
            }

            stage.stopStage();

            batchJobDAO.saveBatchJob(newJob);

        } else {
            missingBatchJobIdError(exchange);

        }
    }

    private void aggregateAndPersistErrors(String batchJobId, IngestionFileEntry fe) {
        for (Fault fault : fe.getFaultsReport().getFaults()) {
            String faultMessage = fault.getMessage();
            String faultLevel = fault.isError() ? FaultType.TYPE_ERROR.getName()
                    : fault.isWarning() ? FaultType.TYPE_WARNING.getName() : "Unknown";

            Error error = Error.createIngestionError(batchJobId, BatchJobStageType.XML_FILE_PROCESSOR.getName(),
                    fe.getFileName(), null, null, null, faultLevel, faultLevel, faultMessage);
            batchJobDAO.saveError(error);
        }
        BatchJobUtils.writeErrorsWithDAO(batchJobId, BatchJobStageType.XML_FILE_PROCESSOR, fe.getFaultsReport(),
                batchJobDAO);
    }

    public ReferenceResolutionHandler getReferenceResolutionHandler() {
        return referenceResolutionHandler;
    }

    public void setReferenceResolutionHandler(ReferenceResolutionHandler referenceResolutionHandler) {
        this.referenceResolutionHandler = referenceResolutionHandler;
    }

    private Stage startAndGetStage(NewBatchJob newJob) {
        Stage stage = new Stage();
        newJob.getStages().add(stage);
        stage.setStageName(BatchJobStageType.XML_FILE_PROCESSOR.getName());
        stage.startStage();
        return stage;
    }

    private String getBatchJobId(Exchange exchange) {
        return exchange.getIn().getHeader("BatchJobId", String.class);
    }

    private void missingBatchJobIdError(Exchange exchange) {
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        log.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }

}

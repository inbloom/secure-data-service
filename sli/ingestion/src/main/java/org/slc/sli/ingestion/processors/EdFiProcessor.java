package org.slc.sli.ingestion.processors;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.handler.AbstractIngestionHandler;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.util.performance.Profiled;

/**
 * Camel interface for processing our EdFi batch job.
 * Derives the handler to use based on the file format of the files in the batch job and delegates
 * the processing to it.
 *
 * @author dduran
 *
 */
@Component
public class EdFiProcessor implements Processor {

    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(EdFiProcessor.class);

    private Map<FileFormat, AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry>> fileHandlerMap;

    @Override
    @ExtractBatchJobIdToContext
    @Profiled
    public void process(Exchange exchange) throws Exception {
        
        try {
            BatchJob job = exchange.getIn().getBody(BatchJob.class);

            for (IngestionFileEntry fe : job.getFiles()) {
                processFileEntry(fe);
                job.getFaultsReport().append(fe.getFaultsReport());
            }

            // set headers for ingestion routing
            if (job.getErrorReport().hasErrors()) {
                exchange.getIn().setHeader("hasErrors", job.getErrorReport().hasErrors());
                exchange.getIn().setHeader("IngestionMessageType", MessageType.PERSIST_REQUEST.name());
            } else if (exchange.getIn().getHeader("dry-run").equals(true)) {
                LOG.info("dry-run specified; data will not be published");
                exchange.getIn().setHeader("IngestionMessageType", MessageType.DONE.name());
            } else {
                exchange.getIn().setHeader("IngestionMessageType", MessageType.PERSIST_REQUEST.name());
            }
        } catch (Exception exception) {
            exchange.getIn().setHeader("ErrorMessage", exception.toString());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            LOG.error("Exception:",  exception);
        }
    }

    public void processFileEntry(IngestionFileEntry fe) {

        if (fe.getFileType() != null) {

            FileFormat fileFormat = fe.getFileType().getFileFormat();

            // get the handler for this file format
            AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> fileHandler = fileHandlerMap
                    .get(fileFormat);
            if (fileHandler != null) {

                fileHandler.handle(fe);

            } else {
                throw new IllegalArgumentException("Unsupported file format: " + fe.getFileType().getFileFormat());
            }
        } else {
            throw new IllegalArgumentException("FileType was not provided.");
        }
    }

    public void setFileHandlerMap(
            Map<FileFormat, AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry>> fileHandlerMap) {
        this.fileHandlerMap = fileHandlerMap;
    }
}

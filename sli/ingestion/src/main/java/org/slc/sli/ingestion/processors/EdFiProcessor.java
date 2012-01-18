package org.slc.sli.ingestion.processors;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.handler.AbstractIngestionHandler;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;

import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
    @Profiled(tag = "EdFiProcessor - file {$0.getIn().getHeader(\"CamelFileNameOnly\")} - batch {$0.getExchangeId()}")
    public void process(Exchange exchange) throws Exception {

        BatchJob job = exchange.getIn().getBody(BatchJob.class);

        for (IngestionFileEntry fe : job.getFiles()) {

            processFileEntry(fe);

            job.getFaultsReport().append(fe.getFaultsReport());
        }

        // report status of errors to exchange
        if (job.getErrorReport().hasErrors()) {
            exchange.getIn().setHeader("hasErrors", job.getErrorReport().hasErrors());
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

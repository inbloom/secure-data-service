package org.slc.sli.ingestion.processors.datamodel;

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
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.util.performance.Profiled;

/**
 * Camel interface for processing our EdFi batch job.
 * Derives the handler to use based on the file format of the files in the batch job and delegates
 * the processing to it.
 *
 * @author bsuzuki
 *
 */
@Component
public class EdFiProcessor extends org.slc.sli.ingestion.processors.EdFiProcessor implements Processor {

    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(EdFiProcessor.class);

    private Map<FileFormat, AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry>> fileHandlerMap;

    @Override
    @ExtractBatchJobIdToContext
    @Profiled
    public void process(Exchange exchange) throws Exception {

        // TODO get the batchjob from the state manager, define the stageName explicitly
        // Get the batch job ID from the exchange
//        String batchJobId = exchange.getIn().getBody(String.class);
//        NewBatchJob job = BatchJobUtils.getBatchJob(batchJobId);
//        BatchJobUtils.startStage(job, this.getClass().getName());
        BatchJob batchJob = BatchJobUtils.getBatchJobUsingStateManager(exchange);
        
        try {
            
            for (IngestionFileEntry fe : batchJob.getFiles()) {
                
                // TODO BatchJobUtil.startMetric(batchJobId, this.getClass().getName(), fe.getFileName())

                // TODO pass the NewBatchJob
                processFileEntry(fe);
                batchJob.getFaultsReport().append(fe.getFaultsReport());

                // TODO BatchJobUtil.stopMetric(batchJobId, this.getClass().getName(), fe.getFileName(), fe.getRecordCount, fe.getFaultsReport.getFaults().size())
            }

            // set headers
            if (batchJob.getErrorReport().hasErrors()) {
                exchange.getIn().setHeader("hasErrors", batchJob.getErrorReport().hasErrors());
            }

            // next route should be DATA_TRANSFORMATION
            exchange.getIn().setHeader("IngestionMessageType", MessageType.DATA_TRANSFORMATION.name());

        } catch (Exception exception) {
            exchange.getIn().setHeader("ErrorMessage", exception.toString());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            LOG.error("Exception:", exception);
            // TODO JobLogStatus.log
        }

        BatchJobUtils.saveBatchJobUsingStateManager(batchJob);
        // TODO When the interface firms up we should set the stage stopTimeStamp in job before saving to db, rather than after really
        // BatchJobUtils.stopStage(batchJob.getId(), this.getClass().getName());

    }

    public void processFileEntry(IngestionFileEntry fe) {

        if (fe.getFileType() != null) {

            FileFormat fileFormat = fe.getFileType().getFileFormat();

            // get the handler for this file format
            AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> fileHandler = fileHandlerMap
                    .get(fileFormat);
            
            if (fileHandler != null) {

                // TODO change handlers to take a NewBatchJob job
                // TODO handlers must create new resources and log errors to the db 
                // TODO record counts must be set for resources when they are first processed/created
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

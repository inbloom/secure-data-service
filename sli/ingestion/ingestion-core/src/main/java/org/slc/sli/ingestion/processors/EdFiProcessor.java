package org.slc.sli.ingestion.processors;

import java.net.InetAddress;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.performance.Profiled;
import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.handler.AbstractIngestionHandler;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.validation.ErrorReport;

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

        InetAddress localhost = InetAddress.getLocalHost();

        String batchJobId = exchange.getIn().getHeader("BatchJobId", String.class);
        if (batchJobId == null) {
            exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            LOG.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
        }

        try {
            // get the job from the db
            BatchJobDAO batchJobDAO = new BatchJobMongoDA();
            NewBatchJob newJob = batchJobDAO.findBatchJobById(batchJobId);

            Stage stage = new Stage();
            stage.setStageName(BatchJobStageType.EDFI_PROCESSING.getName());
            newJob.getStages().add(stage);

            stage.startStage();
            batchJobDAO.saveBatchJob(newJob);


            BatchJob batchJob = exchange.getIn().getBody(BatchJob.class);

            for (IngestionFileEntry fe : batchJob.getFiles()) {

                Metrics metrics = new Metrics(fe.getFileName(), localhost.getHostAddress(), localhost.getHostName());
                metrics.startMetric();
                stage.getMetrics().add(metrics);
                batchJobDAO.saveBatchJob(newJob);

                FileProcessStatus fileProcessStatus = new FileProcessStatus();

                ErrorReport errorReport = fe.getErrorReport();
                processFileEntry(fe, errorReport, fileProcessStatus);
                batchJob.getFaultsReport().append(fe.getFaultsReport());

                metrics.setRecordCount(fileProcessStatus.getTotalRecordCount());

                int errorCount = 0;
                for (Fault fault : fe.getFaultsReport().getFaults()) {
                    if (fault.isError()) {
                        errorCount++;
                    }
                    String faultMessage = fault.getMessage();
                    String faultLevel  = fault.isError() ? FaultType.TYPE_ERROR.getName() : fault.isWarning() ? FaultType.TYPE_WARNING.getName() : "Unknown";
                    BatchJobMongoDA.logBatchStageError(batchJobId, BatchJobStageType.EDFI_PROCESSING, faultLevel, faultLevel, faultMessage);
                }

                metrics.setErrorCount(errorCount);
                metrics.stopMetric();

                ResourceEntry resource = new ResourceEntry();
                resource.setResourceId(fileProcessStatus.getOutputFileName());
                resource.setResourceName(fileProcessStatus.getOutputFilePath());
                resource.setResourceFormat(FileFormat.NEUTRALRECORD.toString());
                resource.setResourceType(fe.getFileType().getName());
                resource.setRecordCount((int) fileProcessStatus.getTotalRecordCount());

                newJob.getResourceEntries().add(resource);
                batchJobDAO.saveBatchJob(newJob);
            }

            stage.stopStage();
            batchJobDAO.saveBatchJob(newJob);

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
            if (batchJobId != null) {
                BatchJobMongoDA.logBatchStageError(batchJobId, BatchJobStageType.EDFI_PROCESSING,
                        FaultType.TYPE_ERROR.getName(), null, exception.toString());
            }
        }

    }

    public void processFileEntry(IngestionFileEntry fe, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {

        if (fe.getFileType() != null) {

            FileFormat fileFormat = fe.getFileType().getFileFormat();

            // get the handler for this file format
            AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> fileHandler = fileHandlerMap
                    .get(fileFormat);

            if (fileHandler != null) {
                fileHandler.handle(fe, errorReport, fileProcessStatus);

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

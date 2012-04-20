package org.slc.sli.ingestion.processors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.FileType;
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

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.EDFI_PROCESSOR;

    private static final Logger LOG = LoggerFactory.getLogger(EdFiProcessor.class);

    private Map<FileFormat, AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry>> fileHandlerMap;

    @Override
    @ExtractBatchJobIdToContext
    @Profiled
    public void process(Exchange exchange) throws Exception {

        String batchJobId = exchange.getIn().getHeader("BatchJobId", String.class);
        if (batchJobId == null) {

            handleNoBatchJobIdInExchange(exchange);
        } else {

            processEdFi(exchange, batchJobId);
        }

    }

    private void processEdFi(Exchange exchange, String batchJobId) {
        try {
            Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE);

            BatchJobDAO batchJobDAO = new BatchJobMongoDA();
            NewBatchJob newJob = batchJobDAO.findBatchJobById(batchJobId);

            newJob.getStages().add(stage);
            batchJobDAO.saveBatchJob(newJob);

            List<IngestionFileEntry> fileEntryList = extractFileEntryList(batchJobId, newJob);

            boolean anyErrorsProcessingFiles = false;
            for (IngestionFileEntry fe : fileEntryList) {

                Metrics metrics = Metrics.createAndStart(fe.getFileName());
                stage.getMetrics().add(metrics);
                batchJobDAO.saveBatchJob(newJob);

                FileProcessStatus fileProcessStatus = new FileProcessStatus();
                ErrorReport errorReport = fe.getErrorReport();

                // actually do the processing
                processFileEntry(fe, errorReport, fileProcessStatus);

                metrics.setRecordCount(fileProcessStatus.getTotalRecordCount());

                int errorCount = aggregateAndLogProcessingErrors(batchJobId, fe);
                if (errorCount > 0) {
                    anyErrorsProcessingFiles = true;
                }

                metrics.setErrorCount(errorCount);

                ResourceEntry resource = createResourceForOutputFile(fe, fileProcessStatus);
                newJob.getResourceEntries().add(resource);

                metrics.stopMetric();

            }

            batchJobDAO.saveBatchJob(newJob);

            setExchangeHeaders(exchange, anyErrorsProcessingFiles);

        } catch (Exception exception) {
            handleProcessingExceptions(exchange, batchJobId, exception);
        }
    }

    public void processFileEntry(IngestionFileEntry fe, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {

        if (fe.getFileType() != null) {
            FileFormat fileFormat = fe.getFileType().getFileFormat();

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

    private int aggregateAndLogProcessingErrors(String batchJobId, IngestionFileEntry fe) {
        int errorCount = 0;
        for (Fault fault : fe.getFaultsReport().getFaults()) {
            if (fault.isError()) {
                errorCount++;
            }
            String faultMessage = fault.getMessage();
            String faultLevel = fault.isError() ? FaultType.TYPE_ERROR.getName()
                    : fault.isWarning() ? FaultType.TYPE_WARNING.getName() : "Unknown";

            // TODO: this should use the BatchJobDAO interface
            BatchJobMongoDA.logBatchStageError(batchJobId, BATCH_JOB_STAGE, faultLevel, faultLevel, faultMessage);
        }
        return errorCount;
    }

    private void handleProcessingExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("Exception:", exception);
        if (batchJobId != null) {
            BatchJobMongoDA.logBatchStageError(batchJobId, BATCH_JOB_STAGE, FaultType.TYPE_ERROR.getName(), null,
                    exception.toString());
        }
    }

    private void setExchangeHeaders(Exchange exchange, boolean hasError) {
        if (hasError) {
            exchange.getIn().setHeader("hasErrors", hasError);
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        } else {
            exchange.getIn().setHeader("IngestionMessageType", MessageType.DATA_TRANSFORMATION.name());
        }
    }

    private ResourceEntry createResourceForOutputFile(IngestionFileEntry fe, FileProcessStatus fileProcessStatus) {
        ResourceEntry resource = new ResourceEntry();
        String rId = fileProcessStatus.getOutputFileName();
        if (rId == null) {
            rId = "Empty_" + (fe.getFileName());
        }
        resource.setResourceId(rId);
        resource.setResourceName(fileProcessStatus.getOutputFilePath());
        resource.setResourceFormat(FileFormat.NEUTRALRECORD.getCode());
        resource.setResourceType(fe.getFileType().getName());
        resource.setRecordCount((int) fileProcessStatus.getTotalRecordCount());
        resource.setExternallyUploadedResourceId(fe.getFileName());
        return resource;
    }

    private List<IngestionFileEntry> extractFileEntryList(String batchJobId, NewBatchJob newJob) {
        List<IngestionFileEntry> fileEntryList = new ArrayList<IngestionFileEntry>();

        List<ResourceEntry> resourceList = newJob.getResourceEntries();
        for (ResourceEntry resource : resourceList) {
            if (FileFormat.EDFI_XML.getCode().equalsIgnoreCase(resource.getResourceFormat())) {

                FileFormat fileFormat = FileFormat.findByCode(resource.getResourceFormat());
                FileType fileType = FileType.findByNameAndFormat(resource.getResourceType(), fileFormat);
                String fileName = resource.getResourceId();
                String checksum = resource.getChecksum();

                IngestionFileEntry fe = new IngestionFileEntry(fileFormat, fileType, fileName, checksum);
                fe.setFile(new File(resource.getResourceName()));
                fe.setBatchJobId(batchJobId);

                fileEntryList.add(fe);
            }
        }
        return fileEntryList;
    }

    private void handleNoBatchJobIdInExchange(Exchange exchange) {
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }

    public void setFileHandlerMap(
            Map<FileFormat, AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry>> fileHandlerMap) {
        this.fileHandlerMap = fileHandlerMap;
    }
}

package org.slc.sli.ingestion.processors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.performance.Profiled;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.handler.SmooksFileHandler;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;
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

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.EDFI_PROCESSOR;

    private static final Logger LOG = LoggerFactory.getLogger(EdFiProcessor.class);

    @Autowired
    private SmooksFileHandler smooksFileHandler;

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    @Override
    @ExtractBatchJobIdToContext
    @Profiled
    public void process(Exchange exchange) throws Exception {

        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);

        if (workNote == null || workNote.getBatchJobId() == null) {
            handleNoBatchJobIdInExchange(exchange);
        } else {
            processEdFi(workNote, exchange);
        }
    }

    private void processEdFi(WorkNote workNote, Exchange exchange) {
        LOG.info("Starting stage: {}", BATCH_JOB_STAGE);

        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE);

        String batchJobId = workNote.getBatchJobId();
        NewBatchJob newJob = null;
        try {
            newJob = batchJobDAO.findBatchJobById(batchJobId);

            List<IngestionFileEntry> fileEntryList = extractFileEntryList(batchJobId, newJob);

            // prepare staging database
            setupStagingDatabase(batchJobId);

            boolean anyErrorsProcessingFiles = false;

            if (fileEntryList.size() > 0) {
                // prepare staging database
                setupStagingDatabase(batchJobId);
            }

            for (IngestionFileEntry fe : fileEntryList) {

                Metrics metrics = Metrics.newInstance(fe.getFileName());
                stage.getMetrics().add(metrics);

                FileProcessStatus fileProcessStatus = new FileProcessStatus();
                ErrorReport errorReport = fe.getErrorReport();

                // actually do the processing
                processFileEntry(fe, errorReport, fileProcessStatus);

                metrics.setRecordCount(fileProcessStatus.getTotalRecordCount());

                int errorCount = aggregateAndLogProcessingErrors(batchJobId, fe);
                if (errorCount > 0) {
                    anyErrorsProcessingFiles = true;
                    metrics.setErrorCount(errorCount);
                }

                ResourceEntry resource = BatchJobUtils.createResourceForOutputFile(fe, fileProcessStatus);
                newJob.getResourceEntries().add(resource);
            }

            setExchangeHeaders(exchange, anyErrorsProcessingFiles);

        } catch (Exception exception) {
            handleProcessingExceptions(exchange, batchJobId, exception);
        } finally {
            if (newJob != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, newJob);
                batchJobDAO.saveBatchJob(newJob);
            }
        }
    }

    public void processFileEntry(IngestionFileEntry fe, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {

        if (fe.getFileType() != null) {
            FileFormat fileFormat = fe.getFileType().getFileFormat();
            if (fileFormat == FileFormat.EDFI_XML) {
                LOG.info("Processing file: {}", fe.getFile().getPath());

                smooksFileHandler.handle(fe, errorReport, fileProcessStatus);

                LOG.info("Done processing file: {}", fe.getFile().getPath());
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

            Error error = Error.createIngestionError(batchJobId, fe.getFileName(), BATCH_JOB_STAGE.getName(), null,
                    null, null, faultLevel, faultLevel, faultMessage);
            batchJobDAO.saveError(error);
        }
        return errorCount;
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

                String lzPath = resource.getTopLevelLandingZonePath();

                IngestionFileEntry fe = new IngestionFileEntry(fileFormat, fileType, fileName, checksum, lzPath);
                fe.setFile(new File(resource.getResourceName()));
                fe.setBatchJobId(batchJobId);

                fileEntryList.add(fe);
            }
        }
        return fileEntryList;
    }

    private void handleProcessingExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LogUtil.error(LOG, "Error processing batch job " + batchJobId, exception);
        if (batchJobId != null) {
            Error error = Error.createIngestionError(batchJobId, null, BATCH_JOB_STAGE.getName(), null, null, null,
                    FaultType.TYPE_ERROR.getName(), null, exception.toString());
            batchJobDAO.saveError(error);
        }
    }

    private void setExchangeHeaders(Exchange exchange, boolean hasError) {
        if (hasError) {
            exchange.getIn().setHeader("hasErrors", hasError);
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        }
    }

    private void handleNoBatchJobIdInExchange(Exchange exchange) {
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }

    private void setupStagingDatabase(String batchJobId) {
        neutralRecordMongoAccess.getRecordRepository().ensureIndexesForJob(batchJobId);
    }

}

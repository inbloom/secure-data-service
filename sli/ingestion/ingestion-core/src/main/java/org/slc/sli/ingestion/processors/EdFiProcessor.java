package org.slc.sli.ingestion.processors;

import java.io.File;
import java.net.InetAddress;
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
    private boolean switchToNewJob = true;
    private boolean pluginOldPipeline = false;

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
            //BatchJob batchJob = exchange.getIn().getBody(BatchJob.class);

            // get the job from the db
            BatchJobDAO batchJobDAO = new BatchJobMongoDA();
            NewBatchJob newJob = batchJobDAO.findBatchJobById(batchJobId);

            Stage stage = new Stage();
            stage.setStageName(BatchJobStageType.EDFI_PROCESSING.getName());
            newJob.getStages().add(stage);

            stage.startStage();
            batchJobDAO.saveBatchJob(newJob);

            List<IngestionFileEntry> felOrignal = null;
            //felOrignal = batchJob.getFiles();

            // create file entry list from resources from DB
            List<ResourceEntry> resourceList = newJob.getResourceEntries();

            List<IngestionFileEntry> fileEntryList = new ArrayList<IngestionFileEntry>();

            for (ResourceEntry resource : resourceList) {
                if (resource.getResourceFormat() != null && resource.getResourceFormat().equalsIgnoreCase(FileFormat.EDFI_XML.getCode())) {
                    IngestionFileEntry fe = new IngestionFileEntry(FileFormat.findByCode(resource.getResourceFormat()),
                            FileType.findByNameAndFormat(resource.getResourceType(), FileFormat.findByCode(resource.getResourceFormat())),
                            resource.getResourceId(), resource.getChecksum());
                    fe.setFile(new File(resource.getResourceName()));

                    //TODO: set to the current batchJobId
//                    if (pluginOldPipeline) {
//                        fe.setBatchJobId(batchJob.getId());
//                    } else {
                        fe.setBatchJobId(batchJobId);
//                    }
                    fileEntryList.add(fe);
                }
            }

            // switch to new code
            if (this.switchToNewJob) {
                felOrignal = fileEntryList;
            }

            boolean hasError = false;
            int emptyCounter = 1;
            for (IngestionFileEntry fe : felOrignal) {

                Metrics metrics = new Metrics(fe.getFileName(), localhost.getHostAddress(), localhost.getHostName());
                metrics.startMetric();
                stage.getMetrics().add(metrics);
                batchJobDAO.saveBatchJob(newJob);

                FileProcessStatus fileProcessStatus = new FileProcessStatus();

                ErrorReport errorReport = fe.getErrorReport();
                processFileEntry(fe, errorReport, fileProcessStatus);
                //batchJob.getFaultsReport().append(fe.getFaultsReport());

                metrics.setRecordCount(fileProcessStatus.getTotalRecordCount());

                int errorCount = 0;
                for (Fault fault : fe.getFaultsReport().getFaults()) {
                    if (fault.isError()) {
                        errorCount++;
                        hasError = true;
                    }
                    String faultMessage = fault.getMessage();
                    String faultLevel  = fault.isError() ? FaultType.TYPE_ERROR.getName() : fault.isWarning() ? FaultType.TYPE_WARNING.getName() : "Unknown";
                    BatchJobMongoDA.logBatchStageError(batchJobId, BatchJobStageType.EDFI_PROCESSING, faultLevel, faultLevel, faultMessage);
                }

                metrics.setErrorCount(errorCount);
                metrics.stopMetric();

                ResourceEntry resource = new ResourceEntry();
                String rId = fileProcessStatus.getOutputFileName();
                if(rId == null) rId = "Empty_" + (emptyCounter++);
                resource.setResourceId(rId);
                resource.setResourceName(fileProcessStatus.getOutputFilePath());
                resource.setResourceFormat(FileFormat.NEUTRALRECORD.getCode());
                resource.setResourceType(fe.getFileType().getName());
                resource.setRecordCount((int) fileProcessStatus.getTotalRecordCount());
                resource.setExternallyUploadedResourceId(fe.getFileName());

                newJob.getResourceEntries().add(resource);
                batchJobDAO.saveBatchJob(newJob);
            }

            stage.stopStage();
            batchJobDAO.saveBatchJob(newJob);

//            if (this.switchToNewJob) {
//                batchJob.setFiles(felOrignal);
//            }

            // set headers
            if (hasError) {
                exchange.getIn().setHeader("hasErrors", hasError);
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

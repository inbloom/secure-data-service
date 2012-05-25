package org.slc.sli.ingestion.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;

/**
 * Utilities for BatchJob
 *
 * @author dduran
 *
 */
public class BatchJobUtils {

    private static final Logger LOG = LoggerFactory.getLogger(BatchJobUtils.class);

    private static final String TIMESTAMPPATTERN = "yyyy-MM-dd:HH-mm-ss";
    private static final FastDateFormat FORMATTER = FastDateFormat.getInstance(TIMESTAMPPATTERN);

    private static InetAddress localhost;
    static {
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getHostAddress() {
        return localhost.getHostAddress();
    }

    public static String getHostName() {
        return localhost.getHostName();
    }

    public static Date getCurrentTimeStamp() {
        return new Date();
    }

    public static void writeErrorsWithDAO(String batchId, String resourceId, BatchJobStageType stage,
            FaultsReport errorReport, BatchJobDAO batchJobDAO) {
        if (errorReport.hasErrors()) {
            String severity;
            List<Fault> faults = errorReport.getFaults();
            for (Fault fault : faults) {
                if (fault.isError()) {
                    severity = FaultType.TYPE_ERROR.getName();
                } else if (fault.isWarning()) {
                    severity = FaultType.TYPE_WARNING.getName();
                } else {
                    // TODO consider adding this to FaultType
                    severity = "UNKNOWN";
                }
                Error error = Error.createIngestionError(batchId, resourceId, stage.getName(), null, null, null,
                        severity, null, fault.getMessage());
                batchJobDAO.saveError(error);
            }
        }
    }

    public static void stopStageAndAddToJob(Stage stage, NewBatchJob job) {
        stage.stopStage();
        job.addStage(stage);
    }

    public static void stopStageChunkAndAddToJob(Stage stage, NewBatchJob job) {
        stage.stopStage();
        job.addStageChunk(stage);
        job.stop();
    }

    public static ResourceEntry createResourceForOutputFile(IngestionFileEntry fe, FileProcessStatus fileProcessStatus) {
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

}

/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.ingestion.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

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
public final class BatchJobUtils {

    private BatchJobUtils() { }

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

    public static void writeWarningssWithDAO(String batchId, String resourceId, BatchJobStageType stage,
            FaultsReport errorReport, BatchJobDAO batchJobDAO) {
        String severity;
        List<Fault> faults = errorReport.getFaults();
        for (Fault fault : faults) {
            if (fault.isWarning()) {
                severity = FaultType.TYPE_WARNING.getName();
                Error error = Error.createIngestionError(batchId, resourceId, stage.getName(), null, null, null,
                        severity, null, fault.getMessage());
                batchJobDAO.saveError(error);
            }
        }
    }

    public static void completeStageAndJob(Stage stage, NewBatchJob job) {
        job.stop();
        stopStageAndAddToJob(stage, job);
    }

    public static void stopStageAndAddToJob(Stage stage, NewBatchJob job) {
        stage.stopStage();
        job.addStage(stage);
    }

    public static void stopStageChunkAndAddToJob(Stage stage, NewBatchJob job) {
        stage.stopStage();
        job.addStageChunk(stage);
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

    public static String jobIdToDbName(String jobId) {
        // only use the UUID portion of the jobId
        if (jobId.length() < 36) {
            throw new IllegalArgumentException("Job id length should not be less than 36 - should contain UUID");
        }

        int startOfUuid = jobId.length() - 36;

        return "staging-" + jobId.substring(startOfUuid);
    }

}

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


package org.slc.sli.ingestion.landingzone;

import java.io.File;
import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.SimpleReportStats;
import org.slc.sli.ingestion.reporting.SimpleSource;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.ErrorReportSupport;

/**
 * Represents an Ingestion File Entry which includes the file to ingest along with its
 * metainformation.
 *
 */
public class IngestionFileEntry implements Serializable, ErrorReportSupport {

    private static final long serialVersionUID = 8326156381009199389L;

    // Attributes
    private FileFormat fileFormat;
    private FileType fileType;
    private String fileName;
    private File file;
    private File neutralRecordFile;
    private File deltaNeutralRecordFile;
    private String checksum;
    private FaultsReport faultsReport;
    private String topLevelLandingZonePath;

    @Autowired
    private AbstractMessageReport databaseMessageReport;

    private ReportStats reportStats;

    // will only be set when this is added to a BatchJob
    private String batchJobId;

    // Constructors
    public IngestionFileEntry(FileFormat fileFormat, FileType fileType, String fileName, String checksum) {
        this(fileFormat, fileType, fileName, checksum, null);
    }

    public IngestionFileEntry(FileFormat fileFormat, FileType fileType, String fileName, String checksum, String topLevelLandingZonePath) {
        this.fileFormat = fileFormat;
        this.fileType = fileType;
        this.fileName = fileName;
        this.checksum = checksum;
        this.faultsReport = new FaultsReport();
        this.topLevelLandingZonePath = topLevelLandingZonePath;
        this.reportStats = new SimpleReportStats(new SimpleSource(batchJobId, fileName, null));
    }

    // Methods

    /**
     * @return the reportStats
     */
    public ReportStats getReportStats() {
        return reportStats;
    }

    /**
     * Set the Ingestion file format.
     *
     * @param fileFormat
     *            to set
     */
    public void setFileFormat(FileFormat fileFormat) {
        this.fileFormat = fileFormat;
    }

    /**
     * Get the Ingestion file format.
     *
     * @return fileFormat to obtain
     */
    public FileFormat getFileFormat() {
        return this.fileFormat;
    }

    /**
     * Set the Ingestion file type.
     *
     * @param fileType
     *            to set
     */
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    /**
     * Get the Ingestion file type.
     *
     * @return fileType to obtain
     */
    public FileType getFileType() {
        return this.fileType;
    }

    /**
     * Set the Ingestion file name.
     *
     * @param fileName
     *            to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Get the Ingestion file name.
     *
     * @return fileName to obtain
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Set the Ingestion file.
     *
     * @param file
     *            to set
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Get the Ingestion file.
     *
     * @return file to obtain
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Set the Ingestion file checksum.
     *
     * @param checksum
     *            to set
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    /**
     * Get the Ingestion file checksum.
     *
     * @return checksum to obtain
     */
    public String getChecksum() {
        return this.checksum;
    }

    public File getNeutralRecordFile() {
        return neutralRecordFile;
    }

    public void setNeutralRecordFile(File neutralRecordFile) {
        this.neutralRecordFile = neutralRecordFile;
    }

    public File getDeltaNeutralRecordFile() {
        return deltaNeutralRecordFile;
    }

    public void setDeltaNeutralRecordFile(File deltaNeutralRecordFile) {
        this.deltaNeutralRecordFile = deltaNeutralRecordFile;
    }

    public FaultsReport getFaultsReport() {
        return this.faultsReport;
    }

    @Override
    public ErrorReport getErrorReport() {
        return getFaultsReport();
    }

    public String getBatchJobId() {
        return batchJobId;
    }

    public void setBatchJobId(String batchJobId) {
        this.batchJobId = batchJobId;
    }

    /**
     * @return the topLevelLandingZonePath
     */
    public String getTopLevelLandingZonePath() {
        return topLevelLandingZonePath;
    }

    /**
     * @param topLevelLandingZonePath the topLevelLandingZonePath to set
     */
    public void setTopLevelLandingZonePath(String topLevelLandingZonePath) {
        this.topLevelLandingZonePath = topLevelLandingZonePath;
    }

    /**
     * @return the databaseMessageReport
     */
    public AbstractMessageReport getDatabaseMessageReport() {
        return databaseMessageReport;
    }

}

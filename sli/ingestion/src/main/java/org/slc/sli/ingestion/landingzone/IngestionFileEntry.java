package org.slc.sli.ingestion.landingzone;

import java.io.File;

import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.ErrorReportSupport;

/**
 * Represents an Ingestion File Entry which includes the file to ingest along with its
 * metainformation.
 *
 */
public class IngestionFileEntry implements ErrorReportSupport {

    // Attributes
    private FileFormat fileFormat;
    private FileType fileType;
    private String fileName;
    private File file;
    private File neutralRecordFile;
    private File deltaNeutralRecordFile;
    private String checksum;
    private FaultsReport faultsReport;

    // Constructors
    public IngestionFileEntry(FileFormat fileFormat, FileType fileType, String fileName, String checksum) {
        this.fileFormat = fileFormat;
        this.fileType = fileType;
        this.fileName = fileName;
        this.checksum = checksum;
        this.faultsReport = new FaultsReport();
    }

    // Methods

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

}

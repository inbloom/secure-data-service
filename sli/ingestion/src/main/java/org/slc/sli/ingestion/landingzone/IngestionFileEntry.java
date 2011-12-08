package org.slc.sli.ingestion.landingzone;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;

/**
 * Represents an Ingestion File Entry which includes the file to ingest along with its metainformation.
 * 
 */
public class IngestionFileEntry {
    
    // Attributes
    private FileFormat fileFormat;  
    private FileType fileType;  
    private String fileName;
    private String checksum;
    
    // Constructors
    public IngestionFileEntry(FileFormat fileFormat, FileType fileType, String fileName, String checksum) {
        this.fileFormat = fileFormat;
        this.fileType = fileType;
        this.fileName = fileName;
        this.checksum = checksum;
    }
    
    // Methods

    /**
     * Set the Ingestion file format.
     * 
     * @param fileFormat to set
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
     * @param fileType to set
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
     * @param fileName to set
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
     * Set the Ingestion file checksum.
     * 
     * @param checksum to set
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
    
}

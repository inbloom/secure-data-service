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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.io.FileUtils;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.Resource;

/**
 * Represents an Ingestion File Entry which includes the file to ingest along with its
 * metainformation.
 *
 */
public class IngestionFileEntry implements Serializable, Resource {

    private static final long serialVersionUID = 8326156381009199389L;

    // Attributes
    private String parentZipFileOrDirectory;
    private FileFormat fileFormat;
    private FileType fileType;
    private String fileName;
    private String checksum;
    private boolean valid;

    // will only be set when this is added to a BatchJob
    private String batchJobId;

    public IngestionFileEntry(String parentZipFileOrDirectory, FileFormat fileFormat, FileType fileType, String fileName, String checksum) {
        this.parentZipFileOrDirectory = parentZipFileOrDirectory;
        this.fileFormat = fileFormat;
        this.fileType = fileType;
        this.fileName = fileName;
        this.checksum = checksum;
        this.valid = true;
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

    public String getBatchJobId() {
        return batchJobId;
    }

    public void setBatchJobId(String batchJobId) {
        this.batchJobId = batchJobId;
    }

    @Override
    public String getResourceId() {
        return fileName;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getParentZipFileOrDirectory() {
        return parentZipFileOrDirectory;
    }

    public InputStream getFileStream() throws IOException {
        File parentFile = new File(getParentZipFileOrDirectory());
        if (parentFile.isDirectory()) {
            return new BufferedInputStream(FileUtils.openInputStream(new File(parentFile, getFileName())));
        } else {
            return ZipFileUtil.getInputStreamForFile(parentFile, getFileName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((batchJobId == null) ? 0 : batchJobId.hashCode());
        result = prime * result + ((checksum == null) ? 0 : checksum.hashCode());
        result = prime * result + ((fileFormat == null) ? 0 : fileFormat.hashCode());
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        result = prime * result + ((fileType == null) ? 0 : fileType.hashCode());
        result = prime * result + ((parentZipFileOrDirectory == null) ? 0 : parentZipFileOrDirectory.hashCode());
        result = prime * result + (valid ? 1231 : 1237);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        IngestionFileEntry other = (IngestionFileEntry) obj;
        if (batchJobId == null) {
            if (other.batchJobId != null) {
                return false;
            }
        } else if (!batchJobId.equals(other.batchJobId)) {
            return false;
        }
        if (checksum == null) {
            if (other.checksum != null) {
                return false;
            }
        } else if (!checksum.equals(other.checksum)) {
            return false;
        }
        if (fileFormat != other.fileFormat) {
            return false;
        }
        if (fileName == null) {
            if (other.fileName != null) {
                return false;
            }
        } else if (!fileName.equals(other.fileName)) {
            return false;
        }
        if (fileType != other.fileType) {
            return false;
        }
        if (parentZipFileOrDirectory == null) {
            if (other.parentZipFileOrDirectory != null) {
                return false;
            }
        } else if (!parentZipFileOrDirectory.equals(other.parentZipFileOrDirectory)) {
            return false;
        }
        if (valid != other.valid) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "IngestionFileEntry [parentZipFileOrDirectory=" + parentZipFileOrDirectory + ", fileFormat=" + fileFormat
                + ", fileType=" + fileType + ", fileName=" + fileName + ", checksum=" + checksum + ", valid=" + valid
                + ", batchJobId=" + batchJobId + "]";
    }
}

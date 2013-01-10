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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 */
public class LocalFileSystemLandingZone implements LandingZone, Serializable {

    private static final long serialVersionUID = 7441095255253233611L;

    protected File directory;

    public LocalFileSystemLandingZone() {
        //Empty default constructor
    }

    public LocalFileSystemLandingZone(File directory) {
        this.directory = directory;
    }

    /**
     * Return the absolute local path
     */
    @Override
    public String getLZId() {
        return getDirectory().getAbsolutePath();
    }

    /**
     * @return the directory
     */
    public File getDirectory() {
        return directory;
    }

    /**
     * @param directory
     *            the directory to set
     */
    public void setDirectory(File directory) {
        this.directory = directory;
    }

    /**
     * @return File object for the given fileName
     */
    @Override
    public File getFile(String fileName) {
        File f = FileUtils.getFile(this.directory, fileName);
        if (f.exists()) {
            return f;
        } else {
            return null;
        }
    }

    /**
     * @return File object for the newly-created file
     */
    @Override
    public File createFile(String fileName) throws IOException {
        File f = FileUtils.getFile(this.directory, fileName);
        if (f.exists()) {
            throw new IOException("file already exists: " + fileName);
        }
        f.createNewFile();
        return f;
    }

    /**
     * load file to local landing zone
     */
    public void loadFile(File file) throws IOException {

        File dest = FileUtils.getFile(this.directory, file.getName());
        // will overwrite if destination file exists
        FileUtils.copyFile(file, dest);
    }

    /**
     * Returns a java.io.File for a log file to be used to report BatchJob
     * status/progress.  The file will be created if it does not yet exist.
     *
     * @return File object
     */
    @Override
    public File getLogFile(String jobId) throws IOException {
        String fileName = "job-" + jobId + ".log";
        File f = FileUtils.getFile(this.directory, fileName);
        if (!f.exists()) {
            f.createNewFile();
        }
        return f;
    }

    /**
     * @return md5Hex string for the given File object
     */
    @Override
    public String getMd5Hex(File file) throws IOException {
        FileInputStream s = FileUtils.openInputStream(file);
        try {
            return DigestUtils.md5Hex(s);
        } finally {
            IOUtils.closeQuietly(s);
        }
    }

}

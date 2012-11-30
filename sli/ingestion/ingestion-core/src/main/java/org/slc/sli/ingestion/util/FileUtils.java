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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Utility class for dealing with files
 *
 * @author nbrown
 *
 */
@Component
public final class FileUtils {

    private FileUtils() { }

    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    /**
     * Get or create a sub-directory withing the provided parent directory
     *
     * @param parentDir
     * @param subDirName
     * @return
     */
    public static File getOrCreateSubDir(final File parentDir, final String subDirName) {
        String parentDirAbsPath = parentDir.getAbsolutePath();
        if (!parentDirAbsPath.endsWith("/")) {
            parentDirAbsPath += "/";
        }
        File subDir = new File(parentDirAbsPath + subDirName);
        if (!subDir.exists()) {
            if (!subDir.mkdir()) {
                LOG.error("Failed to mkdir sub dir: " + subDirName+ " under "+ parentDir.getPath());
            }
        }
        return subDir;
    }

    /**
     * Renames a file
     *
     * @param source
     * @param dest
     * @return boolean value whether the renaming was successful or not.
     */
    public static boolean renameFile(File source, File dest) {
        if (dest.exists() && !dest.delete()) {
            LOG.error("Failed to delete: " + dest.getPath());
        }
        return source.renameTo(dest);
    }

    /**
     * Returns when a file stops changing in size or timeout
     *
     * @param changingFile
     * @param pollInterval
     * @param timeout
     * @return boolean value whether the file has stopped changing in size.
     */
    public static boolean isFileDoneChanging(File changingFile, long interval, long timeout)
            throws InterruptedException {
        // timeout is in secs
        long clockTimeout = System.currentTimeMillis() + timeout;

        long prevLastModified = Long.MIN_VALUE;
        long prevLength = Long.MIN_VALUE;

        LOG.info("File monitored for change is " + changingFile.getAbsolutePath());

        while (System.currentTimeMillis() < clockTimeout) {
            long newLastModified = changingFile.lastModified();
            long newLength = changingFile.length();

            Thread.sleep(interval);

            LOG.info("prevLength " + prevLength + ", newLength " + newLength);

            if (prevLastModified == newLastModified && prevLength == newLength) {
                return true;
            }

            LOG.info(changingFile.getName() + " modification detected - waiting to process...");

            prevLastModified = newLastModified;
            prevLength = newLength;

        }

        return false;
    }

}

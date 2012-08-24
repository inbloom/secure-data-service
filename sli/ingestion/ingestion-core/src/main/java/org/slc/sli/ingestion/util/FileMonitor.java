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

/**
 * @author unavani
 *
 */
import java.io.File;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for monitoring changes in disk files.
 * Usage:
 *
 *    1. Implement the FileListener interface.
 *    2. Create a FileMonitor instance.
 *    3. Add the file(s) to listen for.
 *
 * fileUpdateComplete() will be called when updates are not found in the polling interval
 *
 */
public class FileMonitor {

    private HashMap<File, Long> fileModifiedTimeCollection;
    private FileListener listener;
    private long interval;

    private static final Logger LOG = LoggerFactory.getLogger(FileMonitor.class);

    private Object currentTag;

    public Object getTag() {
        return currentTag;
    }

    /**
     *
     *  Create a file monitor instance with specified polling interval.
     *  @param pollingInterval  Polling interval in milliseconds.
     */
    public FileMonitor(long pollingInterval, FileListener fileListener, Object tag) {

        currentTag = tag;
        fileModifiedTimeCollection = new HashMap<File, Long>();
        listener = fileListener;
        interval = pollingInterval;
    }

    /**
     * Add file to listen for. File may be any java.io.File (including a
     * directory) and may well be a non-existing file in the case where the
     * creating of the file is to be trapped.
     * <p>
     * More than one file can be listened for. When the specified file is
     * created, modified or deleted, listeners are notified.
     *
     * @param file  File to listen for.
     */
    public void addFile(File file) {

        if (!fileModifiedTimeCollection.containsKey(file)) {
            long modifiedTime = file.exists() ? file.lastModified() : -1;
            fileModifiedTimeCollection.put(file, new Long(modifiedTime));
        }
    }

    /**
     * Start monitoring for updates
     */
    public void startFileMonitor() {

        for (File file : fileModifiedTimeCollection.keySet()) {
            long lastModifiedTime = fileModifiedTimeCollection.get(file).longValue();
            long newModifiedTime  = file.exists() ? file.lastModified() : -1;
            // Check if file has changed
            if ((newModifiedTime != lastModifiedTime) || (newModifiedTime == -1)) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    LOG.error("DEBUG-UN: Exception occured during monitoring the file");
                    e.printStackTrace();
                }
            }
            LOG.error("DEBUG-UN: File Name-" + file.getName() + "File Length-" + file.length());
        }
        listener.fileUpdateComplete(FileMonitor.this);
    }

    /**
    *
    */
   public interface FileListener {
       public void fileUpdateComplete(Object sender);
   }

}
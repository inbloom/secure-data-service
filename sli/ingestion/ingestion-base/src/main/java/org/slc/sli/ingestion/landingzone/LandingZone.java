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
import java.io.IOException;

/**
 * 
 *
 */
public interface LandingZone {

    /**
     * Checks for a file in the landing zone with this name, and if found,
     * returns a java.io.File for it. Returns null if no file with the name can
     * be found.
     * 
     * @return File object
     */
    public File getFile(String fileName);

    /**
     * Creates a file in the landing zone with this name and returns a
     * java.io.File for it.
     * 
     * @return File object
     */
    public File createFile(String fileName) throws IOException;

    /**
     * Returns a java.io.File for a log file to be used to report BatchJob
     * status/progress.  The file will be created if it does not yet exist.
     * 
     * @return File object
     */
    public File getLogFile(String jobId) throws IOException;

    /**
     * @return md5Hex string for the given File object
     * @throws IOException
     */
    public String getMd5Hex(File file) throws IOException;
    
    /**
     * 
     * @return Location of LZ (e.g., folder, URL, etc.)
     */
    public String getLZId();
    
}
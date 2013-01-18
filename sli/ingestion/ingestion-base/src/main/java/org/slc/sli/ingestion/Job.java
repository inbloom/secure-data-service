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

package org.slc.sli.ingestion;

import java.util.List;
import java.util.Set;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;

/**
 * Interface for batch jobs
 *
 * @author nbrown
 *
 */
public interface Job {

    /**
     * @return the jobId
     */
    String getId();

    /**
     * @param key
     * @return
     */
    String getProperty(String key);

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    String getProperty(String key, String defaultValue);

    /**
     *
     * @return
     */
    Set<String> propertyNames();

    /**
     *
     * @param name
     * @param value
     */
    void setProperty(String name, String value);

    /**
     *
     * @return
     */
    List<IngestionFileEntry> getFiles();

    /**
     * Return the tenantId for this job.
     *
     * @return
     */
    String getTenantId();

}

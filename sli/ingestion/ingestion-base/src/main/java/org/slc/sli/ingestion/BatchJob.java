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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;

/**
 * Batch Job class.
 *
 * @author okrook
 * @deprecated
 */
@Deprecated
public final class BatchJob implements Serializable, Job {

    private static final long serialVersionUID = -340538024579162600L;

    /**
     * stores a globally unique ID for the Job
     */
    private final String id;

    /**
     * stores the date upon which the Job was created
     */
    private final Date creationDate;

    /**
     * holds references to the files involved in this Job
     */
    private List<IngestionFileEntry> files;

    /**
     * stores configuration properties for the Job
     */
    private Properties configProperties;

    /**
     * non-public constructor; use factory methods
     */
    private BatchJob(String id) {
        this.id = id;
        this.creationDate = new Date();
    }

    /**
     * Initialize a BatchJob with default settings for initialization
     *
     * @return BatchJob with default settings
     */
    public static Job createDefault() {
        return BatchJob.createDefault(null);
    }

    /**
     * Initialize a BatchJob with default settings for initialization
     *
     * @param filename
     *            string representation of incoming file
     * @return BatchJob with default settings
     */
    public static Job createDefault(String filename) {
        BatchJob job = new BatchJob(createId(filename));

        job.configProperties = new Properties();
        job.files = new ArrayList<IngestionFileEntry>();
        return job;
    }

    /**
     * generates a new unique ID
     */
    protected static String createId(String filename) {
        if (filename == null) {
            return UUID.randomUUID().toString();
        } else {
            return filename + "-" + UUID.randomUUID().toString();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.ingestion.Job#getCreationDate()
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.ingestion.Job#getFiles()
     */
    @Override
    public List<IngestionFileEntry> getFiles() {
        return files;
    }

    /**
     * set the files
     */
    public void setFiles(List<IngestionFileEntry> files) {
        this.files = files;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.ingestion.Job#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.ingestion.Job#getProperty(java.lang.String)
     */
    @Override
    public String getProperty(String key) {
        return configProperties.getProperty(key);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.ingestion.Job#getProperty(java.lang.String, java.lang.String)
     */
    @Override
    public String getProperty(String key, String defaultValue) {
        return configProperties.getProperty(key, defaultValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.ingestion.Job#propertyNames()
     */

    @Override
    public Set<String> propertyNames() {
        Set<String> propNames = new HashSet<String>();
        Enumeration<?> enumeration = configProperties.propertyNames();
        while (enumeration.hasMoreElements()) {
            propNames.add(enumeration.nextElement().toString());
        }
        return propNames;
    }

    @Override
    public void setProperty(String key, String value) {
        configProperties.setProperty(key, value);
    }

    @Override
    public String toString() {
        return "BatchJob [id=" + id + ", creationDate=" + creationDate + ", configProperties=" + configProperties + "]";
    }

    @Override
    public String getTenantId() {
        // TODO Auto-generated method stub
        return null;
    }

}

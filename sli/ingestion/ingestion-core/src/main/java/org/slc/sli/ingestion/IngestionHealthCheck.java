/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;



/**
 * Ingestion Health Check.
 *
 * @author unavani
 *
 */
public class IngestionHealthCheck {

    private BatchJobMongoDA batchJobMongoDA;
    private NewBatchJob newBatchJob;
    private String version;

    private static final Logger LOG = LoggerFactory.getLogger(IngestionHealthCheck.class);

    public String getVersion() {

        if (version == null || version.equals("")) {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("SLI_metadata.txt");
            BufferedReader br = null;
            if (in != null) {
                try {
                    br = new BufferedReader(new InputStreamReader(in));
                    String currentFileLine;
                    while ((currentFileLine = br.readLine()) != null)   {
                        Pattern versionPattern = Pattern.compile("^Version\\s*:\\s*(.*)\\s*$");
                        Matcher versionMatcher = versionPattern.matcher(currentFileLine);
                        if (versionMatcher.matches()) {
                            version = versionMatcher.group(1);
                        }
                    }
                } catch (IOException ioe) {
                    LOG.error("Error occured while obtaining the version: " + ioe.getLocalizedMessage());
                } finally {
                    IOUtils.closeQuietly(br);
                    IOUtils.closeQuietly(in);
                }
            }
        }
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public BatchJobMongoDA getBatchJobMongoDA() {
        return batchJobMongoDA;
    }

    public void setBatchJobMongoDA(BatchJobMongoDA batchJobMongoDA) {
        this.batchJobMongoDA = batchJobMongoDA;
    }

    public void updateLastActivity() {
        this.newBatchJob = batchJobMongoDA.findLatestBatchJob();
    }

    public String getStartTime() {
        //Start Time
        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        long startTimeLong = bean.getStartTime();
        return convertEpochTimeToDateTime(startTimeLong);
    }

    public String getLastActivity() {
        if (this.newBatchJob == null) {
            return "StartUp";
        } else {
            return newBatchJob.getId();
        }
    }

    public String getLastActivityTime() {
        if (this.newBatchJob == null) {
            return getStartTime();
        } else {
            DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            if (this.newBatchJob.getStatus().equals("CompletedSuccessfully")
                    || this.newBatchJob.getStatus().equals("CompletedWithErrors")) {
                return format.format(this.newBatchJob.getJobStopTimestamp());
            } else {
                return format.format(this.newBatchJob.getJobStartTimestamp());
            }
        }
    }

    private String convertEpochTimeToDateTime(long epochTime) {
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return format.format(epochTime);
    }

}

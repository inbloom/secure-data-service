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

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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

    public String getVersion() {
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
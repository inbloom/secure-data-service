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

package org.slc.sli.ingestion.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.util.BatchJobUtils;

/**
 * Model for the different stages of ingestion processing.
 *
 * @author dduran
 *
 */
public class Stage {

    public Stage() {
        // mongoTemplate requires this constructor.
    }

    private String jobId;

    private String stageName;
    private String stageDesc;
    private String status;
    private Date startTimestamp;
    private Date stopTimestamp;
    private long elapsedTime;
    private String sourceIp;
    private String hostname;
    private String processingInformation;

    private List<Metrics> metrics;

    public Stage(String stageName, String stageDesc) {
        this.stageName = stageName;
        this.stageDesc = stageDesc;
        this.metrics = new LinkedList<Metrics>();
    }

    public Stage(String stageName, String stageDesc, String status, Date startTimestamp, Date stopTimestamp,
            List<Metrics> metrics) {
        this.stageName = stageName;
        this.stageDesc = stageDesc;
        this.status = status;
        this.startTimestamp = new Date(startTimestamp.getTime());
        this.stopTimestamp = new Date(stopTimestamp.getTime());
        List<Metrics> theMetrics = metrics;
        if (theMetrics == null) {
            theMetrics = new LinkedList<Metrics>();
        }
        this.metrics = theMetrics;
    }

    public static Stage createAndStartStage(BatchJobStageType batchJobStageType, String stageDesc) {
        Stage stage = new Stage(batchJobStageType.getName(), stageDesc);
        stage.startStage();
        return stage;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStageName() {
        return stageName;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartTimestamp() {
        return new Date(startTimestamp.getTime());
    }

    public void setStartTimestamp(Date startTimestamp) {
        this.startTimestamp = new Date(startTimestamp.getTime());
    }

    public Date getStopTimestamp() {
        return new Date(stopTimestamp.getTime());
    }

    public void setStopTimestamp(Date stopTimestamp) {
        this.stopTimestamp = new Date(stopTimestamp.getTime());
    }

    public List<Metrics> getMetrics() {
        if (metrics == null) {
            metrics = new LinkedList<Metrics>();
        }
        return metrics;
    }

    public synchronized void addMetrics(Metrics metrics) {
        if (this.metrics == null) {
            this.metrics = new LinkedList<Metrics>();
        }
        this.metrics.add(metrics);
    }

    public void setMetrics(List<Metrics> metrics) {
        this.metrics = metrics;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public String getHostname() {
        return hostname;
    }

    public String getProcessingInformation() {
        return processingInformation;
    }

    public void setProcessingInformation(String processingInformation) {
        this.processingInformation = processingInformation;
    }

    public void update(String stageName, String status, Date startTimestamp, Date stopTimestamp) {
        if (stageName != null) {
            this.stageName = stageName;
        }
        if (status != null) {
            this.status = status;
        }
        if (startTimestamp != null) {
            this.startTimestamp = new Date(startTimestamp.getTime());
        }
        if (stopTimestamp != null) {
            this.stopTimestamp = new Date(stopTimestamp.getTime());
        }
    }

    public void startStage() {
        this.setStatus("running");
        this.setStartTimestamp(BatchJobUtils.getCurrentTimeStamp());
        this.sourceIp = BatchJobUtils.getHostAddress();
        this.hostname = BatchJobUtils.getHostName();
    }

    public void stopStage() {
        this.setStatus("finished");
        this.setStopTimestamp(BatchJobUtils.getCurrentTimeStamp());
        this.elapsedTime = calcElapsedTime();
    }

    public void addCompletedMetrics(Metrics metrics) {
        this.metrics.add(metrics);
    }

    private long calcElapsedTime() {
        if (stopTimestamp != null && startTimestamp != null) {
            return stopTimestamp.getTime() - startTimestamp.getTime();
        }
        return -1L;
    }

    public String getStageDesc() {
        return stageDesc;
    }

    public void setStageDesc(String stageDesc) {
        this.stageDesc = stageDesc;
    }

}

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

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import org.slc.sli.ingestion.util.BatchJobUtils;

/**
 *
 * @author dduran
 *
 */
@Document
public final class Error {

    @Indexed
    private String batchJobId = "";

    private String stageName = "";

    private String resourceId = "";

    private String sourceIp = "";

    private String hostname = "";

    private String recordIdentifier = "";

    private Date timestamp = new Date();

    private String severity = "";

    private String errorType = "";

    private String errorDetail = "";

    public Error() {
        // Mongo requires this
    }

    public Error(String batchJobId, String stageName, String resourceId, String sourceIp, String hostname,
            String recordIdentifier, Date timestamp, String severity, String errorType, String errorDetail) {
        this.setBatchJobId(batchJobId);
        this.setStageName(stageName);
        this.setResourceId(resourceId);
        this.setSourceIp(sourceIp);
        this.setHostname(hostname);
        this.setRecordIdentifier(recordIdentifier);
        this.setTimestamp(timestamp);
        this.setSeverity(severity);
        this.setErrorType(errorType);
        this.setErrorDetail(errorDetail);
    }

    // TODO: too many params. refactor.
    @SuppressWarnings("PMD.CustomAvoidThrowingRawExceptionTypes") // NOPMD - False positive CustomAvoidThrowingRawExceptionTypes
    public static Error createIngestionError(String ingestionJobId, String resourceId, String stageName,
            String sourceIp, String hostname, String recordIdentifier, String severity, String errorType,
            String errorDetail) {

        String theSourceIp = sourceIp;
        if (theSourceIp == null) {
            theSourceIp = BatchJobUtils.getHostAddress();
        }

        String theHostname = hostname;
        if (theHostname == null) {
            theHostname = BatchJobUtils.getHostName();
        }

        Error error = new Error(ingestionJobId, stageName, resourceId, theSourceIp, theHostname, recordIdentifier,
                BatchJobUtils.getCurrentTimeStamp(), severity, errorType, errorDetail);

        return error;
    }

    public String getBatchJobId() {
        return batchJobId;
    }

    public void setBatchJobId(String batchJobId) {
        if(batchJobId != null) {
            this.batchJobId = batchJobId;
        }
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        if(stageName != null) {
            this.stageName = stageName;
        }
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        if(resourceId != null) {
            this.resourceId = resourceId;
        }
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        if(sourceIp != null){
            this.sourceIp = sourceIp;
        }
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        if(hostname != null){
            this.hostname = hostname;
        }
    }

    public String getRecordIdentifier() {
        return recordIdentifier;
    }

    public void setRecordIdentifier(String recordIdentifier) {
        if(recordIdentifier != null){
            this.recordIdentifier = recordIdentifier;
        }
    }

    public Date getTimestamp() {
        return new Date(timestamp.getTime());
    }

    public void setTimestamp(Date timestamp) {
        if(timestamp != null){
            this.timestamp = new Date(timestamp.getTime());
        }
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        if(severity != null){
            this.severity = severity;
        }
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        if(errorType != null){
            this.errorType = errorType;
        }
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        if(errorDetail != null){
            this.errorDetail = errorDetail;
        }
    }

}

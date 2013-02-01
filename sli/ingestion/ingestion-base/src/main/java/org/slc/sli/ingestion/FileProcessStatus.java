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

import java.util.Map;

/**
 *
 */
public class FileProcessStatus {
    private String jobId;

    private long totalRecordCount;
    private Map<String, Long> duplicateCounts;

    private String outputFilePath;
    private String outputFileName;

    public String getJobId() {
        return this.jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public long getTotalRecordCount() {
        return this.totalRecordCount;
    }

    public void setTotalRecordCount(long totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
    }

    public String getOutputFileName() {
        return this.outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public String getOutputFilePath() {
        return this.outputFilePath;
    }

    public void setOutputFilePath(String outputFile) {
        this.outputFilePath = outputFile;
    }

    public Map<String, Long> getDuplicateCounts() {
        return duplicateCounts;
    }

    public void setDuplicateCounts(Map<String, Long> duplicateCounts) {
        this.duplicateCounts = duplicateCounts;
    }



}

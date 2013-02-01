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

import java.util.Map;

/**
 * Model for metrics of an ingestion job
 *
 * @author dduran
 *
 */
public class Metrics {

    private String resourceId;
    private long recordCount;
    private long errorCount;
    private Map<String, Long> duplicateCounts;

    public static Metrics newInstance(String resourceId) {
        Metrics metrics = new Metrics(resourceId);
        metrics.setRecordCount(0);
        metrics.setErrorCount(0);
        return metrics;
    }

    public Metrics() {
        // mongoTemplate requires this constructor.
    }

    public Metrics(String resourceId) {
        this.resourceId = resourceId;
    }

    public Metrics(String resourceId, long recordCount, long errorCount) {
        this.resourceId = resourceId;
        this.recordCount = recordCount;
        this.errorCount = errorCount;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(long recordCount) {
        this.recordCount = recordCount;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }

    public void update(String resourceId, long recordCount, long errorCount) {
        if (resourceId != null) {
            this.resourceId = resourceId;
        }
        this.recordCount = recordCount;
        this.errorCount = errorCount;
    }

    @Override
    public String toString() {
        return String.format("%s ->%d[%d]", this.resourceId, this.recordCount, this.errorCount);
    }

    public Map<String, Long> getDuplicateCounts() {
        return duplicateCounts;
    }

    public void setDuplicateCounts(Map<String, Long> duplicateCounts) {
        this.duplicateCounts = duplicateCounts;
    }

}

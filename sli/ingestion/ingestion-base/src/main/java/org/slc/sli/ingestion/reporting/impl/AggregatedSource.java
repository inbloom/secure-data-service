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

package org.slc.sli.ingestion.reporting.impl;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author slee
 *
 */
public class AggregatedSource extends JobSource {

    private List<JobSource> aggregatedJobSourceList = new ArrayList<JobSource>();

    public AggregatedSource(String batchJobId, String resourceId, String stageName) {
        super(batchJobId, resourceId, stageName);
    }

    public void addSource(JobSource jobSource) {
        aggregatedJobSourceList.add(jobSource);
    }

    @Override
    public String getUserFriendlyMessage() {
        String s = "";
        for (JobSource source : aggregatedJobSourceList) {
            s += source.getUserFriendlyMessage();
            s += "\n";
        }
        return s;
    }
}

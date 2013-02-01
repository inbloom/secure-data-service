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

    public AggregatedSource(String resourceId) {
        super(resourceId);
    }

    public void addSource(JobSource jobSource) {
        aggregatedJobSourceList.add(jobSource);
    }

    @Override
    public String getUserFriendlyMessage() {
        String s = "";
        for (int i=0; i<aggregatedJobSourceList.size(); ++i) {
            String ufm = aggregatedJobSourceList.get(i).getUserFriendlyMessage();
            // aggregate only non-empty messages
            // which are separate by ";" 
            if (ufm != null && ufm.length() > 0) {
                if (s.length() > 0) {
                    s += ";";
                }
                s += ufm;
            }
        }
        return s;
    }
}

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

package org.slc.sli.ingestion.reporting;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author npandey
 *
 */

public class SimpleSource implements Source {

    private String batchJobId;
    private String resourceId;
    private String stageName;
    private List<ElementLocationInfo> locationInfoList = new ArrayList<ElementLocationInfo>();

    public SimpleSource(String batchJobId, String resourceId, String stageName) {
        this.batchJobId = batchJobId;
        this.resourceId = resourceId;
        this.stageName = stageName;
    }

    @Override
    public String getBatchJobId() {
        return batchJobId;
    }

    @Override
    public String getResourceId() {
        return resourceId;
    }

    @Override
    public String getStageName() {
        return stageName;
    }

    @Override
    public void addElementLocationInfo(ElementLocationInfo info) {
        locationInfoList.add(info);
    }

    @Override
    public void clearElementLocationInfo() {
        locationInfoList.clear();
    }

    @Override
    public List<ElementLocationInfo> getElementLocationInfo() {
        return this.locationInfoList;
    }

}

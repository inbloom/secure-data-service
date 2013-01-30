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

package org.slc.sli.api.criteriaGenerator;

import java.util.List;

/**
 * @author pghosh
 * Class to hold the entity to filter on and the attributes for granular access date filter
 */
public class EntityFilterInfo {
    private String entityName;
    private String beginDateAttribute;
    private String endDateAttribute;
    private List<String> connectingEntityList;

    public List<String> getConnectingEntityList() {
        return connectingEntityList;
    }

    public void setConnectingEntityList(List<String> connectingEntityList) {
        this.connectingEntityList = connectingEntityList;
    }

    public String getSessionAttribute() {
        return sessionAttribute;
    }

    public void setSessionAttribute(String sessionAttribute) {
        this.sessionAttribute = sessionAttribute;
    }

    private String sessionAttribute;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getBeginDateAttribute() {
        return beginDateAttribute;
    }

    public void setBeginDateAttribute(String beginDateAttribute) {
        this.beginDateAttribute = beginDateAttribute;
    }

    public String getEndDateAttribute() {
        return endDateAttribute;
    }

    public void setEndDateAttribute(String endDateAttribute) {
        this.endDateAttribute = endDateAttribute;
    }
}

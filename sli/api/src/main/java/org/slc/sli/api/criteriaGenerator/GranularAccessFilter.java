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

import org.slc.sli.domain.NeutralQuery;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pghosh
 * Date: 1/15/13
 */
public class GranularAccessFilter {
    private String entityName;
    private NeutralQuery neutralQuery;

    public List<String> getConnectedEntityList() {
        return connectedEntityList;
    }

    public void setConnectedEntityList(List<String> connectedEntityList) {
        this.connectedEntityList = connectedEntityList;
    }

    private List<String> connectedEntityList;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public NeutralQuery getNeutralQuery() {
        return neutralQuery;
    }

    public void setNeutralQuery(NeutralQuery neutralQuery) {
        this.neutralQuery = neutralQuery;
    }
}

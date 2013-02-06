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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.domain.CalculatedData;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.reporting.ElementSource;

/**
 * Adapter for Entity
 *
 * @author dduran
 *
 */
public class NeutralRecordEntity implements Entity, Resource, ElementSource {

    private NeutralRecord neutralRecord;
    private long recordNumberInFile;

    public NeutralRecordEntity() {
        this(null, 0);
    }

    public NeutralRecordEntity(NeutralRecord neutralRecord) {
        this(neutralRecord, 0);
    }

    /**
     * @author tshewchuk 1/12/2010 (PI2 US311)
     */
    public NeutralRecordEntity(NeutralRecord neutralRecord, long recordNumber) {
        this.neutralRecord = neutralRecord;
        this.recordNumberInFile = recordNumber;  // Added 2/2/2012 by Thomas Shewchuk
    }

    @Override
    public String getType() {
        return neutralRecord.getRecordType();
    }

    @Override
    public String getEntityId() {
        return neutralRecord.getRecordId();
    }

    @Override
    public Map<String, Object> getBody() {
        return neutralRecord.getAttributes();
    }

    /**
     * @author tshewchuk 2/2/2010 (PI3 US811)
     */
    @Override
    public Map<String, Object> getMetaData() {
        return neutralRecord.getMetaData();
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     */
    public void setAttributeField(String fieldName, Object fieldValue) {
        this.neutralRecord.setAttributeField(fieldName, fieldValue);
    }

    public Object getLocalId() {
        return neutralRecord.getLocalId();
    }

    /**
     * @author tshewchuk 2/3/2010 (PI3 US811)
     */
    public Map<String, Object> getLocalParentIds() {
        return neutralRecord.getLocalParentIds();
    }

    /**
     * @author tshewchuk 1/12/2010 (PI3 US811)
     */
    public long getRecordNumberInFile() {
        return recordNumberInFile;
    }

    /**
     * @author tshewchuk 2/1/2010 (PI3 US811)
     */
    public boolean isAssociation() {
        return neutralRecord.isAssociation();
    }

    public NeutralRecord getNeutralRecord() {
        return neutralRecord;
    }

    public void setNeutralRecord(NeutralRecord neutralRecord) {
        this.neutralRecord = neutralRecord;
    }

    @Override
    public String getElementType() {
        return neutralRecord.getRecordType();
    }

    @Override
    public int getVisitBeforeLineNumber() {
        return neutralRecord.getVisitBeforeLineNumber();
    }

    @Override
    public int getVisitBeforeColumnNumber() {
        return neutralRecord.getVisitBeforeColumnNumber();
    }

    public int getVisitAfterLineNumber() {
        return neutralRecord.getVisitAfterLineNumber();
    }

    public int getVisitAfterColumnNumber() {
        return neutralRecord.getVisitAfterColumnNumber();
    }

    @Override
    public String getStagedEntityId() {
        return neutralRecord.getRecordId();
    }

    @Override
    public String toString() {
        StringBuilder entity = new StringBuilder();
        entity.append("[ Neutral Record Entity: ");
        entity.append("{type: ").append(getType()).append(" }, ");
        entity.append("{entity id: ").append(getEntityId()).append(" }, ");
        entity.append("{local id: ").append(getLocalId()).append(" }, ");
        entity.append("{local parent ids: ").append(getLocalParentIds()).append(" }, ");
        entity.append("{body: ").append(getBody()).append(" }, ");
        entity.append("{metadata: ").append(getMetaData()).append(" }, ");
        entity.append("{record number: ").append(getRecordNumberInFile()).append(" }");
        entity.append(" ]");
        return entity.toString();
    }

    @Override
    public CalculatedData<String> getCalculatedValues() {
        return new CalculatedData<String>();
    }

    @Override
    public CalculatedData<Map<String, Integer>> getAggregates() {
        return new CalculatedData<Map<String, Integer>>();
    }

    @Override
    public Map<String, List<Entity>> getEmbeddedData() {
        return new HashMap<String, List<Entity>>();
    }

    @Override
    public Map<String, List<Map<String, Object>>> getDenormalizedData() {
        return new HashMap<String, List<Map<String, Object>>>();
    }

    @Override
    public String getResourceId() {
        return neutralRecord.getResourceId();
    }
}

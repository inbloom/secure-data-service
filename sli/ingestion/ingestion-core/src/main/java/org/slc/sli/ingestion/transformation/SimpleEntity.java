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

package org.slc.sli.ingestion.transformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.domain.CalculatedData;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.Resource;
import org.slc.sli.ingestion.reporting.ElementSource;

/**
 * @author okrook
 *
 */
public class SimpleEntity implements Entity, Resource, ElementSource {
    private String type;
    private String entityId;
    private String stagedEntityId;
    private Map<String, Object> body;
    private Map<String, Object> metaData;
    private long recordNumber;
    private String sourceFile;
    private int visitBeforeLineNumber;
    private int visitBeforeColumnNumber;
    private int visitAfterLineNumber;
    private int visitAfterColumnNumber;

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

    @Override
    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }

    public long getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(long recordNumber) {
        this.recordNumber = recordNumber;
    }

    @Override
    public String getElementType() {
        return type;
    }

    @Override
    public int getVisitBeforeLineNumber() {
        return visitBeforeLineNumber;
    }

    public void setVisitBeforeLineNumber(int visitBeforeLineNumber) {
        this.visitBeforeLineNumber = visitBeforeLineNumber;
    }

    @Override
    public int getVisitBeforeColumnNumber() {
        return visitBeforeColumnNumber;
    }

    public void setVisitBeforeColumnNumber(int visitBeforeColumnNumber) {
        this.visitBeforeColumnNumber = visitBeforeColumnNumber;
    }

    public int getVisitAfterLineNumber() {
        return visitAfterLineNumber;
    }

    public void setVisitAfterLineNumber(int visitAfterLineNumber) {
        this.visitAfterLineNumber = visitAfterLineNumber;
    }

    public int getVisitAfterColumnNumber() {
        return visitAfterColumnNumber;
    }

    public void setVisitAfterColumnNumber(int visitAfterColumnNumber) {
        this.visitAfterColumnNumber = visitAfterColumnNumber;
    }

    @Override
    public String getStagedEntityId() {
        return stagedEntityId;
    }

    public void setStagedEntityId(String stagedEntityId) {
        this.stagedEntityId = stagedEntityId;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    @Override
    public String toString() {
        StringBuilder entity = new StringBuilder();
        entity.append("[ Simple Entity: ");
        entity.append("{type: ").append(getType()).append("}, ");
        entity.append("{entity id: ").append(getEntityId()).append("}, ");
        entity.append("{staged id: ").append(getStagedEntityId()).append("},");
        entity.append("{body: ").append(getBody()).append("}, ");
        entity.append("{metadata: ").append(getMetaData()).append("}, ");
        entity.append("{record number: ").append(getRecordNumber()).append("}");
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
        return getSourceFile();
    }
}

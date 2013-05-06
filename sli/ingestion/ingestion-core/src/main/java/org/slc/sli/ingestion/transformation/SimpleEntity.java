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
import org.slc.sli.ingestion.ActionVerb;
import org.slc.sli.ingestion.NeutralRecord;
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
    private String sourceFile;
    private int visitBeforeLineNumber;
    private int visitBeforeColumnNumber;
    private int visitAfterLineNumber;
    private int visitAfterColumnNumber;
    private Map<String, String> actionAttributes;

    public static final String FIELD_UUID = "UUID";
    public static final String DELETE_AFFECTED_COUNT = "DELETEAFFECTEDCOUNT";

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

    public ActionVerb getAction( ) {
        ActionVerb result = ActionVerb.NONE;
        if ( metaData != null && metaData.containsKey( NeutralRecord.KEY_ACTION) ) {
            result = ActionVerb.valueOf( (String) metaData.get(NeutralRecord.KEY_ACTION));
        }
     return ( result) ;
    }

    public String getUUID( ) {
        String result = null;
        if ( metaData != null && metaData.containsKey( FIELD_UUID ) ) {
            result = (String) metaData.get( FIELD_UUID);
        }
        return( result );
    }
    public void setUUID( String id ) {
        if( metaData == null ) {
            metaData = new HashMap< String, Object>();
        }
        metaData.put( FIELD_UUID, id);
    }

    public void setAction( ActionVerb action) {
        if( metaData == null ) {
            metaData = new HashMap< String, Object>();
        }
        metaData.put( NeutralRecord.KEY_ACTION, action.toString());
    }

    public void removeAction( ) {
        if( metaData != null ) {

            if( this.getMetaData().containsKey( NeutralRecord.KEY_ACTION ) ) {
                this.getMetaData().remove(NeutralRecord.KEY_ACTION );
            }
        }
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

    public String getDeleteAffectedCount() {
        String result = null;
        if ( metaData != null && metaData.containsKey(DELETE_AFFECTED_COUNT) ) {
            result = (String) metaData.get(DELETE_AFFECTED_COUNT);
        }
        return( result );
    }

    public void setDeleteAffectedCount(String id) {
        if( metaData == null ) {
            metaData = new HashMap< String, Object>();
        }
        metaData.put(DELETE_AFFECTED_COUNT, id);
    }


    @Override
    public Map<String, List<Entity>> getContainerData() {
        return new HashMap<String, List<Entity>>();
    }

    @Override
    public void hollowOut() {
        // override super implementation with empty implementation
    }

    public void setActionAttributes(Map<String, String> actionAttributes) {
        this.actionAttributes = actionAttributes;
    }

    public Map<String, String> getActionAttributes() {
        return  actionAttributes;
    }

    public boolean doForceDelete() {
        String force = actionAttributes.get("Force");
        if (force == null) {
            return true;    // default
        } else if (force.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean doLogViolations() {
        String logViolations = actionAttributes.get("LogViolations");
        if (logViolations == null) {
            return true;    // default
        } else if (logViolations.equals("true")) {
            return true;
        } else {
            return false;
        }
    }
}

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
package org.slc.sli.search.transform.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.connector.SourceDatastoreConnector;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.slc.sli.search.transform.EntityConverter;

public class AssessmentPeriodDescriptorEntityConverter implements EntityConverter {
    
    public final static String ASSESSMENT = "assessment";
    public final static String ASSESSMENT_PERIOD_DESCRIPTOR = "assessmentPeriodDescriptor";
    public final static String ASSESSMENT_PERIOD_DESCRIPTOR_ID = "assessmentPeriodDescriptorId";

    private SourceDatastoreConnector sourceDatastoreConnector;
    private IndexConfigStore indexConfigStore;

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> treatment(String index, Action action, Map<String, Object> entityMap) {
        List<Map<String, Object>> entities = new ArrayList<Map<String, Object>>();

        // return empty list as there is no need to delete anything for assessmentPeriodDescriptor
        if (action == Action.DELETE) {
            return entities;
        }
        
        // this is from sarje update event, that body is null
        if (entityMap.get("body") == null) {
            DBObject apdQuery = new BasicDBObject("_id", entityMap.get("_id"));
            IndexConfig apdConfig = indexConfigStore.getConfig(ASSESSMENT_PERIOD_DESCRIPTOR);
            DBCursor cursor = sourceDatastoreConnector.getDBCursor(index, ASSESSMENT_PERIOD_DESCRIPTOR, apdConfig.getFields(), apdQuery);
            if (cursor.hasNext()) {
                Map<String, Object> apdEntity = cursor.next().toMap();
                entityMap.put("body", apdEntity.get("body"));
            }
        }
        
        DBObject query = new BasicDBObject("body." + ASSESSMENT_PERIOD_DESCRIPTOR_ID, entityMap.get("_id"));
        IndexConfig config = indexConfigStore.getConfig(ASSESSMENT);
        
        DBCursor cursor = sourceDatastoreConnector.getDBCursor(index, ASSESSMENT, config.getFields(), query);
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            Map<String, Object> assessmentMap = obj.toMap();
            ((Map<String, Object>) assessmentMap.get("body")).put(ASSESSMENT_PERIOD_DESCRIPTOR, entityMap.get("body"));
            ((Map<String, Object>) assessmentMap.get("body")).remove(ASSESSMENT_PERIOD_DESCRIPTOR_ID);
            entities.add(assessmentMap);
        }
            
        return entities;
    }
    
    public void setIndexConfigStore(IndexConfigStore indexConfigStore) {
        this.indexConfigStore = indexConfigStore;
    }

    public void setSourceDatastoreConnector(SourceDatastoreConnector sourceDatastoreConnector) {
        this.sourceDatastoreConnector = sourceDatastoreConnector;
    }
}

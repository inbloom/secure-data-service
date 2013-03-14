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

import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.connector.SourceDatastoreConnector;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.slc.sli.search.transform.EntityConverter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class AssessmentEntityConverter implements EntityConverter {
    
    public final static String ASSESSMENT = "assessment";
    public static final String ASSESSMENT_PERIOD_DESCRIPTOR = "assessmentPeriodDescriptor";
    public static final String ASSESSMENT_PERIOD_DESCRIPTOR_ID = "assessmentPeriodDescriptorId";
    public static final String ASSESSMENT_FAMILY_COLLECTION = "assessmentFamily";
    public static final String ASSESSMENT_FAMILY_REFERENCE = "assessmentFamilyReference";
    public static final String ASSESSMENT_FAMILY_HIERARCHY = "assessmentFamilyHierarchyName";
    
    private SourceDatastoreConnector sourceDatastoreConnector;
    private IndexConfigStore indexConfigStore;
    
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> treatment(String index, Action action, Map<String, Object> entityMap) {
        
        Map<String, Object> assessmentEntityMap = entityMap;
        
        // no need to denormalize anything if it's a delete operation
        if (action != Action.DELETE) {
            Map<String, Object> body = (Map<String, Object>) assessmentEntityMap.get("body");
            
            // from sarge update event, body is null
            if (body == null) {
                DBObject assessmentQuery = new BasicDBObject("_id", assessmentEntityMap.get("_id"));
                IndexConfig assessmentConfig = indexConfigStore.getConfig(ASSESSMENT);
                DBCursor cursor = sourceDatastoreConnector.getDBCursor(index, ASSESSMENT, assessmentConfig.getFields(),
                        assessmentQuery);
                if (cursor.hasNext()) {
                    assessmentEntityMap = cursor.next().toMap();
                    body = (Map<String, Object>) assessmentEntityMap.get("body");
                }
            }
            
            if (body != null) {
                body.put(ASSESSMENT_PERIOD_DESCRIPTOR, extractPeriodDescriptor(index, body));
                body.put(ASSESSMENT_FAMILY_HIERARCHY, extractFamilyHierarchy(index, body));
            }
        }
        
        return Arrays.asList(assessmentEntityMap);
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractPeriodDescriptor(String index, Map<String, Object> body) {
        String assessmentPeriodDescriptorId = (String) body.remove(ASSESSMENT_PERIOD_DESCRIPTOR_ID);
        if (assessmentPeriodDescriptorId != null) {
            IndexConfig apdConfig = indexConfigStore.getConfig(ASSESSMENT_PERIOD_DESCRIPTOR);
            DBObject query = new BasicDBObject("_id", assessmentPeriodDescriptorId);
            DBCursor cursor = sourceDatastoreConnector.getDBCursor(index, ASSESSMENT_PERIOD_DESCRIPTOR,
                    apdConfig.getFields(), query);
            if (cursor.hasNext()) {
                DBObject obj = cursor.next();
                Map<String, Object> assessmentPeriodDescriptor = obj.toMap();
                return (Map<String, Object>) assessmentPeriodDescriptor.get("body");
            }
        }
        return null;
    }
    
    private String extractFamilyHierarchy(String index, Map<String, Object> body) {
        Deque<String> familyTitles = new LinkedList<String>();
        String assessmentFamilyReference = (String) body.remove(ASSESSMENT_FAMILY_REFERENCE);
        Set<String> checkedReferences = new HashSet<String>();
        while (assessmentFamilyReference != null && !checkedReferences.contains(assessmentFamilyReference)) {
            checkedReferences.add(assessmentFamilyReference);
            DBCursor cursor = sourceDatastoreConnector.getDBCursor(index, ASSESSMENT_FAMILY_COLLECTION, Arrays.asList(
                    "body.assessmentFamilyReference", "body.assessmentFamilyTitle"), new BasicDBObject("_id",
                    assessmentFamilyReference));
            if (cursor != null && cursor.hasNext()) {
                DBObject family = cursor.next();
                if (family != null && family.containsField("body")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> familyBody = (Map<String, Object>) family.get("body");
                    familyTitles.addFirst((String) familyBody.get("assessmentFamilyTitle"));
                    assessmentFamilyReference = (String) familyBody.get("assessmentFamilyReference");
                }
            }
        }
        return StringUtils.join(familyTitles, ".");
    }
    
    public void setIndexConfigStore(IndexConfigStore indexConfigStore) {
        this.indexConfigStore = indexConfigStore;
    }
    
    public void setSourceDatastoreConnector(SourceDatastoreConnector sourceDatastoreConnector) {
        this.sourceDatastoreConnector = sourceDatastoreConnector;
    }
}

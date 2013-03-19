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

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.apache.commons.lang3.StringUtils;

import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.connector.SourceDatastoreConnector;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.slc.sli.search.transform.EntityConverter;

public class AssessmentEntityConverter implements EntityConverter {
    
    public static final String ASSESSMENT = "assessment";
    public static final String ASSESSMENT_PERIOD_DESCRIPTOR = "assessmentPeriodDescriptor";
    public static final String ASSESSMENT_PERIOD_DESCRIPTOR_ID = "assessmentPeriodDescriptorId";
    public static final String ASSESSMENT_FAMILY_COLLECTION = "assessmentFamily";
    public static final String ASSESSMENT_FAMILY_REFERENCE = "assessmentFamilyReference";
    public static final String ASSESSMENT_FAMILY_HIERARCHY = "assessmentFamilyHierarchyName";
    public static final String ASSESSMENT_FAMILY_TITLE = "assessmentFamilyTitle";
    public static final List<String> FAMILY_FIELDS = Arrays.asList("body.assessmentFamilyReferece",
            "body.assessmentFamilyTitle");
    
    private SourceDatastoreConnector sourceDatastoreConnector;
    private IndexConfigStore indexConfigStore;
    
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> treatment(String index, Action action, Map<String, Object> entityMap) {
        
        Map<String, Object> assessmentEntityMap = entityMap;
        
        // no need to denormalize anything if it's a delete operation
        if (action != Action.DELETE) {
            Map<String, Object> body = getBody(index, assessmentEntityMap);
            
            if (body != null) {
                body.put(ASSESSMENT_PERIOD_DESCRIPTOR, extractPeriodDescriptor(index, body));
                body.put(ASSESSMENT_FAMILY_HIERARCHY, extractFamilyHierarchy(index, body));
                assessmentEntityMap.put("body", body);
            }
        }
        
        return Arrays.asList(assessmentEntityMap);
    }

    protected Map<String, Object> getBody(String index, Map<String, Object> assessmentEntityMap) {
        IndexConfig assessmentConfig = indexConfigStore.getConfig(ASSESSMENT);
        List<String> fields = assessmentConfig.getFields();
        return getBody(index, assessmentEntityMap, ASSESSMENT, fields);
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getBody(String index, Map<String, Object> entityMap, String type, List<String> fields) {
        Map<String, Object> body = (Map<String, Object>) entityMap.get("body");
        
        // from sarge update event, body is null
        if (body == null) {
            DBObject assessmentQuery = new BasicDBObject("_id", entityMap.get("_id"));
            DBCursor cursor = sourceDatastoreConnector.getDBCursor(index, type, fields,
                    assessmentQuery);
            if (cursor.hasNext()) {
                Map<String, Object> newAssessmentEntityMap = cursor.next().toMap();
                body = (Map<String, Object>) newAssessmentEntityMap.get("body");
            }
        }
        return body;
    }
    
    @Override
    // No action needs to be updated
    public Action convertAction(Action action) {
        return action;
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
            DBCursor cursor = sourceDatastoreConnector.getDBCursor(index, ASSESSMENT_FAMILY_COLLECTION, FAMILY_FIELDS, new BasicDBObject("_id",
                    assessmentFamilyReference));
            if (cursor != null && cursor.hasNext()) {
                DBObject family = cursor.next();
                if (family != null && family.containsField("body")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> familyBody = (Map<String, Object>) family.get("body");
                    familyTitles.addFirst((String) familyBody.get(ASSESSMENT_FAMILY_TITLE));
                    assessmentFamilyReference = (String) familyBody.get(ASSESSMENT_FAMILY_REFERENCE);
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

    protected SourceDatastoreConnector getSourceDatastoreConnector() {
        return sourceDatastoreConnector;
    }

    protected IndexConfigStore getIndexConfigStore() {
        return indexConfigStore;
    }


}

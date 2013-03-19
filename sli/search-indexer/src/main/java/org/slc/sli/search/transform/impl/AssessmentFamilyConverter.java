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

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.slc.sli.search.entity.IndexEntity.Action;

public class AssessmentFamilyConverter extends AssessmentEntityConverter {
    
    @Override
    public List<Map<String, Object>> treatment(String index, Action action, Map<String, Object> entityMap) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        String id = entityMap.get("_id").toString();
        String familyName = action == Action.DELETE ? "" : getName(index, entityMap).toString();
        List<Map<String, Object>> assessments = addAssessmentsForFamilyId(index, id, familyName);
        for (Map<String, Object> assessment : assessments) {
            result.addAll(super.treatment(index, action, assessment));
        }
        
        return result;
    }
    
    /**
     * Add the assessments matching the given family id to the list to be updated
     * 
     * @param dbname the name of the db
     * @param id
     *            the family id
     * @param familyName
     *            the family name
     * @param results
     *            the list of results
     */
    @SuppressWarnings("unchecked")
    protected List<Map<String, Object>> addAssessmentsForFamilyId(String dbname, String id, String familyName) {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        DBObject referenceQuery = BasicDBObjectBuilder.start().add("body.assessmentFamilyReference", id).get();
        DBCursor assessmentCursor = getSourceDatastoreConnector().getDBCursor(dbname, ASSESSMENT,
                getIndexConfigStore().getConfig(ASSESSMENT).getFields(), referenceQuery);
        while (assessmentCursor.hasNext()) {
            DBObject obj = assessmentCursor.next();
            Map<String, Object> assessment = obj.toMap();
            Map<String, Object> body = (Map<String, Object>) assessment.get("body");
            body.remove(ASSESSMENT_FAMILY_REFERENCE);
            body.put(ASSESSMENT_FAMILY_HIERARCHY, familyName);
            assessment.put("body", body);
            results.add(assessment);
        }
        DBCursor familyCursor = getSourceDatastoreConnector().getDBCursor(dbname, ASSESSMENT_FAMILY_COLLECTION, FAMILY_FIELDS, referenceQuery);
        while (familyCursor.hasNext()) {
            DBObject subFamily = familyCursor.next();
            String subId = (String) subFamily.get("_id");
            String subName = (String) ((Map<String, Object>) subFamily.get("body")).get("assessmentFamilyTitle");
            results.addAll(addAssessmentsForFamilyId(dbname, subId, familyName + "." + subName));
        }
        return results;
    }
    
    @SuppressWarnings("unchecked")
    private StringBuilder getName(String index, Map<String, Object> entityMap) {
        Map<String, Object> body = getBody(index, entityMap, ASSESSMENT_FAMILY_COLLECTION, FAMILY_FIELDS);
        String parentRef = (String) body.get(ASSESSMENT_FAMILY_REFERENCE);
        StringBuilder builder = new StringBuilder();
        if (parentRef != null && !parentRef.equals("")) {
            DBCursor cursor = getSourceDatastoreConnector().getDBCursor(index, ASSESSMENT_FAMILY_COLLECTION, FAMILY_FIELDS,
                    BasicDBObjectBuilder.start().add("_id", parentRef).get());
            if (cursor.hasNext()) {
                builder = getName(index, cursor.next().toMap());
                builder.append(".");
            }
        }
        return builder.append(body.get("assessmentFamilyTitle").toString());
    }
    
    @Override
    public Action convertAction(Action action) {
        return action == Action.DELETE ? Action.UPDATE : action;
    }
    
}

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

import org.slc.sli.search.entity.IndexEntity.Action;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class AssessmentFamilyConverter extends AssessmentEntityConverter {
    
    @Override
    public List<Map<String, Object>> treatment(String index, Action action, Map<String, Object> entityMap) {
        List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
        String id = entityMap.get("_id").toString();
        String familyName = action == Action.DELETE ? "" : getName(index, entityMap);
        results.addAll(addAssessmentsForFamilyId(id, familyName));
        return results;
    }

    /**
     * Add the assessessments matching the given family id to the list to be updated
     * 
     * @param id the family id
     * @param familyName the family name
     * @param results the list of results
     */
    @SuppressWarnings("unchecked")
    protected List<Map<String, Object>> addAssessmentsForFamilyId(String id, String familyName) {
        List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
        DBCursor cursor = getSourceDatastoreConnector().getDBCursor("assessment",
                getIndexConfigStore().getConfig("assessment").getFields(),
                BasicDBObjectBuilder.start().add("body.assessmentFamilyReference", id).get());
        while(cursor.hasNext()) {
            DBObject obj = cursor.next();
            Map<String, Object> assessment = obj.toMap();
            Map<String, Object> body = (Map<String, Object>) assessment.get("body");
            body.put("assessmentFamilyHierarchyName", familyName);
            assessment.put("body", body);
            results.add(assessment);
        }
        return results;
    }
    
    private String getName(String index, Map<String, Object> entityMap){
        Map<String, Object> body = getBody(index, entityMap);
        return body.get("assessmentFamilyTitle").toString();
    }
    
    @Override
    public Action convertAction(Action action) {
        return action;
    }
    
}

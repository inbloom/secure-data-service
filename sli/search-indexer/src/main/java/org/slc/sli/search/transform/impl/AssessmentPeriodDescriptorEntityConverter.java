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

import static org.slc.sli.common.constants.EntityNames.ASSESSMENT;
import static org.slc.sli.common.constants.EntityNames.ASSESSMENT_PERIOD_DESCRIPTOR;
import static org.slc.sli.common.constants.ParameterConstants.ASSESSMENT_PERIOD_DESCRIPTOR_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.entity.IndexEntity.Action;

public class AssessmentPeriodDescriptorEntityConverter extends AssessmentEntityConverter {
    
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> treatment(String index, Action action, Map<String, Object> entityMap) {
        List<Map<String, Object>> entities = new ArrayList<Map<String, Object>>();
        Map<String, Object> body = null;

        if (action != Action.DELETE) {
            IndexConfig apdConfig = getIndexConfigStore().getConfig(ASSESSMENT_PERIOD_DESCRIPTOR);
            body = getBody(index, entityMap, ASSESSMENT_PERIOD_DESCRIPTOR, apdConfig.getFields());
        }

        DBObject query = new BasicDBObject("body." + ASSESSMENT_PERIOD_DESCRIPTOR_ID, entityMap.get("_id"));
        IndexConfig config = getIndexConfigStore().getConfig(ASSESSMENT);
        
        DBCursor cursor = getSourceDatastoreConnector().getDBCursor(index, ASSESSMENT, config.getFields(), query);
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            Map<String, Object> assessmentMap = obj.toMap();
            if (action != Action.DELETE) {
                ((Map<String, Object>) assessmentMap.get("body")).put(ASSESSMENT_PERIOD_DESCRIPTOR, body);
            }
            ((Map<String, Object>) assessmentMap.get("body")).remove(ASSESSMENT_PERIOD_DESCRIPTOR_ID);
            entities.addAll(super.treatment(index, convertAction(action), assessmentMap));
        }

        return entities;
    }

    
    @Override
    public Action convertAction(Action action) {
        // delete action will need to changed into an update action on assessments
        return action == Action.DELETE ? Action.INDEX: action;
    }

}

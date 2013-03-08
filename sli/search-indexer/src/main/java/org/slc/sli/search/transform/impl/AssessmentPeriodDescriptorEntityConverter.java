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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.entity.IndexEntity.Action;

public class AssessmentPeriodDescriptorEntityConverter extends GenericEntityConverter {
    
    @SuppressWarnings("unchecked")
    public List<IndexEntity> convert(String index, Action action, Map<String, Object> entityMap, boolean decrypt) {
        // *********HACK need to remove this soon***********
        TenantContext.setTenantId("Midgar");

        List<IndexEntity> entities = new ArrayList<IndexEntity>();

        // no need to delete anything for assessmentPeriodDescriptor
        if (action == Action.DELETE) {
            return entities;
        }
        
        DBObject query = new BasicDBObject("body.assessmentPeriodDescriptorId", entityMap.get("_id"));
        IndexConfig config = indexConfigStore.getConfig("assessment");
        
        DBCursor cursor = sourceDatastoreConnector.getDBCursor("assessment", config.getFields(), query);
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            Map<String, Object> assessmentMap = obj.toMap();
            ((Map<String, Object>) assessmentMap.get("body")).put("assessmentPeriodDescriptor",
                    Arrays.asList(entityMap.get("body")));
            ((Map<String, Object>) assessmentMap.get("body")).remove("assessmentPeriodDescriptorId");
            entities.addAll(super.convert(index, action, assessmentMap, decrypt));
        }
            
        return entities;
    }
    
}

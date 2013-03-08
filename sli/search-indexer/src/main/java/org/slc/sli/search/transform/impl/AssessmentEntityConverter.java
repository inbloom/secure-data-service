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
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.entity.IndexEntity.Action;

public class AssessmentEntityConverter extends GenericEntityConverter {

    @SuppressWarnings("unchecked")
    public List<IndexEntity> convert(String index, Action action, Map<String, Object> entityMap, boolean decrypt) {
        //*********HACK need to remove this soon***********
        TenantContext.setTenantId("Midgar");
        
        // no need to denormalize anything if it's a delete operation
        if (action == Action.DELETE) {
            return super.convert(index, action, entityMap, decrypt);
        }
        
        Map<String, Object> body = (Map<String, Object>) entityMap.get("body");
        if (body != null) {
            String assessmentPeriodDescriptorId = (String) body.remove("assessmentPeriodDescriptorId");
            if (assessmentPeriodDescriptorId != null) {
                IndexConfig apdConfig = indexConfigStore.getConfig("assessmentPeriodDescriptor");
                DBObject query = new BasicDBObject("_id", assessmentPeriodDescriptorId);
                DBCursor cursor = sourceDatastoreConnector.getDBCursor("assessmentPeriodDescriptor", apdConfig.getFields(), query);
                if (cursor.hasNext()) {
                    DBObject obj = cursor.next();
                    Map<String, Object> assessmentPeriodDescriptor = obj.toMap();
                    ((Map<String, Object>) entityMap.get("body")).put("assessmentPeriodDescriptor", Arrays.asList(assessmentPeriodDescriptor.get("body")));
                }
            }
        }
       
        return super.convert(index, action, entityMap, decrypt);
    }

}

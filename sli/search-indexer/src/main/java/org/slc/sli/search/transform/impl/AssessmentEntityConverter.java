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

        Map<String, Object> metaData = (Map<String, Object>) entityMap.get("metaData");
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

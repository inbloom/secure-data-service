package org.slc.sli.api.resources.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Utils for entity resource unit tests
 *
 * @author chung
 *
 */
public class ResourceTestUtil {
    public static Map<String, Object> createTestEntity(String resourceName) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", "1");
        entity.put("field2", 2);
        entity.put(getResourceIdName(resourceName), 1234);
        return entity;
    }

    public static Map<String, Object> createTestUpdateEntity(String resourceName) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", 8);
        entity.put("field2", 2);
        entity.put(getResourceIdName(resourceName), 1234);
        return entity;
    }

    public static Map<String, Object> createTestSecondaryEntity(String resourceName) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", 5);
        entity.put("field2", 6);
        entity.put(getResourceIdName(resourceName), 5678);
        return entity;
    }

    public static Map<String, Object> createTestAssociationEntity(String resourceName, String ep1Name, String ep1Id, String ep2Name, String ep2Id) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", "1");
        entity.put("field2", 2);
        entity.put(getResourceIdName(resourceName), 1234);
        entity.put(getResourceIdName(ep1Name), ep1Id);
        entity.put(getResourceIdName(ep2Name), ep2Id);
        return entity;
    }

    public static String getResourceIdName(String resourceName) {
        String resId = resourceName.substring(0, 1).toLowerCase() + resourceName.substring(1);
        resId = resId.replace("Resource", "Id");
        return resId;
    }
}

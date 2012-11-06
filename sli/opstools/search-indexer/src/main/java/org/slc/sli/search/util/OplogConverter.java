/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
package org.slc.sli.search.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity converter for OpLog
 * 
 * @author tosako
 * 
 */

@SuppressWarnings("unchecked")
public class OplogConverter {
    public static final String OPERATOR_PUSH = "$push";
    public static final String OPERATOR_PUSH_ALL = "$pushAll";
    public static final String OPERATOR_PULL = "$pull";
    public static final String OPERATOR_PULL_ALL = "$pullAll";
    public static final String OPERATOR_SET = "$set";
    public static final String OPERATOR_ADD_TO_SET = "$addToSet";
    public static final String OPERATOR_EACH = "$each";

    public static final String INDEX = "_index";

    /**
     * Convert OpLog entry to Entity for Insert
     * 
     * @param oplogEntry
     * @return
     */
    public static Map<String, Object> getEntityForInsert(Map<String, Object> oplogEntry) {
        return (Map<String, Object>) oplogEntry.get("o");
    }

    /**
     * Convert OpLog entry to Entity for Update
     * 
     * @param oplogEntry
     * @return
     */
    public static Map<String, Object> getEntityForUpdate(Map<String, Object> oplogEntry) {
        Meta meta = getMeta(oplogEntry);
        Map<String, Object> o2 = (Map<String, Object>) oplogEntry.get("o2");
        String id = (String) o2.get("_id");
        Map<String, Object> o = (Map<String, Object>) oplogEntry.get("o");

        // merge data into entity json (id, type, metadata.tenantId, body)
        Map<String, Object> entityMap = new HashMap<String, Object>();
        entityMap.put("_id", id);
        entityMap.put(INDEX, meta.getIndex());
        entityMap.put("type", meta.getType());

        // convert to index entity object
        return merge(o, entityMap);
    }

    /**
     * Convert OpLog entry to Entity for Delete
     * 
     * @param oplogEntry
     * @param type
     * @return
     */
    public static Map<String, Object> getEntityForDelete(Map<String, Object> oplogEntry) {
        Meta meta = getMeta(oplogEntry);
        Map<String, Object> o = (Map<String, Object>) oplogEntry.get("o");
        String id = (String) o.get("_id");

        // merge data into entity json (id, type, metadata.tenantId)
        Map<String, Object> entityMap = new HashMap<String, Object>();
        entityMap.put("_id", id);
        entityMap.put("type", meta.getType());
        return entityMap;
    }

    public static Map<String, Object> merge(Map<String, Object> o, Map<String, Object> entityMap) {
        if (o.containsKey(OPERATOR_SET)) {
            mergeSet(o, entityMap);
        } else if (o.containsKey(OPERATOR_PUSH)) {
            mergePush(o, entityMap, (Map<String, Object>) o.get(OPERATOR_PUSH));
        } else if (o.containsKey(OPERATOR_PUSH_ALL)) {
            mergePush(o, entityMap, (Map<String, Object>) o.get(OPERATOR_PUSH_ALL));
        } else if (o.containsKey(OPERATOR_PULL)) {
            mergePull(o, entityMap, (Map<String, Object>) o.get(OPERATOR_PULL));
        } else if (o.containsKey(OPERATOR_PULL_ALL)) {
            mergePull(o, entityMap, (Map<String, Object>) o.get(OPERATOR_PULL_ALL));
        } else if (o.containsKey(OPERATOR_ADD_TO_SET)) {
            mergeAddToSet(o, entityMap);
        }
        return entityMap;
    }

    public static Meta getMeta(Map<String, Object> opLogMap) {
        String[] meta = ((String) opLogMap.get("ns")).split("\\.");
        return new Meta(meta[0], meta[1]);
    }

    // should support with or without $each
    private static void mergeAddToSet(Map<String, Object> o, Map<String, Object> entityMap) {
        // without $each

        // TODO: need to test
        Map<String, Object> sets = (Map<String, Object>) o.get(OPERATOR_ADD_TO_SET);
        for (String setField : sets.keySet()) {
            List<String> fieldChain = NestedMapUtil.getPathLinkFromDotNotation(setField);
            Object obj = NestedMapUtil.get(fieldChain, entityMap);
            Object setValue = sets.get(setField);
            // Map<String,Object> setValueMap = convertFromJasonToMap(setV);
            List<Object> inspectingObject = convertAsList((setValue instanceof Map && ((Map<String, Object>) setValue)
                    .containsKey(OPERATOR_EACH)) ? ((Map<String, Object>) setValue).get(OPERATOR_EACH) : setValue);

            List<Object> addingObject = filterObject(obj, inspectingObject);
            push(fieldChain, addingObject, entityMap);
        }
    }

    private static void mergeSet(Map<String, Object> o, Map<String, Object> entityMap) {
        Map<String, Object> updates = (Map<String, Object>) o.get(OPERATOR_SET);
        for (String updateField : updates.keySet()) {
            List<String> fieldChain = NestedMapUtil.getPathLinkFromDotNotation(updateField);
            NestedMapUtil.put(fieldChain, updates.get(updateField), entityMap);
        }
    }

    private static void mergePush(Map<String, Object> o, Map<String, Object> entityMap, Map<String, Object> push) {
        // TODO: does it work?
        for (String pushField : push.keySet()) {
            List<String> fieldChain = NestedMapUtil.getPathLinkFromDotNotation(pushField);
            push(fieldChain, convertAsList(push.get(pushField)), entityMap);
        }
    }

    private static void mergePull(Map<String, Object> o, Map<String, Object> entityMap, Map<String, Object> pull) {
        // TODO: implement

    }

    private static List<Object> convertAsList(Object obj) {
        List<Object> list = new ArrayList<Object>();
        if (obj.getClass().isArray()) {
            list.addAll(Arrays.asList(obj));
        } else if (obj instanceof List) {
            list.addAll((List<Object>) obj);
        } else {
            list.add(obj);
        }
        return list;
    }

    private static List<Object> filterObject(Object obj, List<Object> list) {
        if (list.isEmpty() || obj == null)
            return list;
        if (!obj.getClass().isArray())
            list.clear();
        else {
            list.removeAll(Arrays.asList(obj));
        }
        return list;
    }

    private static void push(List<String> fieldChain, List<Object> addingObject, Map<String, Object> entityMap) {
        Object obj = NestedMapUtil.get(fieldChain, entityMap);
        if (obj != null && obj.getClass().isArray()) {
            List<Object> list = new ArrayList<Object>();
            list.addAll(Arrays.asList(obj));
            list.addAll(addingObject);
            NestedMapUtil.put(fieldChain, list, entityMap);
        } else if (obj == null) {
            NestedMapUtil.put(fieldChain, addingObject, entityMap);
        } else {
            StringBuilder sb = new StringBuilder();
            for (String field : fieldChain) {
                if (sb.length() != 0)
                    sb.append(".");
                sb.append(field);
            }
            throw new IllegalArgumentException("The object is not an array: " + sb.toString());
        }
    }

    public static boolean isInsert(Map<String, Object> oplog) {
        return "i".equals(oplog.get("op"));
    }

    public static boolean isUpdate(Map<String, Object> oplog) {
        return "u".equals(oplog.get("op"));
    }

    public static boolean isDelete(Map<String, Object> oplog) {
        return "d".equals(oplog.get("op"));
    }

    public static class Meta {
        private final String index;
        private final String type;

        public Meta(String index, String type) {
            super();
            this.index = index;
            this.type = type;
        }

        public String getIndex() {
            return index;
        }

        public String getType() {
            return type;
        }
    }

}

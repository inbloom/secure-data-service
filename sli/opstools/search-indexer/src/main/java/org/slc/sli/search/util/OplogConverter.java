package org.slc.sli.search.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class OplogConverter {

    public static Map<String, Object> getEntity(Map<String, Object> oplogEntry) {
        Map<String, Object> o2 = (Map<String, Object>) oplogEntry.get("o2");
        String id = (String) o2.get("_id");
        Map<String, Object> o = (Map<String, Object>) oplogEntry.get("o");

        // merge data into entity json (id, type, metadata.tenantId, body)
        Map<String, Object> entityMap = new HashMap<String, Object>();
        entityMap.put("_id", id);
        String[] meta = ((String) oplogEntry.get("ns")).split("\\.");
        entityMap.put("_index", meta[0]);
        entityMap.put("type", meta[1]);

        // convert to index entity object
        return merge(o, entityMap);
    }

    public static Map<String, Object> merge(Map<String, Object> o, Map<String, Object> entityMap) {
        if (o.containsKey("$set")) {
            mergeSet(o, entityMap);
        } else if (o.containsKey("$push")) {
            mergePush(o, entityMap, (Map<String, Object>) o.get("$push"));
        } else if (o.containsKey("$pushAll")) {
            mergePush(o, entityMap, (Map<String, Object>) o.get("$pushAll"));
        } else if (o.containsKey("$pull")) {
            mergePull(o, entityMap, (Map<String, Object>) o.get("$pull"));
        } else if (o.containsKey("$pullAll")) {
            mergePull(o, entityMap, (Map<String, Object>) o.get("$pulAlll"));
        } else if (o.containsKey("$addToSet")) {
            mergeAddToSet(o, entityMap);
        }
        return entityMap;
    }

    // should support with or without $each
    private static void mergeAddToSet(Map<String, Object> o, Map<String, Object> entityMap) {
        // without $each

        // TODO: need to test
        Map<String, Object> sets = (Map<String, Object>) o.get("$addToSet");
        for (String setField : sets.keySet()) {
            List<String> fieldChain = NestedMapUtil.getPathLinkFromDotNotation(setField);
            Object obj = NestedMapUtil.get(fieldChain, entityMap);
            Object setValue = sets.get(setField);
            // Map<String,Object> setValueMap = convertFromJasonToMap(setV);
            List<Object> inspectingObject = convertAsList((setValue instanceof Map && ((Map) setValue)
                    .containsKey("$each")) ? ((Map) setValue).get("$each") : setValue);

            List<Object> addingObject = filterObject(obj, inspectingObject);
            push(fieldChain, addingObject, entityMap);
        }
    }

    private static void mergeSet(Map<String, Object> o, Map<String, Object> entityMap) {
        Map<String, Object> updates = (Map<String, Object>) o.get("$set");
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
            list.addAll((List) obj);
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

}

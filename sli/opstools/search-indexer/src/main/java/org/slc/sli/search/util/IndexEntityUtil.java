package org.slc.sli.search.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.entity.IndexEntity.Action;

public class IndexEntityUtil {
    private final static String NEW_LINE = "\n";
    
    private static ObjectMapper mapper = new ObjectMapper();
    
    public static Map<String, Object> getEntity(String entity) {
        try {
            return mapper.readValue(entity, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new SearchIndexerException("Unable to convert to document");
        }
    }
    
    public static String getBulkIndexJson(List<IndexEntity> docs) {
        StringBuilder sb = new StringBuilder();
        for (IndexEntity ie: docs) {
            toBulkIndexJson(sb, ie);
        }
        return sb.toString();
    }
    
    public static String getBulkGetJson(List<IndexEntity> docs) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"docs\": [");
        for (IndexEntity ie: docs) {
            addHeader(sb, ie);
            sb.append(",").append(NEW_LINE);
        }
        return sb.delete(sb.length() - 2, sb.length() - 1).append("]}").toString();
    }
    
    private static String addHeader(StringBuilder sb, IndexEntity ie) {
        sb.append("{").append("\"_index\":\"").append(ie.getIndex()).append("\", \"_type\":\"").
                append(ie.getType()).append("\",\"_id\":\"").append(ie.getId()).append("\"");
        if (ie.getParentId() != null) {
            sb.append(", \"_parent\":\"").append(ie.getParentId()).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }
    
    private static String toBulkIndexJson(StringBuilder sb, IndexEntity ie) {
        sb.append("{\"").append(ie.getActionValue()).append("\":");
        addHeader(sb, ie);
        sb.append("}").append(NEW_LINE);
        sb.append(getBodyForIndex(ie.getBody())).append(NEW_LINE);
        return sb.toString();
    }
    
    public static String getBody(Action action, Map<String, Object> entity) {
        return getBodyForIndex(entity);
    }
    
    public static String getBodyForIndex(Map<String, Object> entity) {
        try {
            return mapper.writeValueAsString(entity);
        } catch (Exception e) {
            throw new SearchIndexerException("Unable to convert to body", e);
        }
    }
    
    private static String getBodyForUpdate(Map<String, Object> entity) {
        try {
//            {
//                "script" : "ctx._source.context = context",
//                    "params" : {
//                        context : {
//                "schoolId":"2012zj-c110606b-010b-11e2-993f-68a86d0b3330"
//                        }
//                    }
//                }
            Map<String, Object> flatMap = NestedMapUtil.toFlatMap(entity);
            StringBuilder script = new StringBuilder();
            for (String key: flatMap.keySet()) {
                script.append("ctx._source.").append(key).append("=").append(mapper.writeValueAsString(flatMap.get(key))).append(";");
            }
            Map<String, Object> updateJsonMap = new HashMap<String, Object>();
            updateJsonMap.put("script", script.toString());
            return mapper.writeValueAsString(updateJsonMap);
        } catch (Exception e) {
            throw new SearchIndexerException("Unable to convert to body", e);
        }
    }
    
    
    public static IndexEntity getIndexEntity(Map<String, Object> entity) {
        return new IndexEntity((String)entity.get("_index"), (String)entity.get("_type"), (String)entity.get("_id"));
    }
    
}

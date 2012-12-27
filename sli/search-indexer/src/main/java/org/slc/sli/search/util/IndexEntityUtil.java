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

import java.util.Collection;
import java.util.HashMap;
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
            throw new SearchIndexerException("Unable to convert to document", e);
        }
    }
    
    public static String getBulkIndexJson(Collection<IndexEntity> docs) {
        StringBuilder sb = new StringBuilder();
        for (IndexEntity ie: docs) {
            toBulkIndexJson(sb, ie);
        }
        return sb.toString();
    }
    
    public static String getBulkDeleteJson(Collection<IndexEntity> docs) {
        StringBuilder sb = new StringBuilder();
        for (IndexEntity ie: docs) {
            toBulkDeleteJson(sb, ie);
        }
        return sb.toString();
    }
    
    public static String getBulkGetJson(Collection<IndexEntity> docs) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"docs\": [");
        for (IndexEntity ie: docs) {
            addHeader(sb, ie);
            sb.append(",").append(NEW_LINE);
        }
        return sb.delete(sb.length() - 2, sb.length() - 1).append("]}").toString();
    }
    
    private static void addHeader(StringBuilder sb, IndexEntity ie) {
        sb.append("{").append("\"_index\":\"").append(ie.getIndex()).append("\", \"_type\":\"").
                append(ie.getType()).append("\",\"_id\":\"").append(ie.getId()).append("\"");
        sb.append("}");
    }
    
    private static void toBulkIndexJson(StringBuilder sb, IndexEntity ie) {
        sb.append("{\"").append(ie.getActionValue()).append("\":");
        addHeader(sb, ie);
        sb.append("}").append(NEW_LINE);
        sb.append(getBodyForIndex(ie.getBody())).append(NEW_LINE);
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
    
    public static void toBulkDeleteJson(StringBuilder sb, IndexEntity ie) {
        sb.append("{\"").append(ie.getActionValue()).append("\":");
        addHeader(sb, ie);
        sb.append("}").append(NEW_LINE);
    }
    
    public static String toUpdateJson(IndexEntity ie) {
        try {
//          {
//              "script" : "ctx._source.context = context",
//                  "params" : {
//                      context : {
//              "schoolId":"2012zj-c110606b-010b-11e2-993f-68a86d0b3330"
//                      }
//                  }
//              }
          StringBuilder script = new StringBuilder();
          for (String key: ie.getBody().keySet()) {
              script.append("ctx._source.").append(key).append("=").append(key).append(";");
          }
          Map<String, Object> updateJsonMap = new HashMap<String, Object>();
          updateJsonMap.put("script", script.toString());
          updateJsonMap.put("params", ie.getBody());
          return mapper.writeValueAsString(updateJsonMap);
      } catch (Exception e) {
    	  // throwing a new exception to avoid PII
          throw new SearchIndexerException("Unable to convert to body");//NOPMD
      }
    }
    
    public static IndexEntity getIndexEntity(Map<String, Object> entity) {
        return new IndexEntity((String)entity.get("_index"), (String)entity.get("_type"), (String)entity.get("_id"));
    }
}

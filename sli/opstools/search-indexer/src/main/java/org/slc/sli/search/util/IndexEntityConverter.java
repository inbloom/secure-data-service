package org.slc.sli.search.util;

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.search.entity.IndexEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * IndexEntityConverter handles conversion of IndexEntity to and from json
 * 
 */
@Component
public class IndexEntityConverter {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private ObjectMapper mapper = new ObjectMapper();
    private final static String NEW_LINE = "\n";
    
    @Autowired
    EntityEncryption entityEncryption;
    // decrypt records flag
    private boolean decrypt = true;
    
    public String toIndexJson(IndexEntity ie) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"").append(ie.getActionValue()).append(
                "\":{").append("\"_index\":\"").append(ie.getIndex()).append("\", \"_type\":\"").
                append(ie.getType()).append("\",\"_id\":\"").append(ie.getId()).append("\"}}").append(NEW_LINE);
        sb.append(ie.getBody()).append(NEW_LINE);
        return sb.toString();
    }
    
    @SuppressWarnings("unchecked")
    public IndexEntity fromEntityJson(String entity) {
        try {
            Map<String, Object> entityMap = mapper.readValue(entity, new TypeReference<Map<String, Object>>() {});           
            Map<String, Object> decryptedMap = 
                    decrypt ? entityEncryption.decrypt((String)entityMap.get("type"), (Map<String, Object>) entityMap.get("body")): entityMap;
            return new IndexEntity(
                    ((String)((Map<String, Object>)entityMap.get("metaData")).get("tenantId")).toLowerCase(), 
                    (String)entityMap.get("type"), 
                    (String)entityMap.get("_id"), 
                    mapper.writeValueAsString(decryptedMap));
            
        } catch (Exception e) {
             throw new SearchIndexerException("Unable to convert entity", e);
        } 
    }
    
    public void setDecrypt(boolean decrypt) {
        this.decrypt = decrypt;
    }
    
}

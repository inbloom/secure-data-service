package org.slc.sli.search.transform;

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.transform.impl.GenericTransformer;
import org.slc.sli.search.util.SearchIndexerException;

/**
 * IndexEntityConverter handles conversion of IndexEntity to and from json
 * 
 */
public class IndexEntityConverter {

    private ObjectMapper mapper = new ObjectMapper();
    private final static String NEW_LINE = "\n";
    
    private EntityEncryption entityEncryption;
    private IndexConfigStore indexConfigStore;
    private GenericTransformer transformer = new GenericTransformer();
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
            Map<String, Object> body = (Map<String, Object>) entityMap.get("body");
            Map<String, Object> metaData = (Map<String, Object>) entityMap.get("metaData");
            String type = (String)entityMap.get("type");
            // decrypt body if needed
            Map<String, Object> decryptedMap = decrypt ? entityEncryption.decrypt(type, body): body;
            // get tenantId
            String indexName = ((String)metaData.get("tenantId")).toLowerCase();
            IndexConfig config = indexConfigStore.getConfig(type);
            //re-assemble entity map
            entityMap.put("body", decryptedMap);
            // transform the entities
            transformer.transform(config, entityMap);
            String id = (String)entityMap.get("_id");
            entityMap.put("_id", id);
            decryptedMap.put("metaData", entityMap.remove("metaData"));
            String indexType = config.getIndexType() == null ? type : config.getIndexType();
            return new IndexEntity(indexName, indexType, id, mapper.writeValueAsString(entityMap.get("body")));
            
        } catch (Exception e) {
             throw new SearchIndexerException("Unable to convert entity", e);
        } 
    }
    
    public void setDecrypt(boolean decrypt) {
        this.decrypt = decrypt;
    }
    public void setEntityEncryption(EntityEncryption entityEncryption) {
        this.entityEncryption = entityEncryption;
    }
    
    public void setIndexConfigStore(IndexConfigStore indexConfigStore) {
        this.indexConfigStore = indexConfigStore;
    }
}

package org.slc.sli.search.transform;

import java.util.Map;

import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.slc.sli.search.transform.impl.GenericTransformer;
import org.slc.sli.search.util.IndexEntityUtil;
import org.slc.sli.search.util.NestedMapUtil;
import org.slc.sli.search.util.SearchIndexerException;

/**
 * IndexEntityConverter handles conversion of IndexEntity to and from json
 * 
 */
public class IndexEntityConverter {
    private EntityEncryption entityEncryption;
    private IndexConfigStore indexConfigStore;
    private final GenericTransformer transformer = new GenericTransformer();
    // decrypt records flag
    private boolean decrypt = true;
    
    public IndexEntity fromEntityJson(String entity) {
        return fromEntityJson(Action.INDEX, entity);
    }
    
    @SuppressWarnings("unchecked")
    public IndexEntity fromEntityJson(Action action, String entity) {
        try {
            Map<String, Object> entityMap = IndexEntityUtil.getEntity(entity);       
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
            String parent = (config.getParentField() != null) ? 
                    (String)NestedMapUtil.get(config.getParentField(), entityMap) : null;
            entityMap.remove("type");
            entityMap.remove("_id");
            entityMap.remove("metaData");
            //decryptedMap.put("metaData", entityMap.remove("metaData"));
            String indexType = config.getIndexType() == null ? type : config.getIndexType();
            action = config.isChildDoc() ?  IndexEntity.Action.UPDATE : action;
            return new IndexEntity(action, indexName, indexType, id, parent, (Map<String, Object>)entityMap.get("body"));
            
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

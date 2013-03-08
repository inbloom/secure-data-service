package org.slc.sli.search.transform.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.connector.SourceDatastoreConnector;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.slc.sli.search.transform.EntityConverter;
import org.slc.sli.search.util.SearchIndexerException;

public class GenericEntityConverter implements EntityConverter {

    protected final GenericTransformer transformer = new GenericTransformer();

    protected SourceDatastoreConnector sourceDatastoreConnector;
    protected EntityEncryption entityEncryption;
    protected IndexConfigStore indexConfigStore;

    @SuppressWarnings("unchecked")
    @Override
    public List<IndexEntity> convert(String index, Action action, Map<String, Object> entityMap, boolean decrypt) {

        List<IndexEntity> entities = new ArrayList<IndexEntity>();
        try {
            Map<String, Object> body = (Map<String, Object>) entityMap
                    .get("body");
            Map<String, Object> metaData = (Map<String, Object>) entityMap
                    .get("metaData");
            String type = (String) entityMap.get("type");

            // decrypt body if needed
            Map<String, Object> decryptedMap = null;
            if (body != null) {
                decryptedMap = decrypt ? entityEncryption.decrypt(type, body)
                        : body;
            }
            // re-assemble entity map
            entityMap.put("body", decryptedMap);
            // get tenantId
            String indexName = (index == null) ? ((String) metaData
                    .get("tenantId")).toLowerCase() : index.toLowerCase();

            IndexConfig config = indexConfigStore.getConfig(type);

            // filter out
            if (!transformer.isMatch(config, entityMap)) {
                return entities;
            }

            // transform the entities
            transformer.transform(config, entityMap);

            String id = (String) entityMap.get("_id");
            String indexType = config.getIndexType() == null ? type : config
                    .getIndexType();
            Action finalAction = config.isChildDoc() ? IndexEntity.Action.UPDATE
                    : action;
            body = (Map<String, Object>) entityMap.get("body");
            if (body == null && action != Action.DELETE) {
                return entities;
            }
            
            entities.add(new IndexEntity(finalAction, indexName, indexType, id,
                    body));

        } catch (Exception e) {
            throw new SearchIndexerException("Unable to convert entity", e);
        }

        return entities;
    }

    /* only for unit tests */
    public void setEntityEncryption(EntityEncryption entityEncryption) {
        this.entityEncryption = entityEncryption;
    }
    
    public void setSourceDatastoreConnector(SourceDatastoreConnector sourceDatastoreConnector) {
        this.sourceDatastoreConnector = sourceDatastoreConnector;
    }
    
    public void setIndexConfigStore(IndexConfigStore indexConfigStore) {
        this.indexConfigStore = indexConfigStore;
    }
}

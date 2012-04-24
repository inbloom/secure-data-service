package org.slc.sli.ingestion.dal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.query.Order;

/** Mongo indexes manager, which loads the indexes from the json configuration file.
 *
 * @author tke
 *
 */

public final class MongoIndexManager {
    private static final Logger LOG = LoggerFactory.getLogger(MongoIndexManager.class);

    private static Map<String, List<IndexDefinition>> collectionIndexes = null;

    private String indexRootDir;

    /**
     * Init function used by Spring. Load indexes map for all collections
     */
    public void init() {
        List<MongoIndexConfig> mongoIndexConfigs = new ArrayList<MongoIndexConfig>();
        IndexResourcePatternResolver indexResolver = new IndexResourcePatternResolver();

        mongoIndexConfigs = indexResolver.findAllIndexes(indexRootDir);
        collectionIndexes = new HashMap<String, List<IndexDefinition>>();

        try {
            for (MongoIndexConfig mongoIndexConfig : mongoIndexConfigs) {
                List<IndexDefinition> indexList;
                String collectionName = mongoIndexConfig.getCollection();
                if (!collectionIndexes.containsKey(collectionName)) {
                    indexList = new ArrayList<IndexDefinition>();
                } else {
                    indexList = collectionIndexes.get(collectionName);
                }
                    indexList.add(createIndexDefinition(mongoIndexConfig.getIndexFields(), collectionName));
                    collectionIndexes.put(collectionName, indexList);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to create the indexes");
        }
    }

    /**Create index definition from buffered reader
     *
     * @param fields : the fields read from the config files
     * @param name : the name of the index
     * @return
     * @throws IOException
     */
    private IndexDefinition createIndexDefinition(List<Map<String, String>> fields, String name) throws IOException {
        Index index = new Index();

        for (Map<String, String> field : fields) {
            index.on(field.get("name"), field.get("position").equals("1") ? Order.ASCENDING : Order.DESCENDING);
        }
        index.named(name);
        return index;
    }

    /**Set indexes for one collection
    *
    * @param template : mongo template to set the index
    * @param collection: the collection to be ensureIndex'ed
    * @param batchJobId
    */
    public void ensureIndex(MongoTemplate template, String collection, String batchJobId) {
        if (!collectionIndexes.containsKey(collection)) {
            LOG.error("No indexes found for " + collection + ". Skipping...");
        }

       for (IndexDefinition index : collectionIndexes.get(collection)) {
           String collectionName = collection + "_" + batchJobId;
           if (!template.collectionExists(collectionName)) {
               template.createCollection(collectionName);
           }
           try {
               template.ensureIndex(index, collectionName);
           } catch (Exception e) {
               //Mongo indexes are not ensured
               LOG.error("Failed to create mongo indexes");
           }
       }
   }

    /**Set indexes for all the collections configured
     *
     * @param template : mongo template to set the index
     */
    public void ensureIndex(MongoTemplate template, String batchJobId) {
        Set<String> collections = collectionIndexes.keySet();

        for (String collection : collections) {
            ensureIndex(template, collection, batchJobId);
        }
    }

    public String getIndexRootDir() {
        return indexRootDir;
    }

    public void setIndexRootDir(String indexRootDir) {
        this.indexRootDir = indexRootDir;
    }

    public static Map<String, List<IndexDefinition>> getCollectionIndexes() {
        return collectionIndexes;
    }

    public static void setCollectionIndexes(Map<String, List<IndexDefinition>> collectionIndexes) {
        MongoIndexManager.collectionIndexes = collectionIndexes;
    }
}

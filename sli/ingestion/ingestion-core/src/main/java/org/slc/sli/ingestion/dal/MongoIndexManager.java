package org.slc.sli.ingestion.dal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.query.Order;

import org.slc.sli.domain.Repository;

/**
 * Mongo indexes manager, which loads the indexes from the json configuration file.
 *
 * @author tke
 *
 */

public final class MongoIndexManager {
    private static final Logger LOG = LoggerFactory.getLogger(MongoIndexManager.class);

    private Map<String, List<IndexDefinition>> collectionIndexes = new HashMap<String, List<IndexDefinition>>();

    private String indexRootDir;

    /**
     * Init function used by Spring. Load indexes map for all collections
     */
    public void init() {
        IndexResourcePatternResolver indexResolver = new IndexResourcePatternResolver();

        List<MongoIndexConfig> mongoIndexConfigs = indexResolver.findAllIndexes(indexRootDir);

        int count = 0;
        try {
            for (MongoIndexConfig mongoIndexConfig : mongoIndexConfigs) {
                List<IndexDefinition> indexList;
                String collectionName = mongoIndexConfig.getCollection();

                if (!collectionIndexes.containsKey(collectionName)) {
                    indexList = new ArrayList<IndexDefinition>();
                } else {
                    indexList = collectionIndexes.get(collectionName);
                }

                if (mongoIndexConfig.getOptions() != null && !mongoIndexConfig.getOptions().isEmpty()) {
                    indexList.add(createIndexDefinition(mongoIndexConfig.getIndexFields(),
                            mongoIndexConfig.getOptions(), count++));
                } else {
                    indexList.add(createIndexDefinition(mongoIndexConfig.getIndexFields(), count++));
                }
                collectionIndexes.put(collectionName, indexList);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to create the indexes");
        }
    }

    /**
     * Create index definition from buffered reader
     *
     * @param fields
     *            : the fields read from the config files
     * @param indexName
     *            : the name of the index.
     * @return
     */
    private static final IndexDefinition createIndexDefinition(List<Map<String, String>> fields, int indexName) {
        Index index = new Index();

        for (Map<String, String> field : fields) {
            index.on(field.get("name"), field.get("order").equals("1") ? Order.ASCENDING : Order.DESCENDING);
        }

        index.named(String.valueOf(indexName));
        return index;
    }

    /**
     * Create unique index definition from parsed map.
     *
     * @param fields
     *            fields read in from config (json) files.
     * @param indexName
     *            name of the index (counter).
     * @return unique index definition.
     */
    private static final IndexDefinition createIndexDefinition(List<Map<String, String>> fields,
            Map<String, String> options, int indexName) {
        Index index = new Index();

        for (Map<String, String> field : fields) {
            index.on(field.get("name"), field.get("order").equals("1") ? Order.ASCENDING : Order.DESCENDING);
        }

        if (options.containsKey("unique") && options.get("unique").equals("true")) {
            index.unique();
        }

        index.named(String.valueOf(indexName));
        return index;
    }

    /**
     * Set indexes for one collection
     *
     * @param template
     *            : mongo template to set the index
     * @param collection
     *            : the collection to be ensureIndex'ed
     * @param batchJobId
     */
    public void ensureIndex(Repository<?> repository, String collection) {
        if (!repository.collectionExists(collection)) {
            repository.createCollection(collection);
        }

        if (!collectionIndexes.containsKey(collection)) {
            return;
        }

        for (IndexDefinition index : collectionIndexes.get(collection)) {

            try {
                repository.ensureIndex(index, collection);
            } catch (Exception e) {
                LOG.error("Failed to create mongo indexes");
            }
        }
    }

    /**
     * Set indexes for all the collections configured
     *
     * @param template
     *            : mongo template to set the index
     */
    public void ensureIndex(Repository<?> repository) {
        Set<String> collections = collectionIndexes.keySet();

        for (String collection : collections) {
            ensureIndex(repository, collection);
        }
    }

    public String getIndexRootDir() {
        return indexRootDir;
    }

    public void setIndexRootDir(String indexRootDir) {
        this.indexRootDir = indexRootDir;
    }

    public Map<String, List<IndexDefinition>> getCollectionIndexes() {
        return collectionIndexes;
    }

    public void setCollectionIndexes(Map<String, List<IndexDefinition>> collectionIndexes) {
        this.collectionIndexes = collectionIndexes;
    }
}

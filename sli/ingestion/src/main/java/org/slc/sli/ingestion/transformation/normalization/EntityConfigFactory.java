package org.slc.sli.ingestion.transformation.normalization;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for entity configurations
 *
 * @author okrook
 *
 */
public class EntityConfigFactory {
    private static final String CONFIG_EXT = ".json";

    private File searchPath;

    private Map<String, EntityConfig> entityConfigurations = new HashMap<String, EntityConfig>();

    public synchronized EntityConfig getEntityConfiguration(String entityType) {
        if (!entityConfigurations.containsKey(entityType)) {
            File config = new File(searchPath, entityType + CONFIG_EXT);

            if (config.exists()) {
                try {
                    entityConfigurations.put(entityType, EntityConfig.parse(config));
                } catch (IOException e) {
                    entityConfigurations.put(entityType, null);
                }
            }
        }

        return entityConfigurations.get(entityType);
    }

    /**
     * @return the searchPath
     */
    public File getSearchPath() {
        return searchPath;
    }

    /**
     * @param searchPath the searchPath to set
     */
    public void setSearchPath(File searchPath) {
        this.searchPath = searchPath;
    }
}

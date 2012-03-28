package org.slc.sli.ingestion.transformation.normalization;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.ResourceUtils;

/**
 * Factory for entity configurations
 *
 * @author okrook
 *
 */
public class EntityConfigFactory {
    private static final String CONFIG_EXT = ".json";

    private String searchPath;

    private Map<String, EntityConfig> entityConfigurations = new HashMap<String, EntityConfig>();

    public synchronized EntityConfig getEntityConfiguration(String entityType) {
        if (!entityConfigurations.containsKey(entityType)) {
            try {
                File config = ResourceUtils.getFile(searchPath + entityType + CONFIG_EXT);

                if (config.exists()) {
                    entityConfigurations.put(entityType, EntityConfig.parse(config));
                }
            } catch (IOException e) {
                entityConfigurations.put(entityType, null);
            }
        }

        return entityConfigurations.get(entityType);
    }

    /**
     * @return the searchPath
     */
    public String getSearchPath() {
        return searchPath;
    }

    /**
     * @param searchPath the searchPath to set
     */
    public void setSearchPath(String searchPath) {
        this.searchPath = searchPath;
    }
}

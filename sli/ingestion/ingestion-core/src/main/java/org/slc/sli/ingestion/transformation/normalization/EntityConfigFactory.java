/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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


package org.slc.sli.ingestion.transformation.normalization;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import org.slc.sli.ingestion.util.LogUtil;

/**
 * Factory for entity configurations
 *
 * @author okrook
 *
 */
public class EntityConfigFactory implements ResourceLoaderAware {
    private static final String CONFIG_EXT = ".json";
    private static final EntityConfig NOT_FOUND = null;
    private static final Logger LOG = LoggerFactory.getLogger(EntityConfigFactory.class);


    private String searchPath;
    private ResourceLoader resourceLoader;

    private Map<String, EntityConfig> entityConfigurations = new HashMap<String, EntityConfig>();

    public synchronized EntityConfig getEntityConfiguration(String entityType) {
        if (!entityConfigurations.containsKey(entityType)) {
            InputStream configIs = null;
            try {
                Resource config = resourceLoader.getResource(searchPath + entityType + CONFIG_EXT);

                if (config.exists()) {
                    configIs = config.getInputStream();
                    entityConfigurations.put(entityType, EntityConfig.parse(configIs));
                } else {
                    LOG.warn("no config found for entity type {}", entityType);
                    entityConfigurations.put(entityType, NOT_FOUND);
                }
            } catch (IOException e) {
                LogUtil.error(LOG, "Error loading entity type " + entityType, e);
                entityConfigurations.put(entityType, NOT_FOUND);
            } finally {
                IOUtils.closeQuietly(configIs);
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

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}

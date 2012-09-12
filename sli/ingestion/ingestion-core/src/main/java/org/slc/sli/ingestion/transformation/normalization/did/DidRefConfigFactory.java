package org.slc.sli.ingestion.transformation.normalization.did;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;

import org.slc.sli.ingestion.util.LogUtil;

/**
 * Factory for deterministic reference configurations
 *
 * @author ecole
 *
 */
public class DidRefConfigFactory implements ResourceLoaderAware {
    private static final String CONFIG_EXT = ".json";
    private static final DidRefConfig NOT_FOUND = null;
    private static final Logger LOG = LoggerFactory.getLogger(DidRefConfigFactory.class);


    private String searchPath;
    private ResourceLoader resourceLoader;

    private Map<String, DidRefConfig> didRefConfigurations = new HashMap<String, DidRefConfig>();

    public synchronized DidRefConfig getDidRefConfiguration(String refType) {
        if (!didRefConfigurations.containsKey(refType)) {
            InputStream configIs = null;
            try {
                Resource config = resourceLoader.getResource(searchPath + refType + CONFIG_EXT);

                if (config.exists()) {
                    configIs = config.getInputStream();
                    didRefConfigurations.put(refType, DidRefConfig.parse(configIs));
                } else {
                    LOG.warn("no config found for entity type {}", refType);
                    didRefConfigurations.put(refType, NOT_FOUND);
                }
            } catch (IOException e) {
                LogUtil.error(LOG, "Error loading entity type " + refType, e);
                didRefConfigurations.put(refType, NOT_FOUND);
            } finally {
                IOUtils.closeQuietly(configIs);
            }
        }

        return didRefConfigurations.get(refType);
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

    @PostConstruct

    public void setup() {
        try {
            File directory = ResourceUtils.getFile(getSearchPath());
            Iterator<File> it = FileUtils.iterateFiles(directory, new String[] { "json" }, false);
            while (it.hasNext()) {
                String fileName = it.next().getName();
                fileName = fileName.substring(0, fileName.indexOf(CONFIG_EXT));
                getDidRefConfiguration(fileName);
            }
        } catch (FileNotFoundException e) {
            LogUtil.error(LOG, "Error getting files from " + getSearchPath(), e);
        }
    }
}

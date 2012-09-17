package org.slc.sli.ingestion.transformation.normalization.did;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
 * Factory for deterministic id entity configurations
 *
 * @author ecole
 *
 */
public class DidEntityConfigFactory implements ResourceLoaderAware {
    private static final String CONFIG_EXT = ".json";
    private static final DidEntityConfig NOT_FOUND = null;
    private static final Logger LOG = LoggerFactory.getLogger(DidEntityConfigFactory.class);


    private String searchPath;
    private ResourceLoader resourceLoader;

    private Map<String, DidEntityConfig> didEntityConfigurations = new HashMap<String, DidEntityConfig>();

    public synchronized DidEntityConfig getDidEntityConfiguration(String entityType) {
        if (!didEntityConfigurations.containsKey(entityType)) {
            InputStream configIs = null;
            try {
                Resource config = resourceLoader.getResource(searchPath + entityType + CONFIG_EXT);

                if (config.exists()) {
                    configIs = config.getInputStream();
                    didEntityConfigurations.put(entityType, DidEntityConfig.parse(configIs));
                } else {
                    LOG.warn("no config found for entity type {}", entityType);
                    didEntityConfigurations.put(entityType, NOT_FOUND);
                }
            } catch (IOException e) {
                LogUtil.error(LOG, "Error loading entity type " + entityType, e);
                didEntityConfigurations.put(entityType, NOT_FOUND);
            } finally {
                IOUtils.closeQuietly(configIs);
            }
        }

        return didEntityConfigurations.get(entityType);
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
            URL baseURL = ResourceUtils.getURL(getSearchPath());
            String protocol = baseURL.getProtocol();
            if (protocol.equals("jar")) {
                String jarPath = baseURL.getPath().substring(5, baseURL.getPath().indexOf("!"));
                JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    String name = entries.nextElement().getName();
                    if (name.matches(getSearchPath().split(":")[1] + "/\\w+\\.json")) {
                        String fileName = name.substring(name.lastIndexOf("/") + 1);
                        fileName = fileName.substring(0, fileName.indexOf(CONFIG_EXT));
                        getDidEntityConfiguration(fileName);
                    }
                }
            } else if (protocol.equals("file")) {
                File directory = FileUtils.toFile(baseURL);
                Iterator<File> it = FileUtils.iterateFiles(directory, new String[] { "json" }, false);
                while (it.hasNext()) {
                    String fileName = it.next().getName();
                    fileName = fileName.substring(0, fileName.indexOf(CONFIG_EXT));
                    getDidEntityConfiguration(fileName);
                }
            }
        } catch (FileNotFoundException fnfe) {
            LogUtil.error(LOG, "Error getting files from " + getSearchPath(), fnfe);
        } catch (IOException ioe) {
            LogUtil.error(LOG, "Error getting files from " + getSearchPath(), ioe);
        }
    }
}

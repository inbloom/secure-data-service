package org.slc.sli.search.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class IndexConfigStore {
    
    // map of configs 
    private Map<String, IndexConfig> configs;

    
    public IndexConfigStore(String configFile) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        if (configFile == null) {
            throw new IllegalArgumentException("sli.search.index.config must be provided");
        }
        File config = new File(configFile);
        if (!configFile.startsWith("/")) {
            URL url = getClass().getResource("/" + configFile);
            if (url == null) {
                throw new IllegalArgumentException("File" + configFile + " does not exist");
            }
            config = new File(url.getFile());
        }
        if (!config.exists()) {
            throw new IllegalArgumentException("File" + config.getAbsolutePath() + " does not exist");
        }
        Map<String, IndexConfig> map = mapper.readValue(config, new TypeReference<Map<String, IndexConfig>>(){});
        for (IndexConfig indexConfig: map.values()) {
            indexConfig.prepare();
        }
        this.configs = Collections.unmodifiableMap(map);
    }
    
    public List<String> getFields(String collection) {
        return getConfig(collection).getFields();
    }
    
    public Collection<String> getCollections() {
        return configs.keySet();
    }
    
    public IndexConfig getConfig(String collection) {
        IndexConfig config = configs.get(collection);
        if (config == null) {
            throw new IllegalArgumentException("Config for " + collection + " is not found");
        }
        return config;
    }
}

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

public class IndexEntityConfigStore {
    
    /**
     * Config holder entity
     *
     */
    public static final class Config {
        
        private List<String> fields;
     
        public List<String> getFields() {
            return fields;
        }
    }
    
    // map of configs 
    private Map<String, Config> configs;

    
    public IndexEntityConfigStore(String configFile) throws JsonParseException, JsonMappingException, IOException {
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
        Map<String, Config> map = mapper.readValue(config, new TypeReference<Map<String, Config>>(){});
        this.configs = Collections.unmodifiableMap(map);
    }
    
    public List<String> getFields(String collection) {
        Config config = configs.get(collection);
        if (config == null) {
            throw new IllegalArgumentException("Unknown collection " + collection);
        }
        return config.getFields();
    }
    
    public Collection<String> getCollections() {
        return configs.keySet();
    }
}

/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
    private final Map<String, IndexConfig> configs;

    
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
        IndexConfig indexConfig;
        for (Map.Entry<String, IndexConfig> entry: map.entrySet()) {
            indexConfig = entry.getValue();  
            indexConfig.prepare(entry.getKey());
            if (indexConfig.isChildDoc()) {
                map.get(indexConfig.getIndexType()).addDependent(entry.getKey());
            }
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

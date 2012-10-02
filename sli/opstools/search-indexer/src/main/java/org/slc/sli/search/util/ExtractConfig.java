package org.slc.sli.search.util;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExtractConfig {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private Map<String, List<String>> collectionFields;
    
    public ExtractConfig() {
        
        collectionFields = new HashMap<String, List<String>>();
        
        // read config file
        Properties p = new Properties();
        try {
            p.load(new FileReader("src/main/resources/search.properties"));
            
            for (Object key : p.keySet()) {
                String keyStr = (String) key;
                if (keyStr.contains("sli.search.extract")) {
                    String collection = keyStr.substring(keyStr.lastIndexOf('.')+1);
                    String fields = p.getProperty(keyStr);
                    List<String> fieldList = new ArrayList<String>(Arrays.asList(fields.replace(" ", "").split(",")));
                    collectionFields.put(collection, fieldList);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error reading search properties", e);
        }
    }
    
    public Set<String> collections() {
        return collectionFields.keySet();
    }
    
    public List<String> getFields(String collection) {
        return collectionFields.get(collection);
    }
    
    public void add(String collection, List<String> fields) {
        collectionFields.put(collection, fields);
    }
}
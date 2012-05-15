package org.slc.sli.ingestion.dal;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

/** Store the Mongo Index configurations.
 *  Will be loaded from the json files.
 *
 * @author tke
 *
 */
public class MongoIndexConfig {
    private String collection;
    private List<Map<String, String>> indexFields;
    private Map<String, String> options;
    
    public String getCollection() {
        return collection;
    }
    
    public void setCollection(String collection) {
        this.collection = collection;
    }
    
    public List<Map<String, String>> getIndexFields() {
        return indexFields;
    }
    
    public void setIndexFields(List<Map<String, String>> indexFields) {
        this.indexFields = indexFields;
    }
    
    public Map<String, String> getOptions() {
        return options;
    }
    
    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public static MongoIndexConfig parse(InputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(inputStream, MongoIndexConfig.class);
    }
}

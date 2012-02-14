package org.slc.sli.scaffold.semantics;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that holds a mapping between entity/field names and their corresponding documentation
 * 
 * @author jstokes
 * 
 */
public class ResourceDocumentation {

    private String resourceName;
    private String resourceDocumentation;
    private Map<String, String> fieldDocumentation = new HashMap<String, String>();
    
    public String getResourceDocumentation() {
        return resourceDocumentation;
    }

    public void setResourceDocumentation(String resourceDocumentation) {
        this.resourceDocumentation = resourceDocumentation;
    }

    public Map<String, String> getFieldDocumentation() {
        return fieldDocumentation;
    }
    
    public void addFieldDocumentation(String key, String value) {
        fieldDocumentation.put(key, value);
    }
    
    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(resourceName + ":" 
                  + resourceDocumentation + "\n");
        for (Map.Entry<String, String> entry : fieldDocumentation.entrySet()) {
            sb.append("\t" + entry.getKey()
                      + "\n\t\t" + entry.getValue() + "\n");
        }
        return sb.toString();
    }

}

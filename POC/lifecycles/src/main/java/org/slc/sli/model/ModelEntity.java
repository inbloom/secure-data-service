package org.slc.sli.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Define the core Datastore Entity class which holds the critical Datastore core data (body), identification information (type, id) and associated "metadata" details.
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */

@XmlRootElement
public class ModelEntity implements Serializable {

    // Logging
    private static final Logger log = LoggerFactory.getLogger(ModelEntity.class);
    
    
    // Constants
    private static final long serialVersionUID = -7538980021984854604L;
    public static final String ID_ATTRIBUTE = "id";

    
    // Static Methods
    // Jackson Mapper
    private static ObjectMapper MAPPER = null;

    public static ObjectMapper getMapper() {
        if (MAPPER == null) {
            MAPPER = new ObjectMapper();
        }
        return MAPPER;
    }
    
    // Parse ModelEntity from JSON
    public static ModelEntity fromJSON(String json) {
        try {
            return getMapper().readValue(json, ModelEntity.class);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    // Get Enum From String
    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
        if( c != null && string != null ) {
            try {
                return Enum.valueOf(c, string.trim().toUpperCase());
            }
            catch(IllegalArgumentException ex) {
            }
        }
        return null;
    }
    

    // Attributes
    protected String type;
    protected Object id;
    protected Map<String, Object> body;
    protected Map<String, Object> metaData;
    protected Map<String, String[]> keys;
    protected ModelLifecycle lifecycle;
    
    
    // Constructors
    public ModelEntity() {  
        this(null, null, null, null, null, null);
    }
    
    public ModelEntity(String type) {
        this(type, null, null, null, null, null);
    }

    public ModelEntity(String type, Map<String, Object> body) {
        this(type, null, body, null, null, null);
    }

    public ModelEntity(String type, Object id, Map<String, Object> body,  Map<String, Object> metaData, Map<String, String[]> keys, ModelLifecycle lifecycle) {
        this.type = type;
        this.id = id;
        this.body = body;
        this.metaData = metaData;
        this.keys = keys;
        this.lifecycle = lifecycle;
        
        if (this.type == null) {
            this.type = this.getClass().getSimpleName();
        }
        if (this.id == null) {
            this.id = "";
        }
        if (this.body == null) {
            this.body = new HashMap<String, Object>();
        }
        if (this.metaData == null) {
            this.metaData = new HashMap<String, Object>();
        }
        if (this.keys == null) {
            this.keys = new HashMap<String, String[]>();
        }
        if (this.lifecycle == null) {
            this.lifecycle = new ModelLifecycle();
        }
    }

    
    // Methods   
    
    public String getType() {
        return type;
    }
   
    public void setType(String type) {
        this.type = type;
    }
    
    public Object getId() {
        return id;
    }
    
    public void setId(Object id) {
        this.id = id;
    }
    
    public Map<String, Object> getBody() {
        return body;
    }
   
    public void setBody(Map<String, Object> body) {
        this.body = body;
    }
   
    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }
   
    public Map<String, String[]> getKeys() {
        return keys;
    }

    public void setKeys(Map<String, String[]> keys) {
        this.keys = keys;
    }
   
    public String[] getKeys(String attributeName) {
        return this.keys.get(attributeName);
    }

    public void setKeys(String attributeName, String[] attributeKeys) {
        this.keys.put(attributeName, attributeKeys);
    }
   
    public ModelLifecycle getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(ModelLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    /**
     * compare neutral records for equality 
     * initial version compares JSON representations 
     * 
     */
    public boolean equals(Object anObject) {
        
        ModelEntity anEntity = (ModelEntity)anObject;
        
        if (!this.getType().equals(anEntity.getType())) {
            return false;
        }
        if (!this.getId().toString().equals(anEntity.getId().toString())) {
            return false;
        }
        for (String key : this.getBody().keySet()) {
            Object value = this.getBody().get(key);
            
            Object aValue = anEntity.getBody().get(key);
            if (!value.equals(aValue)) {
                return false;
            }
        }
        for (String key : this.getMetaData().keySet()) {
            Object value = this.getMetaData().get(key);
            
            Object aValue = anEntity.getMetaData().get(key);
            if (!value.equals(aValue)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * return pure-json representation for strings by default
     */
    @Override
    public String toString() {
        return this.toJSON();
    }

    public String toJSON() {
        try {
            return getMapper().writeValueAsString(this);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    public String toXML() {
        String xml = "<" + this.getType() + ">";
        
        for (String key : this.getBody().keySet()) {
            xml += "<" + key + ">";
            xml += this.getBody().get(key);
            xml += "</" + key + ">";
        }
        
        xml += "</" + this.getType() + ">";
        
        return xml;
    }
    
}

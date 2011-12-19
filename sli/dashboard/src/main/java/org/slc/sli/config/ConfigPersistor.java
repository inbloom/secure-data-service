package org.slc.sli.config;

/**
 * 
 * ConfigPersistor reads and persists view configuration information.
 * 
 * @author dwu
 */
public class ConfigPersistor {

    // get the view config for an entity
    public static ViewConfig getConfig(String entityId) throws Exception {
    
        // make API call with entity id
        
        // extract data block from custom data field
        String configStr = "";
        
        // convert data block to POJOs
        ViewConfig config = ConfigUtil.fromXMLString(configStr);
        
        return config;
    }
    
    // save the view config for an entity
    public static void saveConfig(String entityId, ViewConfig config) throws Exception {
    
        // convert POJO to serialized format
        String configStr = ConfigUtil.toXMLString(config);
        
        // add data to custom data field
        
        // make API call
        
    }
    
}

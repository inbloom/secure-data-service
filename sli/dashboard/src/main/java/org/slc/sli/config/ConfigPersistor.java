package org.slc.sli.config;

import org.slc.sli.client.MockAPIClient;
import org.slc.sli.entity.CustomData;

/**
 * 
 * ConfigPersistor reads and persists view configuration information.
 * 
 * @author dwu
 */
public class ConfigPersistor {

    // get the view configuration for an entity
    public static ViewConfig getConfig(String entityId) throws Exception {
    
        // make API call with entity id
        MockAPIClient mockClient = new MockAPIClient();
        CustomData[] customData = mockClient.getCustomData(entityId, "view_config");
        
        // extract data block from custom data field
        if (customData == null || customData.length == 0)
            return null;
        
        String configStr = customData[0].getCustomData();
        
        // convert data block to POJO
        ViewConfig config = ConfigUtil.fromXMLString(configStr);
        
        return config;
    }
    
    // save the view configuration for an entity
    public static void saveConfig(String entityId, ViewConfig config) throws Exception {
    
        // convert POJO to serialized format
        String configStr = ConfigUtil.toXMLString(config);
        
        // add data to custom data field
        CustomData customData = new CustomData();
        customData.setCustomData(configStr);
        
        // make API call
        
    }
    
}

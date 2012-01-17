package org.slc.sli.config;

import org.slc.sli.client.APIClient;
import org.slc.sli.entity.CustomData;

/**
 * 
 * ConfigPersistor reads and persists view configuration information.
 * 
 * @author dwu
 */
public class ConfigPersistor {

    private static final String VIEW_CONFIG_API_KEY = "view_config";
    
    private APIClient apiClient;
    
    /**
     * Get the view configuration for an entity
     * 
     * @param entityId
     * @return ViewConfigSet
     * @throws Exception
     */
    public ViewConfigSet getConfigSet(String entityId) throws Exception {
    
        // make API call with entity id
        CustomData[] customData = apiClient.getCustomData(entityId, VIEW_CONFIG_API_KEY);
        
        // extract data block from custom data field
        if (customData == null || customData.length == 0) {
            return null;
        }
        
        String configStr = customData[0].getCustomData();
        
        // convert data block to POJO
        ViewConfigSet configSet = ConfigUtil.fromXMLString(configStr);
        
        return configSet;
    }

    /**
     * Save the view configurations for an entity
     * 
     * @param entityId
     * @param configSet
     * @throws Exception
     */
    public void saveConfigSet(String entityId, ViewConfigSet configSet) throws Exception {
    
        // convert POJO to serialized format
        String configStr = ConfigUtil.toXMLString(configSet);
        
        // add data to custom data field
        CustomData customData = new CustomData();
        customData.setCustomData(configStr);
        
        // make API call
        CustomData[] customDataSet = new CustomData[1];
        customDataSet[0] = customData;
        apiClient.saveCustomData(customDataSet, entityId, VIEW_CONFIG_API_KEY);
    }
    
    
    /*
     * Getters and setters
     */
    public APIClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(APIClient apiClient) {
        this.apiClient = apiClient;
    }
}

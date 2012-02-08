package org.slc.sli.config;

import java.util.List;

import com.google.gson.Gson;

import org.slc.sli.client.APIClient;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.EntityManager;
import org.slc.sli.util.Constants;

/**
 * 
 * ConfigPersistor reads and persists view configuration information.
 * 
 * @author dwu
 */
public class ConfigPersistor {

    private static final String VIEW_CONFIG_API_KEY = "view_config";
    private static final String LOZENGE_CONFIG_API_KEY = "lozenge_config";
    
    private APIClient apiClient;
    private EntityManager entityManager;
    
    /**
     * Get the view configuration for an entity
     * 
     * @param entityId
     * @return ViewConfigSet
     * @throws Exception
     */
    public ViewConfigSet getConfigSet(String entityId) throws Exception {
    
        // make API call with entity id
        List<GenericEntity> customData = entityManager.getCustomData(entityId, VIEW_CONFIG_API_KEY);
        
        // extract data block from custom data field
        if (customData == null || customData.size() == 0) {
            return null;
        }
        
        String configStr = (String) (customData.get(0).get(Constants.ATTR_CUSTOM_DATA));
        
        // convert data block to POJO
        ViewConfigSet configSet = ConfigUtil.fromXMLString(configStr);
        
        return configSet;
    }

    
    /**
     * Get the lozenges display configuration for an entity
     * 
     * @param entityId
     * @return LozengeConfig
     * @throws Exception
     */
    public LozengeConfig[] getLozengeConfig(String entityId) throws Exception {
        // make API call with entity id
        List<GenericEntity> customData = entityManager.getCustomData(entityId, LOZENGE_CONFIG_API_KEY);
        
        // extract data block from custom data field
        if (customData == null || customData.size() == 0) {
            return null;
        }
        
        String configStr = (String) (customData.get(0).get(Constants.ATTR_CUSTOM_DATA));
        
        // convert data block to POJO
        Gson gson = new Gson();        
        LozengeConfig[] retVal = gson.fromJson(configStr, LozengeConfig[].class);
        
        return retVal;
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

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}

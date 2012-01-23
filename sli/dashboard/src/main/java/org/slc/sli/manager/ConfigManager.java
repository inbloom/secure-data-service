package org.slc.sli.manager;

import java.util.Arrays;
import java.util.List;

import org.slc.sli.client.APIClient;
import org.slc.sli.config.ConfigPersistor;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.config.ViewConfigSet;
import org.slc.sli.config.LozengeConfig;

/**
 * 
 * ConfigManager allows other classes, such as controllers, to access and persist view configurations.
 * Given a user, it will obtain view configuration at each level of the user's hierarchy, and merge
 * them into one set for the user.
 * 
 * @author dwu
 */
public class ConfigManager extends Manager {
    
    ConfigPersistor persistor;
    
    public ConfigManager() {
        persistor = new ConfigPersistor();
    }
    
    public void setApiClient(APIClient apiClient) {
        this.apiClient = apiClient;
        persistor.setApiClient(apiClient);
    }
    
    /**
     * Get the view configuration set for a user
     * 
     * @param userId
     * @return ViewConfigSet
     */
    public ViewConfigSet getConfigSet(String userId) {
    
        // get view configs for user's hierarchy (state, district, etc)
        // TODO: call ConfigPersistor with entity ids, not user id
        ViewConfigSet userViewConfigSet = null;
        try {
            userViewConfigSet = persistor.getConfigSet(userId);
        } catch (Exception e) {
            return null;
        }
                
        // TODO: merge into one view config set for the user
        
        
        return userViewConfigSet;
    }
    
    /**
     * Get the configuration for one particular view, for a user
     * 
     * @param userId
     * @param viewName
     * @return ViewConfig
     */
    public ViewConfig getConfig(String userId, String viewName) {
        
        ViewConfigSet config = getConfigSet(userId);
        
        if (config == null) {
            return null;
        }
        
        // loop through, find right config
        for (ViewConfig view : config.getViewConfig()) {
            if (view.getName().equals(viewName)) {
                return view;
            }
        }
        return null;
    }
    
    /**
     * Get the configuration for one particular view, for a user
     * 
     * @param userId
     * @param viewName
     * @return ViewConfig
     */
    public List<LozengeConfig> getLozengeConfig(String userId) {
        
        // get lozenge configs for user's hierarchy (state, district, etc)
        // TODO: call ConfigPersistor with entity ids, not user id
        LozengeConfig[] userLozengeConfig = null;
        try {
            userLozengeConfig = persistor.getLozengeConfig(userId);
        } catch (Exception e) {
            return null;
        }
        return Arrays.asList(userLozengeConfig);
    }
    
    /**
     * Get the configuration for one particular view, for a user
     * 
     * @param userId
     * @param type - e.g. studentList, studentProfile, etc.
     * @return ViewConfig
     */
    // TODO: should return a list of ViewConfigs, once User-Based View logic is complete.
    public ViewConfig getConfigWithType(String userId, String type) {
        
        ViewConfigSet config = getConfigSet(userId);
        
        if (config == null) {
            return null;
        }
        
        // loop through, find right config
        for (ViewConfig view : config.getViewConfig()) {
            if (view.getType().equals(type)) {
                return view;
            }
        }
        return null;
    }
    
    /**
     * Merges a hierarchy of configuration sets into one set
     * 
     * @param configSets
     * @return ViewConfigSet
     */
    protected ViewConfigSet mergeConfigSets(List<ViewConfigSet> configSets) {
        // TODO: implement merge
        return null;
    }
    
    /**
     * Save a config set for a particular entity
     * 
     * @param entityId
     * @param configSet
     * @throws Exception
     */
    public void saveConfigSet(String entityId, ViewConfigSet configSet) throws Exception {
    
        persistor.saveConfigSet(entityId, configSet);
    
    }
    
}

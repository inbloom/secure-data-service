package org.slc.sli.config;

import java.util.List;

/**
 * 
 * ConfigManager allows other classes, such as controllers, to access and persist view configurations.
 * Given a user, it will obtain view configuration at each level of the user's hierarchy, and merge
 * them into one set for the user.
 * 
 * @author dwu
 */
public class ConfigManager {
    
    private static ConfigManager instance = null;
    
    protected ConfigManager() {        
    }
    
    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
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
            userViewConfigSet = ConfigPersistor.getConfigSet(userId);
        } catch (Exception e) {
            e.printStackTrace();
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
        
        // loop through, find right config
        for (ViewConfig view : config.getViewConfig()) {
            if (view.getName().equals(viewName))
                return view;
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
    public ViewConfig getConfigWithType(String userId, String typeName) {
        
        ViewConfigSet config = getConfigSet(userId);
        
        // loop through, find right config
        for (ViewConfig view : config.getViewConfig()) {
            if (view.getType().equals(typeName))
                return view;
        }
        return null;
    }
    
    /**
     * Merges a hierarchy of config sets into one set
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
    
        ConfigPersistor.saveConfigSet(entityId, configSet);
    
    }
    
}

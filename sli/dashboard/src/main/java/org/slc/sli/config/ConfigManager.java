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
    
    public ViewConfig getConfig(String userId) {
    
        // get view configs for user's hierarchy (state, district, etc)
        // TODO: call ConfigPersistor with entity ids, not user id
        ViewConfig userViewConfig = null;
        try {
            userViewConfig = ConfigPersistor.getConfig(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
                
        // merge into one view config for the user
        
        
        return userViewConfig;
    }
    
    public View getSingleViewConfig(String userId, String viewName) {
        
        ViewConfig config = getConfig(userId);
        
        // loop through, find right config
        for (View view : config.getView()) {
            if (view.getName().equals(viewName))
                return view;
        }
        return null;
    }
    
    protected ViewConfig mergeConfigs(List<ViewConfig> configs) {
        return null;
    }
    
    
    public void saveConfig(String entityId) {
        
    }
    
}
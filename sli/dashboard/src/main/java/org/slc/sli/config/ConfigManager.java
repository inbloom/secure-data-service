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
    
    public ViewConfig getConfig(String userId, String viewName) {
        
        ViewConfigSet config = getConfigSet(userId);
        
        // loop through, find right config
        for (ViewConfig view : config.getViewConfig()) {
            if (view.getName().equals(viewName))
                return view;
        }
        return null;
    }
    
    protected ViewConfigSet mergeConfigSets(List<ViewConfigSet> configSets) {
        // TODO: implement merge
        return null;
    }
    
    
    public void saveConfigSet(String entityId) {
        // TODO: implement save
    }
    
}

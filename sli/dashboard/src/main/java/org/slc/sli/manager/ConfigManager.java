package org.slc.sli.manager;

import java.util.List;

import org.slc.sli.config.LozengeConfig;
import org.slc.sli.config.StudentFilter;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.config.ViewConfigSet;
import org.slc.sli.entity.Config;


/**
 * 
 * ConfigManager allows other classes, such as controllers, to access and persist view
 * configurations.
 * Given a user, it will obtain view configuration at each level of the user's hierarchy, and merge
 * them into one set for the user.
 * 
 * @author dwu
 */
public interface ConfigManager extends Manager {
    public ViewConfigSet getConfigSet(String userId);
    public ViewConfig getConfig(String userId, String viewName);
    public List<LozengeConfig> getLozengeConfig(String userId);
    public List<StudentFilter> getStudentFilterConfig(String userId);
    public List<ViewConfig> getConfigsWithType(String userId, String type);

    /**
     * reads the educational organization hierarchy and return proper config file
     * 
     * @param token
     * @param componentId
     *            name of the profile
     * @return proper Config to be used for the dashbord
     */
    public Config getComponentConfig(String token, String componentId);    
}

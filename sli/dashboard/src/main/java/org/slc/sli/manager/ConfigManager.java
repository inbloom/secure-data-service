package org.slc.sli.manager;

import java.util.Collection;

import org.slc.sli.entity.Config;
import org.slc.sli.entity.CustomConfig;
import org.slc.sli.entity.EdOrgKey;


/**
 *
 * ConfigManager allows other classes, such as controllers, to access and persist view
 * configurations.
 * Given a user, it will obtain view configuration at each level of the user's hierarchy, and merge
 * them into one set for the user.
 *
 * @author dwu
 */
public interface ConfigManager {

    /**
     * reads the educational organization hierarchy and return proper config file
     *
     * @param customConfig - custom configuration for the district
     * @param userEdOrg - user educational organization proxy
     * @param componentId
     *            name of the profile
     * @return proper Config to be used for the dashbord
     */
    public Config getComponentConfig(CustomConfig customConfig, EdOrgKey userEdOrg, String componentId);
    /**
     * Get all available widget configs relevant for the user
     * @param customConfig - custom configuration for the district
     * @param userEdOrg - user educational organization proxy
     * @return collection of widget conigs
     */
    public Collection<Config> getWidgetConfigs(CustomConfig customConfig, EdOrgKey userEdOrg);
}

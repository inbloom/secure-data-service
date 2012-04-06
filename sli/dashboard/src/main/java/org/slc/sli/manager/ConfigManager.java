package org.slc.sli.manager;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.client.APIClient;
import org.slc.sli.config.ConfigPersistor;
import org.slc.sli.config.LozengeConfig;
import org.slc.sli.config.StudentFilter;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.config.ViewConfigSet;
import org.slc.sli.entity.Config;
import org.slc.sli.util.DashboardException;

/**
 * 
 * ConfigManager allows other classes, such as controllers, to access and persist view
 * configurations.
 * Given a user, it will obtain view configuration at each level of the user's hierarchy, and merge
 * them into one set for the user.
 * 
 * @author dwu
 */
public class ConfigManager extends ApiClientManager {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    ConfigPersistor persistor;
    EntityManager entityManager;
    private InstitutionalHierarchyManager institutionalHierarchyManager;
    
    private String driverConfigLocation;
    private String userConfigLocation;
    
    public ConfigManager() {
        persistor = new ConfigPersistor();
    }
    
    @Override
    public void setApiClient(APIClient apiClient) {
        super.setApiClient(apiClient);
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
     * @param viewName
     * @return StudentFilter list
     */
    public List<StudentFilter> getStudentFilterConfig(String userId) {
        
        // get student filter configs for user's hierarchy (state, district, etc)
        StudentFilter[] userStudentFilterConfig = null;
        try {
            userStudentFilterConfig = persistor.getStudentFilterConfig(userId);
        } catch (Exception e) {
            return null;
        }
        return Arrays.asList(userStudentFilterConfig);
    }
    
    /**
     * Get the configuration for one particular view, for a user
     * 
     * @param userId
     * @param type
     *            - e.g. studentList, studentProfile, etc.
     * @return List<ViewConfig>
     */
    public List<ViewConfig> getConfigsWithType(String userId, String type) {
        
        ViewConfigSet config = getConfigSet(userId);
        List<ViewConfig> viewConfigs = null;
        
        if (config != null && config.getViewConfig() != null) {
            viewConfigs = new ArrayList<ViewConfig>();
            
            // loop through, find right type configs
            for (ViewConfig view : config.getViewConfig()) {
                if (view.getType().equals(type)) {
                    viewConfigs.add(view);
                }
            }
        }
        return viewConfigs;
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
    
    public EntityManager getEntityManager() {
        return entityManager;
    }
    
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        persistor.setEntityManager(entityManager);
    }
    
    /**
     * this method should be called by Spring Framework
     * set location of config file to be read. If the directory does not exist, create it.
     * 
     * @param configLocation
     *            reading from properties file panel.config.driver.dir
     */
    public void setDriverConfigLocation(String configLocation) {
        URL url = Config.class.getClassLoader().getResource(configLocation);
        if (url == null) {
            File f = new File(Config.class.getClassLoader().getResource("") + "/" + configLocation);
            f.mkdir();
            this.driverConfigLocation = f.getAbsolutePath();
        } else
            this.driverConfigLocation = url.getPath();
    }
    
    /**
     * this method should be called by Spring Framework
     * set location of config file to be read. If the directory does not exist, create it.
     * 
     * @param configLocation
     *            reading from properties file panel.config.custom.dir
     */
    public void setUserConfigLocation(String configLocation) {
        if (!configLocation.startsWith("/")) {
            URL url = Config.class.getClassLoader().getResource(configLocation);
            if (url == null) {
                File f = new File(Config.class.getClassLoader().getResource("").getPath() + configLocation);
                f.mkdir();
                configLocation = f.getAbsolutePath();
            } else
                configLocation = url.getPath();
        }
        this.userConfigLocation = configLocation;
    }
    
    /**
     * return the absolute file path of domain specific config file
     * 
     * @param path
     *            can be district ID name or state ID name
     * @param componentId
     *            profile name
     * @return the absolute file path of domain specific config file
     */
    public String getComponentConfigLocation(String path, String componentId) {
        
        return userConfigLocation + "/" + path + "/" + componentId + ".json";
    }
    
    /**
     * return the absolute file path of default config file
     * 
     * @param path
     *            can be district ID name or state ID name
     * @param componentId
     *            profile name
     * @return the absolute file path of default config file
     */
    public String getDriverConfigLocation(String componentId) {
        return this.driverConfigLocation + "/" + componentId + ".json";
    }
    
    /**
     * Find the lowest organization hierarchy config file. If the lowest organization hierarchy
     * config file does not exist, it returns default (Driver) config file.
     * If the Driver config file does not exist, it is in a critical situation. It will throw an
     * exception.
     * 
     * @param customPath
     *            abslute directory path where a config file exist.
     * @param componentId
     *            name of the profile
     * @return proper Config to be used for the dashboard
     */
    private Config getConfigByPath(String customPath, String componentId) {
        Gson gson = new GsonBuilder().create();
        Config customConfig = null;
        Config driverConfig = null;
        try {
            // read Driver (default) config.
            File f = new File(getDriverConfigLocation(componentId));
            driverConfig = gson.fromJson(new FileReader(f), Config.class);
            
            // read custom Config
            f = new File(getComponentConfigLocation(customPath, componentId));
            // if custom config exist, read the config file
            if (f.exists()) {
                customConfig = gson.fromJson(new FileReader(f), Config.class);
                // get overwritten Config file with customConfig based on Driver config.
                return driverConfig.overWrite(customConfig);
            }
            return driverConfig;
        } catch (Throwable t) {
            logger.error("Unable to read config for " + componentId + ", for path " + customPath);
            throw new DashboardException("Unable to read config for " + componentId + ", for path " + customPath);
        }
    }
    
    protected String getCustomConfigPathForUserDomain(String token) {
        return institutionalHierarchyManager.getUserDistrictId(token);
    }
    
    /**
     * reads the educational organization hierarchy and return proper config file
     * 
     * @param token
     * @param componentId
     *            name of the profile
     * @return proper Config to be used for the dashbord
     */
    public Config getComponentConfig(String token, String componentId) {
        
        return getConfigByPath(getCustomConfigPathForUserDomain(token), componentId);
    }
    
    public void setInstitutionalHierarchyManager(InstitutionalHierarchyManager institutionalHierarchyManager) {
        this.institutionalHierarchyManager = institutionalHierarchyManager;
    }
}

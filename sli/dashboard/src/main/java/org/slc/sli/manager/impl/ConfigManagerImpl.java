package org.slc.sli.manager.impl;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.googlecode.ehcache.annotations.Cacheable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.config.ConfigPersistor;
import org.slc.sli.config.LozengeConfig;
import org.slc.sli.config.StudentFilter;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.config.ViewConfigSet;
import org.slc.sli.entity.Config;
import org.slc.sli.entity.CustomConfig;
import org.slc.sli.entity.Config.Type;
import org.slc.sli.entity.EdOrgKey;
import org.slc.sli.manager.ConfigManager;
import org.slc.sli.util.DashboardException;

/**
 * 
 * ConfigManager allows other classes, such as controllers, to access and
 * persist view
 * configurations.
 * Given a user, it will obtain view configuration at each level of the user's
 * hierarchy, and merge
 * them into one set for the user.
 * 
 * @author dwu
 */
public class ConfigManagerImpl implements ConfigManager {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ConfigPersistor persistor;
    
    private String driverConfigLocation;
    private String userConfigLocation;
    
    public ConfigManagerImpl() {
    }
    
    /**
     * Get the view configuration set for a user
     * 
     * @param userId
     * @return ViewConfigSet
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
    public List<StudentFilter> getStudentFilterConfig(String userId) {
        
        // get student filter configs for user's hierarchy (state, district,
        // etc)
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
    @Override
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
     * this method should be called by Spring Framework
     * set location of config file to be read. If the directory does not exist,
     * create it.
     * 
     * @param configLocation
     *            reading from properties file panel.config.driver.dir
     */
    public void setDriverConfigLocation(String configLocation) {
        URL url = Config.class.getClassLoader().getResource(configLocation);
        if (url == null) {
            File f = new File(Config.class.getClassLoader().getResource("")
                    + "/" + configLocation);
            f.mkdir();
            this.driverConfigLocation = f.getAbsolutePath();
        } else {
            this.driverConfigLocation = url.getPath();
        }
    }
    
    /**
     * this method should be called by Spring Framework
     * set location of config file to be read. If the directory does not exist,
     * create it.
     * 
     * @param configLocation
     *            reading from properties file panel.config.custom.dir
     */
    public void setUserConfigLocation(String configLocation) {
        if (!configLocation.startsWith("/")) {
            URL url = Config.class.getClassLoader().getResource(configLocation);
            if (url == null) {
                File f = new File(Config.class.getClassLoader().getResource("")
                        .getPath()
                        + configLocation);
                f.mkdir();
                configLocation = f.getAbsolutePath();
            } else {
                configLocation = url.getPath();
            }
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
     * Find the lowest organization hierarchy config file. If the lowest
     * organization hierarchy
     * config file does not exist, it returns default (Driver) config file.
     * If the Driver config file does not exist, it is in a critical situation.
     * It will throw an
     * exception.
     * 
     * @param apiCustomConfig
     *            custom configuration uploaded by admininistrator.
     * @param customPath
     *            abslute directory path where a config file exist.
     * @param componentId
     *            name of the profile
     * @return proper Config to be used for the dashboard
     */
    private Config getConfigByPath(CustomConfig apiCustomConfig, String customPath, String componentId) {
        Config driverConfig = null;
        try {
            // read custom Config
            File f = new File(getComponentConfigLocation(customPath,
                    componentId));
            String driverId = componentId;
            Config customConfig = null;
            // if custom config exist, read the config file
            if (f.exists()) {
                customConfig = loadConfig(f);
                driverId = customConfig.getParentId();
            }
            // read Driver (default) config.
            f = new File(getDriverConfigLocation(driverId));
            driverConfig = loadConfig(f);
            if (customConfig != null) {
                if ((apiCustomConfig == null) || (apiCustomConfig.size() <= 0)) {
                    // get overwritten Config file with customConfig based on Driver
                    // config.
                    return driverConfig.overWrite(customConfig);
                }
            }
            return driverConfig;
        } catch (Throwable t) {
            logger.error("Unable to read config for " + componentId
                    + ", for path " + customPath, t);
            throw new DashboardException("Unable to read config for "
                    + componentId + ", for path " + customPath);
        }
    }
    
    private Config loadConfig(File f) throws Exception {
        return new GsonBuilder().create().fromJson(new FileReader(f),
                Config.class);
    }
    
    protected String getCustomConfigPathForUserDomain(EdOrgKey edOrgKey) {
        return edOrgKey == null ? null : edOrgKey.getDistrictId();
    }
    
    @Override
    public Config getComponentConfig(CustomConfig customConfig,
            EdOrgKey edOrgKey, String componentId) {
        if (customConfig != null) {
            Config customComponentConfig = customConfig.get(componentId);
            if (customComponentConfig != null) {
                return customComponentConfig;
            }
        }
        return getConfigByPath(customConfig, getCustomConfigPathForUserDomain(edOrgKey),
                componentId);
    }
    
    public void setConfigPersistor(ConfigPersistor configPersistor) {
        this.persistor = configPersistor;
    }
    
    @Override
    @Cacheable(cacheName = "user.config.widget")
    public Collection<Config> getWidgetConfigs(CustomConfig customConfig,
            EdOrgKey edOrgKey) {
        // id to config map
        Map<String, Config> widgets = new HashMap<String, Config>();
        Config config;
        // list files in driver dir
        File driverConfigDir = new File(this.driverConfigLocation);
        File[] configs = driverConfigDir.listFiles();
        if (configs == null) {
            logger.error("Unable to read config directory");
            throw new DashboardException("Unable to read config directory!!!!");
        }
        
        for (File f : driverConfigDir.listFiles()) {
            try {
                config = loadConfig(f);
            } catch (Exception t) {
                logger.error("Unable to read config " + f.getName()
                        + ". Skipping file", t);
                continue;
            }
            // assemble widgets
            if (config.getType() == Type.WIDGET) {
                widgets.put(config.getId(), config);
            }
        }
        for (String id : widgets.keySet()) {
            widgets.put(id, getComponentConfig(customConfig, edOrgKey, id));
        }
        return widgets.values();
    }
}

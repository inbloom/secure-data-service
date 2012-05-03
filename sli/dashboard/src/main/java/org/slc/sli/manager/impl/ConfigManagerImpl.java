package org.slc.sli.manager.impl;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.googlecode.ehcache.annotations.Cacheable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.entity.Config;
import org.slc.sli.entity.Config.Type;
import org.slc.sli.entity.CustomConfig;
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

    private String driverConfigLocation;
    private String userConfigLocation;

    public ConfigManagerImpl() {
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

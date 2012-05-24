package org.slc.sli.manager.impl;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.entity.Config;
import org.slc.sli.entity.Config.Type;
import org.slc.sli.entity.ConfigMap;
import org.slc.sli.entity.EdOrgKey;
import org.slc.sli.manager.ApiClientManager;
import org.slc.sli.manager.ConfigManager;
import org.slc.sli.util.DashboardException;
import org.slc.sli.util.JsonConverter;

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
public class ConfigManagerImpl extends ApiClientManager implements ConfigManager {
    private static final String USER_CONFIG_CACHE = "user.panel.config";
    private static final String USER_WIDGET_CONFIG_CACHE = "user.config.widget";
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
    private Config getConfigByPath(Config customConfig, String componentId) {
        Config driverConfig = null;
        try {
            String driverId = componentId;
            // if custom config exist, read the config file
            if (customConfig != null) {
                driverId = customConfig.getParentId();
            }
            // read Driver (default) config.
            File f = new File(getDriverConfigLocation(driverId));
            driverConfig = loadConfig(f);
            if (customConfig != null) {
                return driverConfig.overWrite(customConfig);
            }
            return driverConfig;
        } catch (Throwable t) {
            logger.error("Unable to read config for " + componentId, t);
            throw new DashboardException("Unable to read config for " + componentId);
        }
    }

    private Config loadConfig(File f) throws Exception {
        if (f.exists()) {
            FileReader fr = new FileReader(f);
            try {
                return JsonConverter.fromJson(fr, Config.class);
            } finally {
                IOUtils.closeQuietly(fr);
            }
        }
        return null;
    }

    protected String getCustomConfigPathForUserDomain(EdOrgKey edOrgKey) {

        return edOrgKey == null ? null : edOrgKey.getDistrictId();
    }

    @Override
    public Config getComponentConfig(String token, EdOrgKey edOrgKey, String componentId) {
        ConfigMap configMap = getCustomConfig(token, edOrgKey);
        Config customComponentConfig = null;
        // if api has config, use it, otherwise, try local config
        if (configMap != null && !configMap.isEmpty()) {
            customComponentConfig = configMap.getComponentConfig(componentId);
        } else {
            try {
                customComponentConfig = loadConfig(new File(getComponentConfigLocation(getCustomConfigPathForUserDomain(edOrgKey), componentId)));
            } catch (Exception e) {
                logger.error("Unable to read config for " + componentId + ", for " + getCustomConfigPathForUserDomain(edOrgKey), e);
                throw new DashboardException("Unable to read local custom config for " + componentId);
            }
        }
        return getConfigByPath(customComponentConfig, componentId);
    }


    @Override
    public Collection<Config> getWidgetConfigs(String token, EdOrgKey edOrgKey) {
        CacheValue<Collection<Config>> value = getCacheValueFromCache(USER_WIDGET_CONFIG_CACHE, token, edOrgKey);
        if (value == null) {
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
                widgets.put(id, getComponentConfig(token, edOrgKey, id));
            }
            putToCache(USER_WIDGET_CONFIG_CACHE, token, edOrgKey, widgets.values());
            return widgets.values();
        }
        return value.get();
    }

    /**
     * Get the user's educational organization's custom configuration.
     *
     * @param token
     *            The user's authentication token.
     * @return The education organization's custom configuration
     */
    @Override
    public ConfigMap getCustomConfig(String token, EdOrgKey edOrgKey) {

        CacheValue<ConfigMap> value = getCacheValueFromCache(USER_CONFIG_CACHE, token);
        ConfigMap config = null;
        if (value == null) {
            try {
              config = getApiClient().getEdOrgCustomData(token, edOrgKey.getSliId());
              putToCache(USER_CONFIG_CACHE, token, config);
            } catch (Throwable t) {
                logger.error("Unable to get custom config from the store for district id " + edOrgKey.getDistrictId(), t);
            }
        } else {
            config = value.get();
        }
        return config;
    }

    /**
     * Put or save the user's educational organization's custom configuration.
     *
     * @param token
     *            The user's authentication token.
     * @param customConfigJson
     *            The education organization's custom configuration JSON.
     */
    @Override
    public void putCustomConfig(String token, EdOrgKey edOrgKey, ConfigMap configMap) {
        getApiClient().putEdOrgCustomData(token, edOrgKey.getSliId(), configMap);
        removeFromCache(USER_CONFIG_CACHE, token);
    }
}

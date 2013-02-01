/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.dashboard.manager.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import org.slc.sli.dashboard.client.APIClient;
import org.slc.sli.dashboard.entity.Config;
import org.slc.sli.dashboard.entity.ConfigMap;
import org.slc.sli.dashboard.entity.EdOrgKey;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.manager.ApiClientManager;
import org.slc.sli.dashboard.manager.ConfigManager;
import org.slc.sli.dashboard.util.CacheableConfig;
import org.slc.sli.dashboard.util.Constants;
import org.slc.sli.dashboard.util.DashboardException;
import org.slc.sli.dashboard.util.JsonConverter;

import com.google.gson.JsonSyntaxException;

/**
 *
 * ConfigManager allows other classes, such as controllers, to access and
 * persist view configurations.
 * Given a user, it will obtain view configuration at each level of the user's
 * hierarchy, and merge them into one set for the user.
 *
 * @author dwu
 */
public class ConfigManagerImpl extends ApiClientManager implements ConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigManagerImpl.class);

    private String driverConfigLocation;
    private String userConfigLocation;

    private static final String LAYOUT_NAME = "layoutName";
    private static final String LAYOUT = "layout";
    private static final String DEFAULT = "default";
    private static final String TYPE = "type";

    public ConfigManagerImpl() {
        //Default constructor
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
        URL url = Thread.currentThread().getContextClassLoader().getResource(configLocation);
        if (url == null) {
            File f = new File(Thread.currentThread().getContextClassLoader().getResource("") + "/" + configLocation);
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
        String localConfigLocation = configLocation;

        if (!localConfigLocation.startsWith("/")) {
            URL url = Thread.currentThread().getContextClassLoader().getResource(localConfigLocation);
            if (url == null) {
                File f = new File(Thread.currentThread().getContextClassLoader().getResource("").getPath() + localConfigLocation);
                f.mkdir();
                localConfigLocation = f.getAbsolutePath();
            } else {
                localConfigLocation = url.getPath();
            }
        }
        this.userConfigLocation = localConfigLocation;
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
        } catch (FileNotFoundException ex) {

            LOGGER.error("Unable to read config for " + componentId, ex);
            throw new DashboardException("Unable to read config for " + componentId, ex);
        } catch (JsonSyntaxException ex) {
            LOGGER.error("Unable to read config for " + componentId, ex);
            throw new DashboardException("Unable to read config for " + componentId, ex);
        }
    }

    private Config loadConfig(File f) throws FileNotFoundException {
        if (f.exists()) {
            FileReader fr = new FileReader(f);
            try {
                return JsonConverter.fromJson(fr, Config.class);
            } finally {
                IOUtils.closeQuietly(fr);
            }
        } else {
            throw new FileNotFoundException("Config file does not exists.");
        }
    }

    @Override
    @CacheableConfig
    public Config getComponentConfig(String token, EdOrgKey edOrgKey, String componentId) {
        Config customComponentConfig = null;
        GenericEntity edOrg = null;
        GenericEntity parentEdOrg = null;
        EdOrgKey parentEdOrgKey = null;
        String id = edOrgKey.getSliId();
        List<EdOrgKey> edOrgKeys = new ArrayList<EdOrgKey>();
        edOrgKeys.add(edOrgKey);

        // keep reading EdOrg until it hits the top.
        APIClient apiClient = getApiClient();

        do {
            if (apiClient != null) {
                edOrg = apiClient.getEducationalOrganization(token, id);
                if (edOrg != null) {
                    parentEdOrg = apiClient.getParentEducationalOrganization(token, edOrg);
                    if (parentEdOrg != null) {
                        id = parentEdOrg.getId();
                        parentEdOrgKey = new EdOrgKey(id);
                        edOrgKeys.add(parentEdOrgKey);
                    }
                } else { // if edOrg is null, it means no parent edOrg either.
                    parentEdOrg = null;
                }
            }
        } while (parentEdOrg != null);
        for (EdOrgKey key : edOrgKeys) {
            ConfigMap configMap = getCustomConfig(token, key);
            // if api has config
            if (configMap != null && !configMap.isEmpty()) {
                Config edOrgComponentConfig = configMap.getComponentConfig(componentId);
                if (edOrgComponentConfig != null) {
                    if (customComponentConfig == null) {
                        customComponentConfig = edOrgComponentConfig;
                    } else {
                        // edOrgComponentConfig overwrites customComponentConfig
                        customComponentConfig = edOrgComponentConfig.overWrite(customComponentConfig);
                    }
                }
            }
        }
        return getConfigByPath(customComponentConfig, componentId);
    }

    @Override
    @Cacheable(value = Constants.CACHE_USER_WIDGET_CONFIG)
    public Collection<Config> getWidgetConfigs(String token, EdOrgKey edOrgKey) {

        Map<String, String> attrs = new HashMap<String, String>();
        attrs.put("type", Config.Type.WIDGET.toString());
        return getConfigsByAttribute(token, edOrgKey, attrs);
    }

    @Override
    public Collection<Config> getConfigsByAttribute(String token, EdOrgKey edOrgKey, Map<String, String> attrs) {
        return getConfigsByAttribute(token, edOrgKey, attrs, true);
    }

    @Override
    public Collection<Config> getConfigsByAttribute(String token, EdOrgKey edOrgKey, Map<String, String> attrs,
            boolean overwriteCustomConfig) {

        // id to config map
        Map<String, Config> configs = new HashMap<String, Config>();
        Config config;

        // list files in driver dir
        File driverConfigDir = new File(this.driverConfigLocation);
        File[] driverConfigFiles = driverConfigDir.listFiles();
        if (driverConfigFiles == null) {
            LOGGER.error("Unable to read config directory");
            throw new DashboardException("Unable to read config directory!!!!");
        }

        for (File f : driverConfigFiles) {
            try {
                config = loadConfig(f);
            } catch (FileNotFoundException e) {
                LOGGER.error("Unable to read config " + f.getName() + ". Skipping file", e);
                continue;
            } catch (JsonSyntaxException e) {
                LOGGER.error("Unable to read config " + f.getName() + ". Skipping file", e);
                continue;
            }

            // check the config params. if they all match, add to the config map.
            boolean matchAll = true;
            for (String attrName : attrs.keySet()) {
                String methodName = "";
                try {

                    // use reflection to call the right config object method
                    methodName = "get" + Character.toUpperCase(attrName.charAt(0)) + attrName.substring(1);
                    Method method = config.getClass().getDeclaredMethod(methodName, new Class[] {});
                    Object ret = method.invoke(config, new Object[] {});

                    // compare the result to the desired result
                    if (!(ret.toString().equals(attrs.get(attrName)))) {
                        matchAll = false;
                        break;
                    }
                } catch (SecurityException e) {
                    matchAll = false;
                    LOGGER.error("Error calling config method: " + methodName);
                } catch (NoSuchMethodException e) {
                    matchAll = false;
                    LOGGER.error("Error calling config method: " + methodName);
                } catch (IllegalArgumentException e) {
                    matchAll = false;
                    LOGGER.error("Error calling config method: " + methodName);
                } catch (IllegalAccessException e) {
                    matchAll = false;
                    LOGGER.error("Error calling config method: " + methodName);
                } catch (InvocationTargetException e) {
                    matchAll = false;
                    LOGGER.error("Error calling config method: " + methodName);
                }
            }

            // add to config map
            if (matchAll) {
                configs.put(config.getId(), config);
            }
        }

        // get custom configs
        if (overwriteCustomConfig) {
            for (String id : configs.keySet()) {
                configs.put(id, getComponentConfig(token, edOrgKey, id));
            }
        }
        return configs.values();
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
        try {
            return getApiClient().getEdOrgCustomData(token, edOrgKey.getSliId());
        } catch (JsonSyntaxException e) {
            // it's a valid scenario when there is no district specific config. Default will be used
            // in this case.
            LOGGER.debug("No district specific config is available, the default config will be used");
            return null;
        }
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
    @CacheEvict(value = Constants.CACHE_USER_PANEL_CONFIG, allEntries = true)
    public void putCustomConfig(String token, EdOrgKey edOrgKey, ConfigMap configMap) {
        getApiClient().putEdOrgCustomData(token, edOrgKey.getSliId(), configMap);
    }

    /**
     * Save one custom configuration for an ed-org
     *
     */
    @Override
    @CacheEvict(value = Constants.CACHE_USER_PANEL_CONFIG, allEntries = true)
    public void putCustomConfig(String token, EdOrgKey edOrgKey, Config config) {

        // get current custom config map from api
        ConfigMap configMap = getCustomConfig(token, edOrgKey);
        if (configMap == null) {
            configMap = new ConfigMap();
            configMap.setConfig(new HashMap<String, Config>());
        }

        // update with new config
        ConfigMap newConfigMap = configMap.cloneWithNewConfig(config);

        // write new config map
        getApiClient().putEdOrgCustomData(token, edOrgKey.getSliId(), newConfigMap);
    }

    /**
     * To find Config is PANEL types and belong to the layout.
     *
     * @param config
     * @param layoutName
     * @return
     */
    private boolean doesBelongToLayout(Config config, String layoutName) {
        boolean belongConfig = true;
        // first filter by type.
        // Target TYPEs are PANEL, GRID, TREE, and REPEAT_HEADER_GRID

        Config.Type type = config.getType();
        if (type != null
                && (type.equals(Config.Type.PANEL) || type.equals(Config.Type.GRID) || type.equals(Config.Type.TREE) || type
                        .equals(Config.Type.REPEAT_HEADER_GRID))) {
            // if a client requests specific layout,
            // then filter layout.
            if (layoutName != null) {

                belongConfig = false;
                Map<String, Object> configParams = config.getParams();
                if (configParams != null) {
                    List<String> layouts = (List<String>) configParams.get(LAYOUT);
                    if (layouts != null) {
                        if (layouts.contains(layoutName)) {
                            belongConfig = true;
                        }
                    }
                }
            }
        } else {
            belongConfig = false;
        }
        return belongConfig;
    }

    @Override
    public Map<String, Collection<Config>> getAllConfigByType(String token, EdOrgKey edOrgKey,
            Map<String, String> params) {
        Map<String, Collection<Config>> allConfigs = new HashMap<String, Collection<Config>>();

        // configIdLookup is used to check parentId from Custom Config exists in Driver Config
        Set<String> configIdLookup = new HashSet<String>();

        Map<String, String> attribute = new HashMap<String, String>();
        String layoutName = params.get(LAYOUT_NAME);

        if (params.containsKey(TYPE)) {
            attribute.put(TYPE, params.get(TYPE));
        }

        // get Driver Config by specified attribute
        Collection<Config> driverConfigs = getConfigsByAttribute(token, edOrgKey, attribute, false);

        // filter config by layout name
        // and
        // build lookup index
        Iterator<Config> configIterator = driverConfigs.iterator();
        while (configIterator.hasNext()) {
            Config driverConfig = configIterator.next();
            if (doesBelongToLayout(driverConfig, layoutName)) {
                configIdLookup.add(driverConfig.getId());
            } else {
                configIterator.remove();
            }
        }

        // add DriverConfig to a returning object
        allConfigs.put(DEFAULT, driverConfigs);

        // read edOrg custom config recursively
        APIClient apiClient = getApiClient();
        
        EdOrgKey loopEdOrgKey = edOrgKey;
        
        while (loopEdOrgKey != null) {
            // customConfigByType will be added to a returning object
            Collection<Config> customConfigByType = new LinkedList<Config>();

            // get current edOrg custom config
            ConfigMap customConfigMap = getCustomConfig(token, loopEdOrgKey);
            if (customConfigMap != null) {
                Map<String, Config> configByMap = customConfigMap.getConfig();
                if (configByMap != null) {
                    Collection<Config> customConfigs = configByMap.values();
                    if (customConfigs != null) {
                        for (Config customConfig : customConfigs) {

                            // if parentId from customConfig does not exist in DriverConfig,
                            // then ignore.
                            String parentId = customConfig.getParentId();
                            if (parentId != null && configIdLookup.contains(parentId)) {
                                if (doesBelongToLayout(customConfig, layoutName)) {
                                    customConfigByType.add(customConfig);
                                }
                            }
                        }
                    }
                }
            }

            // find current EdOrg entity
            GenericEntity edOrg = apiClient.getEducationalOrganization(token, loopEdOrgKey.getSliId());
            List<String> organizationCategories = (List<String>) edOrg.get(Constants.ATTR_ORG_CATEGORIES);
            if (organizationCategories != null && !organizationCategories.isEmpty()) {
                for (String educationAgency : organizationCategories) {
                    if (educationAgency != null) {
                        allConfigs.put(educationAgency, customConfigByType);
                        break;
                    }
                }
            }

            // find parent EdOrg
            loopEdOrgKey = null;
            edOrg = apiClient.getParentEducationalOrganization(token, edOrg);
            if (edOrg != null) {
                String id = edOrg.getId();
                if (id != null && !id.isEmpty()) {
                    loopEdOrgKey = new EdOrgKey(id);
                }
            }
        }

        return allConfigs;
    }
}

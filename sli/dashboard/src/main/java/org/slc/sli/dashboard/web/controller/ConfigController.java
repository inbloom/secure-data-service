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

package org.slc.sli.dashboard.web.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.dashboard.entity.Config;
import org.slc.sli.dashboard.entity.ConfigMap;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.manager.ConfigManager;
import org.slc.sli.dashboard.manager.UserEdOrgManager;
import org.slc.sli.dashboard.util.Constants;
import org.slc.sli.dashboard.util.SecurityUtil;

/**
 *
 * DashboardConfigController
 * This controller handles the dashboard config pages which are only accessible
 * by IT Admins of a District.
 *
 */
@Controller
public class ConfigController extends GenericLayoutController {
    private static final String DASHBOARD_CONFIG_FTL = "dashboard_config.ftl";
    private static final String CONFIG_URL = "/service/config";
    private static final String CONFIG_SAVE_URL = "/service/config/ajaxSave";
    private static final String CONFIG_ALL = "/s/c/cfg/all";

    private UserEdOrgManager userEdOrgManager;
    private ConfigManager configManager;

    @Autowired
    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Autowired
    public void setUserEdOrgManager(UserEdOrgManager userEdOrgManager) {
        this.userEdOrgManager = userEdOrgManager;
    }

    /**
     * Generic layout handler
     *
     * @deprecated retiring method
     * @param id
     * @param request
     * @return
     * @throws IllegalAccessException
     */
    @Deprecated
    @RequestMapping(value = CONFIG_URL, method = RequestMethod.GET)
    public ModelAndView getConfig(HttpServletRequest request) throws IllegalAccessException {
        ModelMap model = new ModelMap();

        String token = SecurityUtil.getToken();
        GenericEntity staffEntity = userEdOrgManager.getStaffInfo(token);

        Boolean isAdmin = SecurityUtil.isAdmin();
        if (isAdmin != null && isAdmin.booleanValue()) {
            GenericEntity edOrg = (GenericEntity) staffEntity.get(Constants.ATTR_ED_ORG);
            boolean configUser = false;

            if (edOrg != null) {
                List<String> organizationCategories = (List<String>) edOrg.get(Constants.ATTR_ORG_CATEGORIES);
                if (organizationCategories != null && !organizationCategories.isEmpty()) {
                    for (String educationAgency : organizationCategories) {
                        if (educationAgency != null && educationAgency.equals(Constants.LOCAL_EDUCATION_AGENCY)) {
                            configUser = true;
                            break;
                        } else if (educationAgency != null && educationAgency.equals(Constants.STATE_EDUCATION_AGENCY)) {
                            configUser = true;
                            break;
                        }
                    }
                }
            }
            if (configUser) {
                ConfigMap configMap = configManager.getCustomConfig(token, userEdOrgManager.getUserEdOrg(token));

                if (configMap != null) {
                    model.addAttribute("configJSON", new GsonBuilder().create().toJson(configMap));
                } else {
                    model.addAttribute("configJSON", "");
                }
            } else {
                model.addAttribute("configJSON", "nonLocalEducationAgency");

            }

            addCommonData(model, request);
            model.addAttribute(Constants.PAGE_TO_INCLUDE, DASHBOARD_CONFIG_FTL);
            return new ModelAndView(Constants.OVERALL_CONTAINER_PAGE, model);
        }
        throw new IllegalAccessException("Access Denied");
    }

    /**
     *
     * @deprecated retiring method
     * @param configMap
     * @return
     */
    @Deprecated
    @RequestMapping(value = CONFIG_SAVE_URL, method = RequestMethod.POST)
    @ResponseBody
    public String saveConfig(@RequestBody @Valid ConfigMap configMap) {
        try {
            putCustomConfig(configMap);
        } catch (JsonSyntaxException jse) {
            LOGGER.error("Error saving config", jse);
            return "Permission Denied";
        }
        return "Success";
    }

    public void putCustomConfig(ConfigMap configMap) {
        String token = SecurityUtil.getToken();
        configManager.putCustomConfig(token, userEdOrgManager.getUserEdOrg(token), configMap);
    }

    /**
     * Controller for client side data pulls without id.
     * The 'params' parameter contains a map of url query parameters. The parameters are matched
     * to the attributes in the JSON config files.
     *
     * e.g. /s/c/cfg?type=LAYOUT&id=school
     */
    @RequestMapping(value = "/s/c/cfg", method = RequestMethod.GET)
    @ResponseBody
    public Collection<Config> handleConfigSearch(@RequestParam Map<String, String> params,
            final HttpServletRequest request, HttpServletResponse response) {

        String token = SecurityUtil.getToken();

        // check user is an admin
        Boolean isAdmin = SecurityUtil.isAdmin();

        if (isAdmin != null && isAdmin.booleanValue()) {
            return configManager.getConfigsByAttribute(token, userEdOrgManager.getUserEdOrg(token), params);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }

    }

    /**
     * Save a layout config
     *
     * @param config
     * @return
     */
    @RequestMapping(value = "/s/c/cfg", method = RequestMethod.POST)
    @ResponseBody
    public String saveLayoutConfig(@RequestBody @Valid Config config) {

        try {
            String token = SecurityUtil.getToken();
            configManager.putCustomConfig(token, userEdOrgManager.getUserEdOrg(token), config);
        } catch (JsonSyntaxException jse) {
            LOGGER.error("Error saving config", jse);
            return "Permission Denied";
        }
        return "Success";
    }

    /**
     * Controller to return configs (driver and all edOrg levels), without waterfall logic.
     * The 'params' parameter contains a map of url query parameters. The parameters are matched
     * to the attributes in the JSON config files.
     *
     * @param configType
     * @param request
     * @param response
     * @return DriverConfig and all EdOrg hierarchy JSON.
     */
    @RequestMapping(value = CONFIG_ALL, method = RequestMethod.GET)
    @ResponseBody
    public List<ConfigWrapper> handleConfigPanels(@RequestParam Map<String, String> configType,
            final HttpServletRequest request, HttpServletResponse response) {

        String token = SecurityUtil.getToken();
        Boolean isAdmin = SecurityUtil.isAdmin();
        if (isAdmin != null && isAdmin.booleanValue()) {
            Map<String, Collection<Config>> mapConfigs = configManager.getAllConfigByType(token,
                    userEdOrgManager.getUserEdOrg(token), configType);

            // re-organize config objects. group by Education Agency Name
            Map<String, List<ConfigWrapper>> mapConfigWrappers = new HashMap<String, List<ConfigWrapper>>();
            if (mapConfigs != null) {
                Set<String> edOrgNames = mapConfigs.keySet();
                for (String edOrgName : edOrgNames) {
                    // get Collection of Config by edOrgName
                    Collection<Config> configs = mapConfigs.get(edOrgName);
                    for (Config config : configs) {
                        ConfigWrapper configWrapper = new ConfigWrapper(config);
                        configWrapper.setEducationAgencyName(edOrgName);
                        List<ConfigWrapper> configWrappers = mapConfigWrappers.get(configWrapper.getId());
                        if (configWrappers == null) {
                            configWrappers = new LinkedList<ConfigWrapper>();
                            mapConfigWrappers.put(configWrapper.getId(), configWrappers);
                        }
                        configWrappers.add(configWrapper);
                    }
                }
            }

            // make a single array of ConfigWrapper
            List<ConfigWrapper> listConfigWrapper = new LinkedList<ConfigWrapper>();
            for (String idName : mapConfigWrappers.keySet()) {
                listConfigWrapper.addAll(mapConfigWrappers.get(idName));
            }

            // make alphabetical order (1st. Config.id 2nd. EdOrgName)
            Collections.sort(listConfigWrapper);
            return listConfigWrapper;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return null;
    }

    /**
     * Config Wrapper class for client.
     *
     * @author tosako
     *
     */
    protected class ConfigWrapper extends Config implements Comparable<ConfigWrapper> {
        private String educationAgencyName;

        public ConfigWrapper(Config config) {
            super(config.getId(), config.getParentId(), config.getName(), config.getType(), config.getCondition(),
                    config.getData(), config.getItems(), config.getRoot(), config.getParams());
        }

        public String getEducationAgencyName() {
            return this.educationAgencyName;
        }

        public void setEducationAgencyName(String configName) {
            this.educationAgencyName = configName;
        }

        // make alphabetical order (1st. Config.id 2nd. EdOrgName)
        @Override
        public int compareTo(ConfigWrapper o) {
            int compare = this.id.compareTo(o.id);
            if (compare == 0) {
                compare = this.educationAgencyName.compareTo(o.educationAgencyName);
            }
            return compare;
        }
    }
}

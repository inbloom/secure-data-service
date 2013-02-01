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

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.dashboard.entity.ModelAndViewConfig;
import org.slc.sli.dashboard.manager.PortalWSManager;
import org.slc.sli.dashboard.manager.component.CustomizationAssemblyFactory;
import org.slc.sli.dashboard.util.Constants;
import org.slc.sli.dashboard.util.JsonConverter;
import org.slc.sli.dashboard.util.SecurityUtil;

/**
 * Controller for all types of requests.
 *
 * @author dwu
 */
@Controller
public abstract class GenericLayoutController {
    protected static final Logger LOGGER = LoggerFactory.getLogger(GenericLayoutController.class);
    private static final String LAYOUT_DIR = "layout/";
    private static final String FTL_EXTENSION = ".ftl";
    private CustomizationAssemblyFactory customizationAssemblyFactory;

    private static final String GOOGLE_ANALYTICS_TRACKER_CONSTANT = "googleAnalyticsTrackerId";
    private static final String MINIFY_JS_CONSTANT = "minifyJs";

    @Autowired
    @Qualifier("googleAnalyticsTrackerId")
    private String googleAnalyticsTrackerId;


    @Autowired
    @Qualifier(MINIFY_JS_CONSTANT)
    private Boolean minifyJs;

    protected PortalWSManager portalWSManager;


    protected ModelMap getPopulatedModel(String layoutId, Object entityKey, HttpServletRequest request) {
        return getPopulatedModel(layoutId, entityKey, request, false);
    }

    /**
     * Populate layout model according to layout defined config for a user/context domain
     *
     * @param layoutId
     *            - unique id of the layout
     * @param entityId
     *            - entity id to pass to the child panels
     * @return
     */
    protected ModelMap getPopulatedModel(String layoutId, Object entityKey, HttpServletRequest request, boolean lazyOverride) {

        // set up model map
        ModelMap model = new ModelMap();
        ModelAndViewConfig modelAndConfig =
                customizationAssemblyFactory.getModelAndViewConfig(layoutId, entityKey, lazyOverride);
        model.addAttribute(Constants.MM_COMPONENT_ID, layoutId);
        model.addAttribute(Constants.MM_ENTITY_ID, entityKey);
        model.addAttribute(Constants.MM_KEY_VIEW_CONFIGS, modelAndConfig.getConfig());
        model.addAttribute(Constants.MM_KEY_LAYOUT, modelAndConfig.getLayoutItems());
        model.addAttribute(Constants.MM_KEY_DATA, modelAndConfig.getData());
        model.addAttribute(Constants.MM_VIEW_DATA_CONFIG_JSON, JsonConverter.toJson(modelAndConfig));

        model.addAttribute(Constants.MM_KEY_LOGGER, LOGGER);
        addCommonData(model, request);
        populateModelLegacyItems(model);
        return model;
    }

    protected void addHeaderFooter(ModelMap model) {
        boolean isAdmin = isAdmin();
        String header = portalWSManager.getHeader(isAdmin);
        if (header != null) {
            header = header.replace("[$USER_NAME$]", SecurityUtil.getUsername());
            model.addAttribute(Constants.ATTR_HEADER_STRING, header);
            model.addAttribute(Constants.ATTR_FOOTER_STRING, portalWSManager.getFooter(isAdmin));
        }
    }

    protected void addCommonData(ModelMap model, HttpServletRequest request) {
        addHeaderFooter(model);
        model.addAttribute(GOOGLE_ANALYTICS_TRACKER_CONSTANT, googleAnalyticsTrackerId);
        model.addAttribute(Constants.CONTEXT_ROOT_PATH,  request.getContextPath());
        model.addAttribute(Constants.CONTEXT_PREVIOUS_PATH,  "javascript:history.go(-1)");
        model.addAttribute(MINIFY_JS_CONSTANT, minifyJs);
    }


    public void populateModelLegacyItems(ModelMap model) {
        model.addAttribute("random", new Random());
    }


    protected String getLayoutView(String layoutName) {
        return LAYOUT_DIR + layoutName +  FTL_EXTENSION;
    }

    protected ModelAndView getModelView(String layoutName, ModelMap model) {
        // Includes the page we want to display in the overall_container page
        model.addAttribute(Constants.PAGE_TO_INCLUDE, getLayoutView(layoutName));
        return new ModelAndView(Constants.OVERALL_CONTAINER_PAGE, model);
    }

    @Autowired
    public void setCustomizedDataFactory(CustomizationAssemblyFactory customizedDataFactory) {
        this.customizationAssemblyFactory = customizedDataFactory;
    }

    @Autowired
    public void setPortalWSManager(PortalWSManager portalWSManager) {
        this.portalWSManager = portalWSManager;
    }

    public String getToken() {
        return SecurityUtil.getToken();
    }

    public boolean isAdmin() {
        return SecurityUtil.isAdmin();
    }
}

package org.slc.sli.web.controller;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.entity.ModelAndViewConfig;
import org.slc.sli.manager.PortalWSManager;
import org.slc.sli.manager.component.CustomizationAssemblyFactory;
import org.slc.sli.util.Constants;
import org.slc.sli.util.JsonConverter;
import org.slc.sli.util.SecurityUtil;

/**
 * Controller for all types of requests.
 *
 * TODO: Refactor methods to be private and mock in unit tests with PowerMockito
 *
 * @author dwu
 */
@Controller
public abstract class GenericLayoutController {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private static final String LAYOUT_DIR = "layout/";
    private static final String FTL_EXTENSION = ".ftl";
    private CustomizationAssemblyFactory customizationAssemblyFactory;

    private static final String GOOGLE_ANALYTICS_TRACKER_CONSTANT = "googleAnalyticsTrackerId";

    @Autowired
    @Qualifier("googleAnalyticsTrackerId")
    private String googleAnalyticsTrackerId;



    protected PortalWSManager portalWSManager;


    /**
     * Populate layout model according to layout defined config for a user/context domain
     *
     * @param layoutId
     *            - unique id of the layout
     * @param entityId
     *            - entity id to pass to the child panels
     * @return
     */
    protected ModelMap getPopulatedModel(String layoutId, Object entityKey, HttpServletRequest request) {

        // set up model map
        ModelMap model = new ModelMap();
        ModelAndViewConfig modelAndConfig =
                customizationAssemblyFactory.getModelAndViewConfig(layoutId, entityKey);
        model.addAttribute(Constants.MM_COMPONENT_ID, layoutId);
        model.addAttribute(Constants.MM_ENTITY_ID, entityKey);
        model.addAttribute(Constants.MM_KEY_VIEW_CONFIGS, modelAndConfig.getConfig());
        model.addAttribute(Constants.MM_KEY_LAYOUT, modelAndConfig.getLayoutItems());
        model.addAttribute(Constants.MM_KEY_DATA, modelAndConfig.getData());
        model.addAttribute(Constants.MM_VIEW_DATA_CONFIG_JSON, JsonConverter.toJson(modelAndConfig));

        model.addAttribute(Constants.MM_KEY_LOGGER, logger);
        addCommonData(model, request);
        // TODO: refactor so the below params can be removed
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
    }


    // TODO: refactor so the below params can be removed
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
        try {
            return SecurityUtil.isAdmin();
        } catch (Throwable t) {
            return false;
        }

    }
}

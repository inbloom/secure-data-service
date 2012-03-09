package org.slc.sli.controller;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.config.DisplaySet;
import org.slc.sli.config.LozengeConfig;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.manager.ConfigManager;
import org.slc.sli.manager.component.CustomizationAssemblyFactory;
import org.slc.sli.util.Constants;
import org.slc.sli.util.DashboardException;
import org.slc.sli.util.SecurityUtil;
import org.slc.sli.view.LozengeConfigResolver;
import org.slc.sli.view.widget.WidgetFactory;

/**
 * Controller for all types of requests.
 * 
 * @author dwu
 */
@Controller
public abstract class GenericLayoutController {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final String LAYOUT_DIR = "layout/";
    
    private ConfigManager configManager;
    private CustomizationAssemblyFactory customizationAssemblyFactory;
    
    /**
     * Populate layout model according to layout defined config for a user/context domain
     * 
     * @param layoutId
     *            - unique id of the layout
     * @param entityId
     *            - entity id to pass to the child panels
     * @return
     */
    protected ModelMap getPopulatedModel(String layoutId, Object entityId) {
        
        UserDetails user = SecurityUtil.getPrincipal();
        
        // get the list of all available viewConfigs
        List<ViewConfig> viewConfigs = configManager.getConfigsWithType(SecurityUtil.getToken(), layoutId);
        
        // set up model map
        ModelMap model = new ModelMap();
        model.addAttribute(Constants.MM_KEY_VIEW_CONFIGS, viewConfigs);
        for (ViewConfig viewConfig : viewConfigs) {
            if (viewConfig.getDisplaySet() != null) {
                for (DisplaySet panel : viewConfig.getDisplaySet()) {
                    try {
                        model.addAttribute(panel.getDisplayName(),
                                customizationAssemblyFactory.getDataComponent(panel.getDisplayName(), entityId));
                    } catch (DashboardException de) {
                        logger.error("Unable to populate model for panel", de);
                    }
                }
            }
        }
        
        model.addAttribute(Constants.MM_KEY_WIDGET_FACTORY, new WidgetFactory());
        List<LozengeConfig> lozengeConfig = configManager.getLozengeConfig(user.getUsername());
        model.addAttribute(Constants.MM_KEY_LOZENGE_CONFIG, new LozengeConfigResolver(lozengeConfig));
        model.addAttribute("random", new Random());
        return model;
    }
    
    protected String getLayoutView(String layoutName) {
        return LAYOUT_DIR + layoutName;
    }
    
    protected ModelAndView getModelView(String layoutName, ModelMap model) {
        return new ModelAndView(getLayoutView(layoutName), model);
    }
    
    @Autowired
    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }
    
    @Autowired
    public void setCustomizedDataFactory(CustomizationAssemblyFactory customizedDataFactory) {
        this.customizationAssemblyFactory = customizedDataFactory;
    }
}

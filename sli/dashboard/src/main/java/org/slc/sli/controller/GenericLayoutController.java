package org.slc.sli.controller;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.config.LozengeConfig;
import org.slc.sli.entity.ModelAndViewConfig;
import org.slc.sli.manager.ConfigManager;
import org.slc.sli.manager.component.CustomizationAssemblyFactory;
import org.slc.sli.util.Constants;
import org.slc.sli.util.JsonConverter;
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
    protected Logger logger = LoggerFactory.getLogger(getClass());
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
    protected ModelMap getPopulatedModel(String layoutId, Object entityKey) {
        
        // set up model map
        ModelMap model = new ModelMap();
        ModelAndViewConfig modelAndConfig =
                customizationAssemblyFactory.getModelAndViewConfig(layoutId, entityKey);
        model.addAttribute(Constants.MM_KEY_VIEW_CONFIGS, modelAndConfig.getComponentViewConfigMap());
        model.addAttribute(Constants.MM_KEY_VIEW_CONFIGS_JSON, JsonConverter.toJson(modelAndConfig.getComponentViewConfigMap()));
        model.addAttribute(Constants.MM_KEY_LAYOUT, modelAndConfig.getLayoutItems());
        model.addAttribute(Constants.MM_KEY_DATA, modelAndConfig.getData());
        model.addAttribute(Constants.MM_KEY_DATA_JSON, JsonConverter.toJson(modelAndConfig.getData()));
        
        // TODO: refactor so the below params can be removed
        model.addAttribute(Constants.MM_KEY_WIDGET_FACTORY, new WidgetFactory());
        List<LozengeConfig> lozengeConfig = configManager.getLozengeConfig(SecurityUtil.getUsername());
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
    
    private static final String DEFAULT_MESSAGE = "An error had occurred. Please try again later.";
    
    @ExceptionHandler(Throwable.class)
    public ModelAndView handleThrowable(Throwable t) {
        logger.error("An error running layout: ", t);
        return new ModelAndView("error", "error", DEFAULT_MESSAGE);
    }
}

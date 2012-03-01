package org.slc.sli.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.config.ViewConfig;
import org.slc.sli.manager.ConfigManager;
import org.slc.sli.util.Constants;
import org.slc.sli.util.SecurityUtil;

/**
 * 
 * 
 * @author dwu
 */
@Controller
@RequestMapping(value = "/service/layout/")
public class GenericController {

    private static final String LAYOUT = "layout/";

    private ConfigManager configManager;
    
    /**
     * Controller for student profile
     * 
     * @param panelIds
     * @return
     */
    @RequestMapping(value = "/student", method = RequestMethod.GET)
    public ModelAndView handleStudentProfile(@RequestParam String id) {
        
        UserDetails user = SecurityUtil.getPrincipal();
        
        // get the list of all available viewConfigs
        List<ViewConfig> viewConfigs = configManager.getConfigsWithType(user.getUsername(), Constants.VIEW_TYPE_STUDENT_PROFILE_PAGE);
        
        // set up model map
        ModelMap model = new ModelMap();
        model.addAttribute(Constants.MM_KEY_VIEW_CONFIGS, viewConfigs);

        return new ModelAndView(LAYOUT + "studentProfile", model);
    }
    
    
    @Autowired
    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }
}

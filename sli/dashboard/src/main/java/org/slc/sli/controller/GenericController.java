package org.slc.sli.controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.config.ViewConfig;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.ConfigManager;
import org.slc.sli.manager.PopulationManager;
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

    private static final String LAYOUT_DIR = "layout/";
    private static final String TABBED_ONE_COL = "tabbed_one_col";
    
    private ConfigManager configManager;
    private PopulationManager populationManager;
    

    /**
     * Controller for student profile
     * 
     * @param panelIds
     * @return
     */
    @RequestMapping(value = "/student", method = RequestMethod.GET)
    public ModelAndView handleStudentProfile(@RequestParam String id) {
        
        // get the list of all available viewConfigs
        List<ViewConfig> viewConfigs = configManager.getConfigsWithType(SecurityUtil.getToken(), Constants.VIEW_TYPE_STUDENT_PROFILE_PAGE);
        
        // set up model map
        ModelMap model = new ModelMap();
        model.addAttribute(Constants.MM_KEY_VIEW_CONFIGS, viewConfigs);

        // get core student info
        GenericEntity student = populationManager.getStudent(SecurityUtil.getToken(), id);
        String name = ((Map) (student.get(Constants.ATTR_NAME))).get(Constants.ATTR_FIRST_NAME) + " " 
                      + ((Map) (student.get(Constants.ATTR_NAME))).get(Constants.ATTR_LAST_SURNAME);
        model.addAttribute("student", name);

        return new ModelAndView(LAYOUT_DIR + TABBED_ONE_COL, model);

    }
    
    @Autowired
    public void setPopulationManager(PopulationManager populationManager) {
        this.populationManager = populationManager;
    }
    
    @Autowired
    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }
}

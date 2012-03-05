package org.slc.sli.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.PopulationManager;
import org.slc.sli.util.Constants;
import org.slc.sli.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller which returns data for the student profile page
 * 
 */
@Controller
@RequestMapping("/studentprofile")
public class ProfileController extends DashboardController {
    
    private PopulationManager populationManager;
    
    
    public PopulationManager getPopulationManager() {
        return populationManager;
    }
    
    @Autowired
    public void setPopulationManager(PopulationManager populationManager) {
        this.populationManager = populationManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView returnProfile(ModelMap model, String id) {
        List<String> ids = new LinkedList<String>();
        ids.add(id);
        GenericEntity student = populationManager.getStudent(SecurityUtil.getToken(), id);
        String name = ((Map) (student.get(Constants.ATTR_NAME))).get(Constants.ATTR_FIRST_NAME) + " "
                      + ((Map) (student.get(Constants.ATTR_NAME))).get(Constants.ATTR_LAST_SURNAME);
        model.addAttribute("student", name);
        return new ModelAndView("studentProfile");
    }
    
}

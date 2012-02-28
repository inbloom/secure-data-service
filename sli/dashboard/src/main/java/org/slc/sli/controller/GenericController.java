package org.slc.sli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author dwu
 *
 */
@Controller
@RequestMapping(value = "/service/layout/")
public class GenericController {

    private static final String LAYOUT = "layout/";

    /**
     * Controller for student profile
     * 
     * @param panelIds
     * @return
     */
    @RequestMapping(value = "/student", method = RequestMethod.GET)
    public ModelAndView handleStudentProfile(@RequestParam String id,
                                             @RequestParam("pid") String[] panelIds,
                                             ModelMap model) {
           
        // print out params, for debugging
        System.out.println("Student id: " + id);
        for (String panelId : panelIds) {
            System.out.println("Panel id: " + panelId);
        }
        
        // set up model map
        model.addAttribute("panelIds", panelIds);
        
        return new ModelAndView(LAYOUT + "studentProfile");
    }
    
}

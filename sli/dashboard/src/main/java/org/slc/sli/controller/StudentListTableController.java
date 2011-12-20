package org.slc.sli.controller;

import java.io.IOException;

import org.springframework.ui.ModelMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;


/**
 * Controller for showing the list of studentview.  
 * 
 */
@Controller
public class StudentListTableController extends DashboardController {

    public StudentListTableController() { }
    
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView studentListTable(String username, 
                                         String studentUIDs, // don't know what this could be yet... 
                                         ModelMap model) throws IOException {
        //TODO: Make call to actual client instead of mock client, and use a token instead of empty string
        model.addAttribute("studentuids", Arrays.asList(studentUIDs.split(",")));
        return new ModelAndView("studentListTable");
    }
}

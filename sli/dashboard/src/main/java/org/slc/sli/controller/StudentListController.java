package org.slc.sli.controller;

import java.io.IOException;

import com.google.gson.Gson;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.slc.sli.entity.School;

/**
 * 
 * TODO: Write Javadoc
 *
 */
public class StudentListController extends DashboardController {

    // model map keys required by the view for the student list view
    public static final String USER_NAME = "username"; 
    public static final String SCHOOL_LIST = "schoolList"; 

    public StudentListController() { }
    
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView retrieveStudentList(String username, ModelMap model) throws IOException {
        Gson gson = new Gson();
        //TODO: Make call to actual client instead of mock client, and use a token instead of empty string
        School[] schoolList = retrieveSchools(username);
        model.addAttribute(SCHOOL_LIST, gson.toJson(schoolList));
        model.addAttribute(USER_NAME, username);
        return new ModelAndView("studentList");
    }
    
    private School[] retrieveSchools(String token) throws IOException {
        return apiClient.getSchools(token);
    }
    
}

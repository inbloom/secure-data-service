package org.slc.sli.controller;

import java.io.IOException;
import com.google.gson.Gson;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.slc.sli.entity.School;
import org.slc.sli.manager.SchoolManager;
import org.slc.sli.util.SecurityUtil;

/**
 * 
 * Handles a request to display a student list
 *
 */
public class StudentListController extends DashboardController {

    // model map keys required by the view for the student list view
    public static final String USER_NAME = "username"; 
    public static final String SCHOOL_LIST = "schoolList"; 

    private SchoolManager schoolManager;

    public StudentListController() { }
    
    /**
     * Retrieves the school, section, and student information and calls the view to display
     * 
     * @param username
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView retrieveStudentList(String username, ModelMap model) throws IOException {
        Gson gson = new Gson();
        //TODO: Make call to actual client instead of mock client, and use a token instead of empty string
        UserDetails user = SecurityUtil.getPrincipal();
        School[] schoolList = schoolManager.retrieveSchools(user.getUsername());
        model.addAttribute("schoolList", gson.toJson(schoolList));
        model.addAttribute("message", "Hello " + user.getUsername());
        model.addAttribute(SCHOOL_LIST, gson.toJson(schoolList));
        model.addAttribute(USER_NAME, user.getUsername());

        return new ModelAndView("studentList");
    }
    
    /*
     * Getters and setters
     */
    public SchoolManager getSchoolManager() {
        return schoolManager;
    }

    public void setSchoolManager(SchoolManager schoolManager) {
        this.schoolManager = schoolManager;
    }
}

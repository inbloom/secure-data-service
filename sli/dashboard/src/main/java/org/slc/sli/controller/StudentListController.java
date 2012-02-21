package org.slc.sli.controller;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.InstitutionalHierarchyManager;
import org.slc.sli.util.SecurityUtil;

/**
 *
 * Handles a request to display a student list
 *
 */
public class StudentListController extends DashboardController {

    // model map keys required by the view for the student list view
    public static final String USER_NAME = "username";
    public static final String INST_HIERARCHY = "instHierarchy";

    private InstitutionalHierarchyManager institutionalHierarchyManager;

    public StudentListController() { }

    /**
     * Retrieves the edorg, school, section, and student information and calls the view to display
     *
     * @param username
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView retrieveStudentList(ModelMap model) throws IOException {
        //TODO: Make call to actual client instead of mock client, and use a token instead of empty string

        UserDetails user = getPrincipal();
        
        List<GenericEntity> instHierarchy = institutionalHierarchyManager.getInstHierarchy(SecurityUtil.getToken());
        model.addAttribute(INST_HIERARCHY, convertToJson(instHierarchy));
        model.addAttribute(USER_NAME, user.getUsername());

        return new ModelAndView("studentList");
    }

    
    private String convertToJson(List<GenericEntity> list) {
        
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    private UserDetails getPrincipal() {
        return SecurityUtil.getPrincipal();
    }

    /*
     * Getters and setters
     */
    public InstitutionalHierarchyManager getInstitutionalHierarchyManager() {
        return institutionalHierarchyManager;
    }

    public void setInstitutionalHierarchyManager(InstitutionalHierarchyManager institutionalHierarchyManager) {
        this.institutionalHierarchyManager = institutionalHierarchyManager;
    }

}

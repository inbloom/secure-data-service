package org.slc.sli.controller;

import java.io.IOException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.slc.sli.manager.InstitutionalHeirarchyManager;
import org.slc.sli.util.SecurityUtil;

/**
 *
 * Handles a request to display a student list
 *
 */
public class StudentListController extends DashboardController {

    // model map keys required by the view for the student list view
    public static final String USER_NAME = "username";
    public static final String INST_HEIRARCHY = "instHeirarchy";

    private InstitutionalHeirarchyManager institutionalHeirarchyManager;

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

        model.addAttribute(INST_HEIRARCHY, institutionalHeirarchyManager.getInstHeirarchyJSON(SecurityUtil.getToken()));
        model.addAttribute(USER_NAME, user.getUsername());

        return new ModelAndView("studentList");
    }



    private UserDetails getPrincipal() {
        return SecurityUtil.getPrincipal();
    }

    /*
     * Getters and setters
     */
    public InstitutionalHeirarchyManager getInstitutionalHeirarchyManager() {
        return institutionalHeirarchyManager;
    }

    public void setInstitutionalHeirarchyManager(InstitutionalHeirarchyManager institutionalHeirarchyManager) {
        this.institutionalHeirarchyManager = institutionalHeirarchyManager;
    }

}

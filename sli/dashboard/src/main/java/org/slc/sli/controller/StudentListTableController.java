package org.slc.sli.controller;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

import org.slc.sli.config.ConfigUtil;
import org.slc.sli.entity.CustomData;
import org.slc.sli.entity.Assessment;
import org.slc.sli.entity.Student;

import org.slc.sli.config.ViewConfigSet;
import org.slc.sli.config.ViewConfig;

import org.slc.sli.view.AssessmentResolver;

/**
 * Controller for showing the list of studentview.  
 * 
 */
public class StudentListTableController extends DashboardController {

    public StudentListTableController() { }
    
    // model map keys required by the view for the student list view
    public static final String VIEW_CONFIG = "viewConfig"; 
    public static final String ASSESSMENTS = "assessments"; 
    public static final String STUDENTS = "students"; 

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView studentListTable(String username, 
                                         String studentUIDs, // don't know what this could be yet... 
                                         ModelMap model) throws Exception {

        // insert the view object into the modelmap
        CustomData[] viewConfigSets = apiClient.getCustomData(username, "view_config");
        if (viewConfigSets != null && viewConfigSets.length > 0) { 
            ViewConfigSet configSet = ConfigUtil.fromXMLString(viewConfigSets[0].getCustomData()); // the user should be at most one view config??
            for (ViewConfig viewConfig : configSet.getViewConfig()) {
                if (viewConfig.getName().equals("listOfStudents")) {
                    model.addAttribute(VIEW_CONFIG, viewConfig);
                }
            }
        }
        // else { } <-- default view configs go here. 

        // insert the students object into the modelmap
        List<String> uids = Arrays.asList(studentUIDs.split(","));
        List<Student> students = Arrays.asList(apiClient.getStudents(username, uids));
        model.addAttribute(STUDENTS, students);

        // insert the assessments object into the modelmap
        List<Assessment> assessments = Arrays.asList(apiClient.getAssessments(username, uids));
        model.addAttribute(ASSESSMENTS, new AssessmentResolver(assessments));

        return new ModelAndView("studentListTable");
    }
}

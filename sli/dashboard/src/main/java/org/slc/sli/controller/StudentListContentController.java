package org.slc.sli.controller;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

import org.slc.sli.entity.Assessment;
import org.slc.sli.entity.Student;
import org.slc.sli.manager.ConfigManager;

import org.slc.sli.config.ViewConfig;

import org.slc.sli.view.AssessmentResolver;
import org.slc.sli.view.StudentResolver;

/**
 * Controller for showing the list of studentview.  
 * 
 */
public class StudentListContentController extends DashboardController {

    public StudentListContentController() { }
    
    // model map keys required by the view for the student list view
    public static final String VIEW_CONFIG = "viewConfig"; 
    public static final String ASSESSMENTS = "assessments"; 
    public static final String STUDENTS = "students"; 

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView studentListContent(String username, 
                                           String population, // don't know what this could be yet... For now, a list of student uids
                                           ModelMap model) throws Exception {

        // insert the viewConfig object into the modelmap
        ViewConfig viewConfig = ConfigManager.getInstance().getConfigWithType(username, "listOfStudents");
        model.addAttribute(VIEW_CONFIG, viewConfig);  

        // insert the students object into the modelmap
        List<String> uids = Arrays.asList(population.split(","));
        List<Student> students = Arrays.asList(apiClient.getStudents(username, uids));
        model.addAttribute(STUDENTS, new StudentResolver(students, viewConfig));

        // insert the assessments object into the modelmap
        List<Assessment> assessments = Arrays.asList(apiClient.getAssessments(username, uids));
        model.addAttribute(ASSESSMENTS, new AssessmentResolver(assessments, viewConfig));

        return new ModelAndView("studentListContent");
    }
}

package org.slc.sli.controller;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slc.sli.entity.Assessment;
import org.slc.sli.entity.assessmentmetadata.AssessmentMetaData;
import org.slc.sli.entity.Student;
import org.slc.sli.manager.AssessmentManager;
import org.slc.sli.manager.ConfigManager;
import org.slc.sli.manager.StudentManager;

import org.slc.sli.client.LiveAPIClient;
import org.slc.sli.config.ViewConfig;

import org.slc.sli.security.SLIPrincipal;
import org.slc.sli.util.SecurityUtil;

import org.slc.sli.view.AssessmentResolver;
import org.slc.sli.view.StudentResolver;
import org.slc.sli.view.widget.WidgetFactory;

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
    public static final String WIDGET_FACTORY = "widgetFactory";
    
    private LiveAPIClient liveClient;

    public LiveAPIClient getLiveClient() {
        return liveClient;
    }

    public void setLiveClient(LiveAPIClient liveClient) {
        this.liveClient = liveClient;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView studentListContent(String username, 
                                           String population, // don't know what this could be yet... For now, a list of student uids
                                           ModelMap model) throws Exception {

        UserDetails user = SecurityUtil.getPrincipal();
        // insert the viewConfig object into the modelmap
        ViewConfig viewConfig = ConfigManager.getInstance().getConfigWithType(user.getUsername(), "listOfStudents");
        model.addAttribute(VIEW_CONFIG, viewConfig);  

        //TODO: Get student uids from target view.
        // insert the students object into the modelmap
        List<String> uids = null;
        if (population != null)
            uids = Arrays.asList(population.split(","));

        SLIPrincipal userDetails = (SLIPrincipal) SecurityUtil.getPrincipal();
        Student[] studs = liveClient.getStudents(userDetails.getId(), uids);
        //List<Student> students = StudentManager.getInstance().getStudentInfo(user.getUsername(), uids, viewConfig);
        
        model.addAttribute(STUDENTS, new StudentResolver(Arrays.asList(studs)));


        // insert the assessments object into the modelmap
        List<Assessment> assessments = AssessmentManager.getInstance().getAssessments(user.getUsername(), uids, viewConfig);
        List<AssessmentMetaData> assessmentsMetaData = AssessmentManager.getInstance().getAssessmentMetaData(user.getUsername());
        model.addAttribute(ASSESSMENTS, new AssessmentResolver(assessments, assessmentsMetaData));

        // insert a widget factory into the modelmap
        model.addAttribute(WIDGET_FACTORY, new WidgetFactory());
        
        return new ModelAndView("studentListContent");
    }
}

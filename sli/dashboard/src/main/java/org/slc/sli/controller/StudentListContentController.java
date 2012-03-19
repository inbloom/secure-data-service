package org.slc.sli.controller;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedSet;

import freemarker.ext.beans.BeansWrapper;

import org.slc.sli.view.GradebookEntryResolver;
import org.slc.sli.view.HistoricalDataResolver;
import org.slc.sli.view.LozengeConfigResolver;
import org.slc.sli.view.StudentResolver;
import org.slc.sli.view.modifier.HistoricalViewModifier;
import org.slc.sli.view.modifier.ViewModifier;
import org.slc.sli.view.modifier.GradebookViewModifer;
import org.slc.sli.view.AssessmentResolver;
import org.slc.sli.view.AttendanceResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.config.LozengeConfig;
import org.slc.sli.config.StudentFilter;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.ConfigManager;
import org.slc.sli.manager.PopulationManager;
import org.slc.sli.manager.StudentProgressManager;
import org.slc.sli.manager.ViewManager;
import org.slc.sli.util.Constants;
import org.slc.sli.util.SecurityUtil;
import org.slc.sli.view.widget.WidgetFactory;

/**
 * Controller for showing the list of studentview.
 * 
 */
@Controller
@RequestMapping("/studentlistcontent")
public class StudentListContentController extends DashboardController {

    private ConfigManager configManager;
    private PopulationManager populationManager;
    private ViewManager viewManager;
    private StudentProgressManager progressManager;
    
    public StudentListContentController() {
    }
    
    /**
     * Retrieves information for the student list and sends back an html table to be displayed
     * 
     * @param population
     *            Don't know what this could be yet... For now, a list of student uids
     * @param model
     * @param viewIndex
     *            The selected view configuration index
     * @param sessionId
     *            is the id of the session you're in to describe historical data.
     * @return a ModelAndView object
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView studentListContent(@RequestParam(required = false, value = Constants.ATTR_SESSION_ID) String sessionId,
            @RequestParam(required = false, value = Constants.ATTR_COURSE_ID) String selectedCourseId,
            @RequestParam(required = false, value = Constants.ATTR_SECTION_ID) String selectedSectionId,
            String population, Integer viewIndex, Integer filterIndex, ModelMap model) throws Exception {
        
        UserDetails user = SecurityUtil.getPrincipal();
                        
        // get the list of all available viewConfigs
        viewManager.setViewConfigs(configManager.getConfigsWithType(user.getUsername(),
                Constants.VIEW_TYPE_STUDENT_LIST));

        // insert the lozenge config object into modelmap
        List<LozengeConfig> lozengeConfig = configManager.getLozengeConfig(user.getUsername());
        if (lozengeConfig != null)
            model.addAttribute(Constants.MM_KEY_LOZENGE_CONFIG, new LozengeConfigResolver(lozengeConfig));
        
        List<String> uids = new ArrayList<String>();
        if (population != null && !population.isEmpty()) {
            uids = Arrays.asList(population.split(","));
        }
        
        viewManager.setViewConfigs(viewManager.getApplicableViewConfigs(uids, SecurityUtil.getToken()));
        
        if (viewManager.getViewConfigs().size() > 0) {
            
            // add applicable viewConfigs to model map
            model.addAttribute(Constants.MM_KEY_VIEW_CONFIGS, viewManager.getViewConfigs());
            
            ViewConfig viewConfig = viewManager.getViewConfigs().get(viewIndex);
            viewManager.setActiveViewConfig(viewConfig);

            /*Commenting this part out due to performance issues
            * TODO: uncomment
            * */
//            if (viewConfig.getName().equals(Constants.MIDDLE_SCHOOL_VIEW)) {
//
//                HistoricalDataResolver historicalDataResolver = getHistoricalDataResolver(selectedCourseId, uids);
//                model.addAttribute(Constants.MM_KEY_HISTORICAL, historicalDataResolver);
//
//                ViewModifier historicalViewModifier = new HistoricalViewModifier(historicalDataResolver);
//                viewManager.apply(historicalViewModifier);
//
//                GradebookEntryResolver gradebookEntryResolver = getGradebookEntryResolver(selectedSectionId, uids);
//                model.addAttribute(Constants.MM_KEY_GRADEBOOK_ENTRY_DATA, gradebookEntryResolver);
//
//                ViewModifier gradebookViewModifier = new GradebookViewModifer(gradebookEntryResolver);
//                viewManager.apply(gradebookViewModifier);
//
//            }
            
            model.addAttribute(Constants.MM_KEY_VIEW_CONFIG, viewConfig);
            
            // prepare student filter
            List<StudentFilter> studentFilterConfig = configManager.getStudentFilterConfig(user.getUsername());
            model.addAttribute("studentFilters", studentFilterConfig);
            
            if (filterIndex == null) {
                filterIndex = 0;
            }
            String studentFilterName = "";
            if (studentFilterConfig != null) {
                studentFilterName = studentFilterConfig.get(filterIndex).getName();
            }
            
            // get student, program, attendance, and assessment result data
            List<GenericEntity> studentSummaries = populationManager.getStudentSummaries(SecurityUtil.getToken(), uids,
                    viewConfig, sessionId);
            StudentResolver studentResolver = new StudentResolver(studentSummaries);
            studentResolver.filterStudents(studentFilterName);
            
            model.addAttribute(Constants.MM_KEY_STUDENTS, studentResolver);
            
            // insert the assessments object into the modelmap
            List<GenericEntity> assmts = populationManager.getAssessments(SecurityUtil.getToken(), studentSummaries);
            model.addAttribute(Constants.MM_KEY_ASSESSMENTS, new AssessmentResolver(studentSummaries, assmts));
            
            // Get attendance
            model.addAttribute(Constants.MM_KEY_ATTENDANCE, new AttendanceResolver());
            
        }
        
        // insert a widget factory into the modelmap
        model.addAttribute(Constants.MM_KEY_WIDGET_FACTORY, new WidgetFactory());
        
        // let template access Constants
        model.addAttribute(Constants.MM_KEY_CONSTANTS,
                BeansWrapper.getDefaultInstance().getStaticModels().get(Constants.class.getName()));
        
        return new ModelAndView("studentListContent");
    }

    private GradebookEntryResolver getGradebookEntryResolver(String selectedSectionId, List<String> uids) {
        Map<String, Map<String, GenericEntity>> gradebookData = getGradebookData(selectedSectionId, uids);
        SortedSet<GenericEntity> gradebookIds = getGradebookIds(gradebookData);
        GradebookEntryResolver gradebookEntryResolver = new GradebookEntryResolver(gradebookData);
        gradebookEntryResolver.setGradebookIds(gradebookIds);
        return gradebookEntryResolver;
    }

    private SortedSet<GenericEntity> getGradebookIds(Map<String, Map<String, GenericEntity>> gradebookData) {
        return progressManager.retrieveSortedGradebookEntryList(gradebookData);
    }

    private Map<String, Map<String, GenericEntity>> getGradebookData(String selectedSectionId, List<String> uids) {
        return progressManager.getCurrentProgressForStudents(SecurityUtil.getToken(), uids, selectedSectionId);
    }

    private HistoricalDataResolver getHistoricalDataResolver(String selectedCourseId, List<String> uids) {
        Map<String, List<GenericEntity>> historicalData = getHistoricalData(selectedCourseId, uids);
        SortedSet<String> schoolYears = getSchoolYears(historicalData);
        return new HistoricalDataResolver(historicalData, schoolYears, null);
    }

    private SortedSet<String> getSchoolYears(Map<String, List<GenericEntity>> historicalData) {
        return progressManager.applySessionAndTranscriptInformation(
                SecurityUtil.getToken(), historicalData);
    }

    private Map<String, List<GenericEntity>> getHistoricalData(String selectedCourseId, List<String> uids) {
        return progressManager.getStudentHistoricalAssessments(
                            SecurityUtil.getToken(), uids, selectedCourseId);
    }

    /*
    * Getters and setters
    */
    
    @Autowired
    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }
    
    @Autowired
    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }
    
    @Autowired
    public void setPopulationManager(PopulationManager populationManager) {
        this.populationManager = populationManager;
    }
    
    @Autowired
    public void setProgressManager(StudentProgressManager progressManager) {
        this.progressManager = progressManager;
    }
    
}

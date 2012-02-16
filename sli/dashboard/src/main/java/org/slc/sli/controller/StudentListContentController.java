package org.slc.sli.controller;

import java.util.Arrays;
import java.util.List;

import freemarker.ext.beans.BeansWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.config.LozengeConfig;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.assessmentmetadata.AssessmentMetaData;
import org.slc.sli.manager.ConfigManager;
import org.slc.sli.manager.PopulationManager;
import org.slc.sli.manager.ViewManager;
import org.slc.sli.util.Constants;
import org.slc.sli.util.SecurityUtil;
import org.slc.sli.view.AssessmentResolver;
import org.slc.sli.view.LozengeConfigResolver;
import org.slc.sli.view.StudentResolver;
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

    public StudentListContentController() { }

    /**
     * Retrieves information for the student list and sends back an html table to be displayed
     *
     * @param population Don't know what this could be yet... For now, a list of student uids
     * @param model
     * @param viewIndex The selected view configuration index
     * @return a ModelAndView object
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView studentListContent(String population, Integer viewIndex,
                                           ModelMap model) throws Exception {

        UserDetails user = SecurityUtil.getPrincipal();

        // get the list of all available viewConfigs
        List<ViewConfig> viewConfigs = configManager.getConfigsWithType(user.getUsername(), Constants.VIEW_TYPE_STUDENT_LIST);

        // insert the lozenge config object into modelmap
        List<LozengeConfig> lozengeConfig = configManager.getLozengeConfig(user.getUsername());
        model.addAttribute(Constants.MM_KEY_LOZENGE_CONFIG, new LozengeConfigResolver(lozengeConfig));

        List<String> uids = null;
        if (population != null) {
            uids = Arrays.asList(population.split(","));
        }
        
        ViewManager viewManager = new ViewManager(viewConfigs, populationManager);
        List<ViewConfig> applicableViewConfigs = viewManager.getApplicableViewConfigs(uids, user);

        if (applicableViewConfigs.size() > 0) {
            // add applicable viewConfigs to model map
            model.addAttribute(Constants.MM_KEY_VIEW_CONFIGS, applicableViewConfigs);

            ViewConfig viewConfig = applicableViewConfigs.get(viewIndex);
            model.addAttribute(Constants.MM_KEY_VIEW_CONFIG, viewConfig);  

            List<GenericEntity> students = populationManager.getStudentInfo(SecurityUtil.getToken(), uids, viewConfig);
            List<GenericEntity> programs = populationManager.getStudentProgramAssociations(user.getUsername(), uids);
            model.addAttribute(Constants.MM_KEY_STUDENTS, new StudentResolver(students, programs));

            // insert the assessments object into the modelmap
            List<GenericEntity> assessments = populationManager.getAssessments(user.getUsername(), uids, viewConfig);
            List<AssessmentMetaData> assessmentsMetaData = populationManager.getAssessmentMetaData(user.getUsername());
            model.addAttribute(Constants.MM_KEY_ASSESSMENTS, new AssessmentResolver(assessments, assessmentsMetaData));            
        }

        // insert a widget factory into the modelmap
        model.addAttribute(Constants.MM_KEY_WIDGET_FACTORY, new WidgetFactory());

        // let template access Constants
        model.addAttribute(Constants.MM_KEY_CONSTANTS, BeansWrapper.getDefaultInstance().getStaticModels().get(Constants.class.getName()));

        return new ModelAndView("studentListContent");
    }

    @Autowired
    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }
    
    @Autowired
    public void setPopulationManager(PopulationManager populationManager) {
        this.populationManager = populationManager;
    }

}

package org.slc.sli.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.config.ViewConfig;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;
import org.slc.sli.view.modifier.ViewModifier;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Handles the logic behind filtering view configurations
 * @author jstokes
 *
 */
public class ViewManager extends ApiClientManager {
    private List<ViewConfig> viewConfigs;
    private ViewConfig activeViewConfig;
    @Autowired private EntityManager entityManager;

    public ViewManager(List<ViewConfig> viewConfigs) {
        this.viewConfigs = viewConfigs;
    }

    /**
     * Returns all view configs the view manager is handling
     * @return a list of view configs
     */
    public List<ViewConfig> getViewConfigs() {
        return viewConfigs;
    }

    /**
     * Set the list of view configs
     * @param viewConfigs list of configs to set
     */
    public void setViewConfigs(List<ViewConfig> viewConfigs) {
        this.viewConfigs = viewConfigs;
    }
    
    public List<ViewConfig> getApplicableViewConfigs(List<String> uids, String token) {
        Map<String, Integer> gradeValues = getGradeValuesFromCohortYears();   
        List<ViewConfig> applicableViewConfigs = new ArrayList<ViewConfig>();

        List<GenericEntity> students = entityManager.getStudents(token, uids);
        if (students == null) { return applicableViewConfigs; }

        for (ViewConfig viewConfig : viewConfigs) {
            String value = viewConfig.getValue();
            
            if (value != null && value.contains("-")) {
                int seperatorIndex = value.indexOf('-');

                Integer lowerBound = Integer.valueOf(value.substring(0, seperatorIndex));
                Integer upperBound = Integer.valueOf(value.substring(seperatorIndex + 1, value.length()));

                // if we can find at least one student in the range, the viewConfig is applicable
                for (GenericEntity student : students) {
                    Integer gradeValue = gradeValues.get(student.get(Constants.ATTR_GRADE_LEVEL));

                    if (gradeValue == null) { continue; }
                    
                    if ((gradeValue >= lowerBound) && (gradeValue <= upperBound)) {
                        applicableViewConfigs.add(viewConfig);
                        break;
                    }
                }                
            }
        }
        return applicableViewConfigs;
    }

    /**
     * Sets the active (current) view configuration
     * @param viewConfig the view to set as current
     */
    public void setActiveViewConfig(ViewConfig viewConfig) {
        this.activeViewConfig = viewConfig;
    }

    /**
     * Get the active view configuration
     * @return active view configuration
     */
    public ViewConfig getActiveConfig() {
        return activeViewConfig;
    }

    /**
     * Apply modifications to the current view configuration
     * @param viewModifier A view modifier class that manipulates the current view
     */
    public void apply(ViewModifier viewModifier) {
        this.activeViewConfig = viewModifier.modify(this.activeViewConfig);
    }
    
    /**
    * This is just placeholder code until we can get an actual numerical 
    * grade value from the data model. Right now all we have to work with 
    * is the text representation as the cohortYear, so map that to numbers
    */
    private Map<String, Integer> getGradeValuesFromCohortYears() {
        Map<String, Integer> gradeValues = new HashMap<String, Integer>();
        gradeValues.put("First grade", 1);
        gradeValues.put("Second grade", 2);
        gradeValues.put("Third grade", 3);
        gradeValues.put("Fourth grade", 4);
        gradeValues.put("Fifth grade", 5);
        gradeValues.put("Sixth grade", 6);
        gradeValues.put("Seventh grade", 7);
        gradeValues.put("Eighth grade", 8);
        gradeValues.put("Ninth grade", 9);
        gradeValues.put("Tenth grade", 10);
        gradeValues.put("Eleventh grade", 11);
        gradeValues.put("Twelfth grade", 12);
        gradeValues.put("Not Available", -1);
        return gradeValues;
    }
}

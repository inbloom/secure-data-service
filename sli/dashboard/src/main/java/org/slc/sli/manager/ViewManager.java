package org.slc.sli.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;

import org.slc.sli.config.ViewConfig;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;

/**
 * Handles the logic behind filtering view configurations
 * @author jstokes
 *
 */
public class ViewManager extends Manager {
    List<ViewConfig> viewConfigs;
    PopulationManager populationManager;
    
    public ViewManager(List<ViewConfig> viewConfigs, PopulationManager populationManager) {
        this.viewConfigs = viewConfigs;
        this.populationManager = populationManager;
    }
    
    public List<ViewConfig> getApplicableViewConfigs(List<String> uids, UserDetails user) {
        // TODO: remove once we can get numerical grade values from data model                                               
        Map<String, Integer> gradeValues = getGradeValuesFromCohortYears();   
        ArrayList<ViewConfig> applicableViewConfigs = new ArrayList<ViewConfig>();
        
        for (ViewConfig viewConfig : viewConfigs) {
            String value = viewConfig.getValue();
            if (value != null && value.contains("-")) {
                int seperatorIndex = value.indexOf('-');

                Integer lowerBound = Integer.valueOf(value.substring(0, seperatorIndex));
                Integer upperBound = Integer.valueOf(value.substring(seperatorIndex + 1, value.length()));
                List<GenericEntity> students = populationManager.getStudentInfo(user.getUsername(), uids, viewConfig);

                // if we can find at least one student in the range, the viewConfig is applicable
                for (GenericEntity student : students) {
                    Integer gradeValue = gradeValues.get(student.get(Constants.ATTR_COHORT_YEAR));

                    if (gradeValue.compareTo(lowerBound) >= 0 && gradeValue.compareTo(upperBound) <= 0)
                    {
                        applicableViewConfigs.add(viewConfig);
                        break;
                    }
                }                
            }
        }
        return applicableViewConfigs;
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
        return gradeValues;
    } 
}

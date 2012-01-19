package org.slc.sli.view.widget;

import org.slc.sli.config.Field;
import org.slc.sli.config.LozengeConfig;
import org.slc.sli.entity.Student;
import org.slc.sli.view.StudentResolver;
import org.slc.sli.view.LozengeConfigResolver;
import java.util.List;
import java.util.ArrayList;

/**
 * Logic used by a widget that displays lozenges for a student 
 * 
 * @author syau
 */
public class Lozenge {

    // list of lozenges that need to be displayed
    private List<LozengeConfig> lozenges;

    public Lozenge(Field field, Student student, StudentResolver students, LozengeConfigResolver lozengeConfigs) {
        lozenges = new ArrayList<LozengeConfig>();

        int maxCount = Integer.MAX_VALUE;
        try {
            maxCount = Integer.parseInt(field.getLozenges().getMaxCount());
        } catch (Exception e) { 
            // lozenges and max count are not required fields, so it is possible to not have them defined.
            e.printStackTrace();
        }  
        
        // Find the lozenges that needs to be displayed and put them into a list 
        if (field.getLozenges() != null 
            && field.getLozenges().getNames() != null) {
            String lozengeNames = field.getLozenges().getNames();
            String[] lozengeNamesArr = lozengeNames.split("\\s+");
            for (int i = 0; i < lozengeNamesArr.length; i++) {
                String lozengeName = lozengeNamesArr[i];
                if (students.lozengeApplies(student, lozengeName)) {
                    LozengeConfig config = lozengeConfigs.get(lozengeName);
                    if (config != null) {
                        lozenges.add(config);
                        if (lozenges.size() > maxCount) {
                            break;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Get the number of lozenges to be displayed
     */
    public int getNumLozenges() { return lozenges.size(); }
    /**
     * Returns the text to display
     */
    public LozengeConfig getLozenge(int i) { return lozenges.get(i); }
    
}

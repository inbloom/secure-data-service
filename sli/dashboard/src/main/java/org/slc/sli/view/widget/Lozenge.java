package org.slc.sli.view.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slc.sli.config.Field;
import org.slc.sli.config.LozengeConfig;
import org.slc.sli.view.LozengeConfigResolver;
import org.slc.sli.view.StudentResolver;

/**
 * Logic used by a widget that displays lozenges for a student
 *
 * @author syau
 */
public class Lozenge {

    // list of lozenges that need to be displayed
    private List<LozengeConfig> lozenges;


    // Constructor: process the field and determine which lozenge needs to be displayed. 
    public Lozenge(Field field, Map student, StudentResolver students, LozengeConfigResolver lozengeConfigs) {

        lozenges = new ArrayList<LozengeConfig>();

        int maxCount = Integer.MAX_VALUE;

        // it is possible for a field not to have lozenges.
        if (field.getLozenges() == null) { return; }

        // check the max count, if it exists.
        if (field.getLozenges().getMaxCount() != null) {
            maxCount = field.getLozenges().getMaxCount().intValue();
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
     * Returns the lozenge to display
     */
    public LozengeConfig get(int i) { return lozenges.get(i); }

}

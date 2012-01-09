package org.slc.sli.view;

import org.slc.sli.entity.Assessment;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A static class for views in SLI dashboard to perform "timed" business logics
 * 
 * @author syau
 *
 */
public class TimedLogic {

    // These implementations are *all* temporary. 
    // We can implement this only after we have a final spec from the API team on what the 
    // Assessment entity really looks like. For now we're just going by the mock assessment entity. 

    /**
     * Returns the assessment with the most recent timestamp
     */
    public static Assessment getMostRecentAssessment(List<Assessment> a) {
        Collections.sort(a, new Comparator<Assessment>() {
            // this should probably get more precise if we actually have an actual timestamp!
            public int compare(Assessment o1, Assessment o2) {
                return o2.getYear() - o1.getYear();  
            }
        });
        return a.get(0);
    }

    /**
     * Returns the assessment with the highest score
     */
    public static Assessment getHighestEverAssessment(List<Assessment> a) {
        Collections.sort(a, new Comparator<Assessment>() {
            public int compare(Assessment o1, Assessment o2) {
                return o2.getScaleScore() - o1.getScaleScore();
            }
        });
        return a.get(0);
    }

    /**
     * Returns the assessment from the latest window
     */
    // For now, just pretend all assessments are administered once a year between windowStart and windowEnd
    public static Assessment getMostRecentAssessmentWindow(List<Assessment> a, 
                                                           AssessmentMetaDataResolver metaDataResolver,
                                                           String assmtName) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        
        // if the window has already passed in the current year, then the latest window is in this year. 
        // Otherwise it's the last year. 
        String windowEndDate = metaDataResolver.findWindowEndDateForFamily(assmtName);
        int windowEndMonth = Integer.parseInt(windowEndDate.split("/")[0]);
        int windowEndDay = Integer.parseInt(windowEndDate.split("/")[1]);
        Calendar thisYearWindowEndDate = new GregorianCalendar(currentYear, windowEndMonth, windowEndDay);
        
        int year = thisYearWindowEndDate.before(Calendar.getInstance()) ? currentYear : currentYear - 1; 
        
        for (Assessment ass : a) {
            if (ass.getYear() == year) {
                return ass;
            }
        }
        return null;
    }

}

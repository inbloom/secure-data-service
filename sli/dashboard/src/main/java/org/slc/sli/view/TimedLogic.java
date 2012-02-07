package org.slc.sli.view;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.assessmentmetadata.Period;

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
    public static GenericEntity getMostRecentAssessment(List<GenericEntity> a) {
        Collections.sort(a, new Comparator<GenericEntity>() {
            // this should probably get more precise if we actually have an actual timestamp!
            public int compare(GenericEntity o1, GenericEntity o2) {
                return (Integer.parseInt((String)(o2.get("year"))) - (Integer.parseInt((String)(o1.get("year")))));  
            }
        });
        return a.get(0);
    }

    /**
     * Returns the assessment with the highest score
     */
    public static GenericEntity getHighestEverAssessment(List<GenericEntity> a) {
        Collections.sort(a, new Comparator<GenericEntity>() {
            public int compare(GenericEntity o1, GenericEntity o2) {
                return (Integer.parseInt((String)(o2.get("scaleScore"))) - (Integer.parseInt((String)(o1.get("scaleScore")))));
            }
        });
        return a.get(0);
    }

    /**
     * Returns the assessment from the most recent window of the given assessment family
     */
    // For now, just pretend all assessments are administered once a year between windowStart and windowEnd
    public static GenericEntity getMostRecentAssessmentWindow(List<GenericEntity> a, 
                                                           AssessmentMetaDataResolver metaDataResolver,
                                                           String assmtName) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        
        // if the window has already passed in the current year, then the latest window is in this year. 
        // Otherwise it's the last year.

        // First, find the "most recent window".
        List<Period> familyPeriods = metaDataResolver.findPeriodsForFamily(assmtName);
        if (familyPeriods == null || familyPeriods.isEmpty()) {
            return null;
        }
        Collections.sort(familyPeriods, new Comparator<Period>() {
            public int compare(Period p1, Period p2) {
                int windowEnd1Month = Integer.parseInt(p1.getWindowEnd().split("/")[0]);
                int windowEnd1Day = Integer.parseInt(p1.getWindowEnd().split("/")[1]);
                int windowEnd2Month = Integer.parseInt(p2.getWindowEnd().split("/")[0]);
                int windowEnd2Day = Integer.parseInt(p2.getWindowEnd().split("/")[1]);
                if (windowEnd1Month != windowEnd2Month) {
                    return windowEnd1Month - windowEnd2Month;
                } else {
                    return windowEnd1Day - windowEnd2Day;
                }
                
            }
        });
        Period mostRecentPeriod = familyPeriods.get(familyPeriods.size() - 1); // start with last period of last year
        int year = currentYear - 1;
        // iterate through all periods for this year chronologically and find the last one that is before the current date. 
        for (Period p : familyPeriods) {
            int windowEndMonth = Integer.parseInt(p.getWindowEnd().split("/")[0]);
            int windowEndDay = Integer.parseInt(p.getWindowEnd().split("/")[1]);
            Calendar thisYearWindowEndDate = new GregorianCalendar(currentYear, windowEndMonth, windowEndDay);
            if (thisYearWindowEndDate.before(Calendar.getInstance())) {
                mostRecentPeriod = p;
                year = currentYear;
            }
        }
        
        for (GenericEntity  ass : a) {
            if (Integer.parseInt((String)(ass.get("year"))) == year 
                &&  metaDataResolver.findPeriodForFamily((String) (ass.get("assessmentName"))) == mostRecentPeriod) {
                return ass;
            }
        }
        return null;
    }

}

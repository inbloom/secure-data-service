package org.slc.sli.view;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A static class for views in SLI dashboard to perform "timed" business logics
 * 
 * @author syau
 * 
 */
public class TimedLogic {

    private static Logger logger = LoggerFactory.getLogger(TimedLogic.class);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Returns the assessment with the most recent timestamp
     */
    public static GenericEntity getMostRecentAssessment(List<GenericEntity> a) {

        Collections.sort(a, new Comparator<GenericEntity>() {
            
            public int compare(GenericEntity o1, GenericEntity o2) {
                
                try {
                    Date d1 = dateFormat.parse(o1.getString(Constants.ATTR_ADMIN_DATE));
                    Date d2 = dateFormat.parse(o2.getString(Constants.ATTR_ADMIN_DATE));
                    return d2.compareTo(d1);
                    
                } catch (Exception e) {
                    logger.error("Date compare error");
                    return 0;
                }
            }
        });

        // TODO: is this necessary? we don't really want to create a new generic entity
        return new GenericEntity(a.get(0));
    }

    /**
     * Returns the assessment with the highest score
     */
    public static GenericEntity getHighestEverAssessment(List<GenericEntity> a) {
        
        Collections.sort(a, new Comparator<GenericEntity>() {
            
            public int compare(GenericEntity o1, GenericEntity o2) {
                List<Map<String, String>> scoreResults1 = o1.getList(Constants.ATTR_SCORE_RESULTS);
                List<Map<String, String>> scoreResults2 = o2.getList(Constants.ATTR_SCORE_RESULTS);
                String score1 = "";
                String score2 = "";
                for (Map<String, String> scoreResult : scoreResults1) {
                    if (scoreResult.get(Constants.ATTR_ASSESSMENT_REPORTING_METHOD).equals(Constants.ATTR_SCALE_SCORE)) {
                        score1 = scoreResult.get(Constants.ATTR_RESULT);
                    }
                }
                
                for (Map<String, String> scoreResult : scoreResults2) {
                    if (scoreResult.get(Constants.ATTR_ASSESSMENT_REPORTING_METHOD).equals(Constants.ATTR_SCALE_SCORE)) {
                        score2 = scoreResult.get(Constants.ATTR_RESULT);
                    }
                }

                return Integer.parseInt(score2) - Integer.parseInt(score1);
            }
        });
        return a.get(0);
    }

    /**
     * Returns the assessment from the most recent window of the given assessment family
     */
    // For now, just pretend all assessments are administered once a year between windowStart and
    // windowEnd
    public static GenericEntity getMostRecentAssessmentWindow(List<GenericEntity> a,
            AssessmentMetaDataResolver metaDataResolver, String assmtName) {
        
        /*
         * int currentYear = Calendar.getInstance().get(Calendar.YEAR);
         * 
         * // if the window has already passed in the current year, then the latest window is in
         * this year.
         * // Otherwise it's the last year.
         * 
         * // First, find the "most recent window".
         * List<Period> familyPeriods = metaDataResolver.findPeriodsForFamily(assmtName);
         * if (familyPeriods == null || familyPeriods.isEmpty()) {
         * return null;
         * }
         * Collections.sort(familyPeriods, new Comparator<Period>() {
         * public int compare(Period p1, Period p2) {
         * int windowEnd1Month = Integer.parseInt(p1.getWindowEnd().split("/")[0]);
         * int windowEnd1Day = Integer.parseInt(p1.getWindowEnd().split("/")[1]);
         * int windowEnd2Month = Integer.parseInt(p2.getWindowEnd().split("/")[0]);
         * int windowEnd2Day = Integer.parseInt(p2.getWindowEnd().split("/")[1]);
         * if (windowEnd1Month != windowEnd2Month) {
         * return windowEnd1Month - windowEnd2Month;
         * } else {
         * return windowEnd1Day - windowEnd2Day;
         * }
         * 
         * }
         * });
         * Period mostRecentPeriod = familyPeriods.get(familyPeriods.size() - 1); // start with last
         * period of last year
         * int year = currentYear - 1;
         * // iterate through all periods for this year chronologically and find the last one that
         * is before the current date.
         * for (Period p : familyPeriods) {
         * int windowEndMonth = Integer.parseInt(p.getWindowEnd().split("/")[0]);
         * int windowEndDay = Integer.parseInt(p.getWindowEnd().split("/")[1]);
         * Calendar thisYearWindowEndDate = new GregorianCalendar(currentYear, windowEndMonth,
         * windowEndDay);
         * if (thisYearWindowEndDate.before(Calendar.getInstance())) {
         * mostRecentPeriod = p;
         * year = currentYear;
         * }
         * }
         * 
         * for (GenericEntity ass : a) {
         * if (Integer.parseInt((String) (ass.get(Constants.ATTR_YEAR))) == year
         * && metaDataResolver.findPeriodForFamily((String)
         * (ass.get(Constants.ATTR_ASSESSMENT_NAME))) == mostRecentPeriod) {
         * 
         * return ass;
         * }
         * }
         */
        return null;
    }

}

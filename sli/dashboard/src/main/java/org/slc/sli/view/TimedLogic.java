package org.slc.sli.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;

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
     * Most recent window is defined as:
     * - assessment with lastest beginDate that is not in the future
     * - if there is no assessment window, most recent studentAssessment admin date is used
     */
    public static GenericEntity getMostRecentAssessmentWindow(Collection<GenericEntity> results,
            Collection<GenericEntity> assessmentMetaData) {
        
        AssessmentPeriod window = getMostRecentWindow(assessmentMetaData);
        
        if (window != null) {
            GenericEntity best = null;
            for (GenericEntity studentAssessment : results) {
                String date = studentAssessment.getString(Constants.ATTR_ADMIN_DATE);
                if (window.beginDate.compareTo(date) <= 0 && window.endDate.compareTo(date) >= 0) {
                    if (best == null || date.compareTo(best.getString(Constants.ATTR_ADMIN_DATE)) > 0) {
                        best = studentAssessment;
                    }
                }
            }
            return best;
        } else {
            GenericEntity mostRecent = null;
            for (GenericEntity studentAssessment : results) {
                String date = studentAssessment.getString(Constants.ATTR_ADMIN_DATE);
                if (date != null
                        && (mostRecent == null || date.compareTo(mostRecent.getString(Constants.ATTR_ADMIN_DATE)) >= 0)) {
                    mostRecent = studentAssessment;
                }
            }
            return mostRecent;
        }
    }
    
    private static AssessmentPeriod getMostRecentWindow(Collection<GenericEntity> assessmentMetaData) {
        String now = javax.xml.bind.DatatypeConverter.printDate(Calendar.getInstance());
        List<AssessmentPeriod> periods = new ArrayList<AssessmentPeriod>();
        for (GenericEntity assessment : assessmentMetaData) {
            @SuppressWarnings("unchecked")
            Map<String, String> periodDescriptor = (Map<String, String>) assessment
                    .getMap(Constants.ATTR_ASSESSMENT_PERIOD_DESCRIPTOR);
            if (periodDescriptor == null) {
                continue;
            }
            String beginDate = periodDescriptor.get(Constants.ATTR_ASSESSMENT_PERIOD_BEGIN_DATE);
            String endDate = periodDescriptor.get(Constants.ATTR_ASSESSMENT_PERIOD_END_DATE);
            if (beginDate == null || endDate == null) {
                continue;
            }
            
            // ignore any assessment periods in the future
            if (now.compareTo(beginDate) < 0) {
                continue;
            }
            
            AssessmentPeriod period = new AssessmentPeriod(assessment, beginDate, endDate);
            periods.add(period);
            
        }
        Collections.sort(periods);
        if (periods.size() > 0) {
            return periods.get(0);
        } else {
            return null;
        }
    }
    
    /**
     * Sortable assessment period. Protected level for unit tests.
     */
    protected static class AssessmentPeriod implements Comparable<AssessmentPeriod> {
        final GenericEntity assessment;
        final String beginDate;
        final String endDate;
        
        public AssessmentPeriod(GenericEntity assessment, String beginDate, String endDate) {
            this.assessment = assessment;
            this.beginDate = beginDate;
            this.endDate = endDate;
        }
        
        /**
         * -1 means more recent, 1 means older
         */
        @Override
        public int compareTo(AssessmentPeriod other) {
            if (other == null) {
                return -1;
            } else if (beginDate.compareTo(other.endDate) > 0) {
                return -1;
            } else if (other.beginDate.compareTo(endDate) > 0) {
                return 1;
            } else if (beginDate.compareTo(other.beginDate) > 0) {
                return -1;
            } else if (other.beginDate.compareTo(beginDate) > 0) {
                return 1;
            } else if (endDate.compareTo(other.endDate) > 0) {
                return 1;
            } else if (other.endDate.compareTo(endDate) > 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }
    
}

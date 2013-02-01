/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.dashboard.util;

import java.text.ParseException;
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

/**
 * A static class for views in SLI dashboard to perform "timed" business logics
 *
 * @author syau
 *
 */
public class TimedLogic {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimedLogic.class);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     *
     */
    public enum TimeSlot {
        MOST_RECENT_WINDOW, MOST_RECENT_RESULT, HIGHEST_EVER;
    }

    /**
     * Returns the student assessment association with the most recent timestamp
     */
    public static Map getMostRecentAssessment(List<Map<String, Object>> a) {

        Collections.sort(a, new Comparator<Map>() {

            @Override
            public int compare(Map o1, Map o2) {

                try {
                    Date d1 = dateFormat.parse((String) o1.get(Constants.ATTR_ADMIN_DATE));
                    Date d2 = dateFormat.parse((String) o2.get(Constants.ATTR_ADMIN_DATE));
                    return d2.compareTo(d1);

                } catch (ParseException e) {
                    LOGGER.error("Date compare error");
                    return 0;
                }
            }
        });

        return a.get(0);
    }

    /**
     * Returns the student assessment association with the highest score
     */
    public static Map getHighestEverAssessment(List<Map<String, Object>> a) {

        Collections.sort(a, new Comparator<Map>() {

            @Override
            public int compare(Map o1, Map o2) {
                List<Map<String, String>> scoreResults1 = (List) o1.get(Constants.ATTR_SCORE_RESULTS);
                List<Map<String, String>> scoreResults2 = (List) o2.get(Constants.ATTR_SCORE_RESULTS);
                String score1 = null;
                String score2 = null;
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
                if (score1 == null && score2 == null) {
                    return 0;
                }

                if (score1 == null) {
                    return 1;
                }

                if (score2 == null) {
                    return -1;
                }

                return Integer.parseInt(score2) - Integer.parseInt(score1);
            }
        });
        return a.get(0);
    }

    /**
     * Returns the student assessment association with the highest objective assessment score, based
     * on objective assessment identification code (i.e. SAT-Reading)
     */

    public static Map getHighestEverObjAssmt(List<Map<String, Object>> saaList, final String objAssmtCode) {
        if (objAssmtCode == null || objAssmtCode.equals("")) {
            return null;
        }
        Collections.sort(saaList, new Comparator<Map>() {

            @Override
            public int compare(Map o1, Map o2) {
                if (o1 == null || o2 == null) {
                    return 0;
                }
                try {
                    List<Map<String, Object>> studentObjAssmts1 = (List<Map<String, Object>>) o1
                            .get(Constants.ATTR_STUDENT_OBJECTIVE_ASSESSMENTS);
                    List<Map<String, Object>> studentObjAssmts2 = (List<Map<String, Object>>) o2
                            .get(Constants.ATTR_STUDENT_OBJECTIVE_ASSESSMENTS);
                    List<Map<String, String>> scoreResults1 = getScoreResults(studentObjAssmts1, objAssmtCode);
                    List<Map<String, String>> scoreResults2 = getScoreResults(studentObjAssmts2, objAssmtCode);
                    String score1 = getScoreFromScoreResults(scoreResults1);
                    String score2 = getScoreFromScoreResults(scoreResults2);
                    if (score2.equals("") && score1.equals("")) {
                        return 0;
                    } else if (score2.equals("")) {
                        return -1;
                    } else if (score1.equals("")) {
                        return 1;
                    } else {
                        return Integer.parseInt(score2) - Integer.parseInt(score1);
                    }
                } catch (ClassCastException e) {
                    return 0;
                }
            }
        });
        return saaList.get(0);
    }

    /**
     * Returns the assessment from the most recent window of the given assessment family
     * Most recent window is defined as:
     * - assessment with lastest beginDate that is not in the future
     * - if there is no assessment window, most recent studentAssessment admin date is used
     */
    public static Map getMostRecentAssessmentWindow(Collection<Map<String, Object>> results,
            Collection<Map<String, Object>> assessmentMetaData) {

        AssessmentPeriod window = getMostRecentWindow(assessmentMetaData);

        if (window != null) {
            Map best = null;
            for (Map studentAssessment : results) {
                String date = (String) studentAssessment.get(Constants.ATTR_ADMIN_DATE);
                if (window.beginDate.compareTo(date) <= 0 && window.endDate.compareTo(date) >= 0) {
                    if (best == null || date.compareTo((String) best.get(Constants.ATTR_ADMIN_DATE)) > 0) {
                        best = studentAssessment;
                    }
                }
            }
            return best;
        } else {
            Map mostRecent = null;
            for (Map studentAssessment : results) {
                String date = (String) studentAssessment.get(Constants.ATTR_ADMIN_DATE);
                if (date != null
                        && (mostRecent == null || date.compareTo((String) mostRecent.get(Constants.ATTR_ADMIN_DATE)) >= 0)) {
                    mostRecent = studentAssessment;
                }
            }
            return mostRecent;
        }
    }

    private static AssessmentPeriod getMostRecentWindow(Collection<Map<String, Object>> assessmentMetaData) {
        String now = javax.xml.bind.DatatypeConverter.printDate(Calendar.getInstance());
        List<AssessmentPeriod> periods = new ArrayList<AssessmentPeriod>();
        for (Map assessment : assessmentMetaData) {
            @SuppressWarnings("unchecked")
            Map<String, String> periodDescriptor = (Map<String, String>) assessment
                    .get(Constants.ATTR_ASSESSMENT_PERIOD_DESCRIPTOR);
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
        final Map assessment;
        final String beginDate;
        final String endDate;

        public AssessmentPeriod(Map assessment, String beginDate, String endDate) {
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

    protected static List<Map<String, String>> getScoreResults(List<Map<String, Object>> studentObjAssmts,
            String objAssmtCode) {
        List<Map<String, String>> scoreResults = new ArrayList<Map<String, String>>();
        if (studentObjAssmts != null) {
            for (Map studentObjAssmt : studentObjAssmts) {
                String idCode = (String) ((Map) (studentObjAssmt.get(Constants.ATTR_OBJECTIVE_ASSESSMENT)))
                        .get(Constants.ATTR_IDENTIFICATIONCODE);
                String[] codes = objAssmtCode.replace("-", " ").split(" ");
                boolean match = true;
                for (String code : codes) {
                    if (!idCode.contains(code)) {
                        match = false;
                    }
                }
                if (match) {
                    scoreResults = (List<Map<String, String>>) (studentObjAssmt.get(Constants.ATTR_SCORE_RESULTS));
                }
            }
        }
        return scoreResults;
    }

    protected static String getScoreFromScoreResults(List<Map<String, String>> scoreResults) {
        String score = "";
        for (Map<String, String> scoreResult : scoreResults) {
            if (scoreResult.get(Constants.ATTR_ASSESSMENT_REPORTING_METHOD).equals(Constants.ATTR_SCALE_SCORE)
                    || scoreResult.get(Constants.ATTR_ASSESSMENT_REPORTING_METHOD).equals(Constants.ATTR_RAW_SCORE)) {
                score = scoreResult.get(Constants.ATTR_RESULT);
            }
        }
        return score;
    }
}

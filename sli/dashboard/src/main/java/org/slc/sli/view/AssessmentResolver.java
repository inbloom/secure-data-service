package org.slc.sli.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.slc.sli.config.Field;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.assessmentmetadata.AssessmentMetaData;
import org.slc.sli.entity.assessmentmetadata.PerfLevel;
import org.slc.sli.util.Constants;


//Hopefully there will be one for each of dataSet types

/**
 * A utility class for views in SLI dashboard. As a wrapper around assessment data passed onto
 *  dashboard views. Contains useful tools look up assessment data
 *
 * @author syau
 *
 */
public class AssessmentResolver {
    Map<String, List<GenericEntity> > studentIdToAssessments;
    AssessmentMetaDataResolver metaDataResolver;

    public static final String DATA_SET_TYPE = "assessment";

    public static final String DATA_POINT_NAME_PERFLEVEL = "perfLevel";
    public static final String DATA_POINT_NAME_SCALESCORE = "scaleScore";
    public static final String DATA_POINT_NAME_PERCENTILE = "percentile";
    public static final String DATA_POINT_NAME_LEXILESCORE = "lexileScore";

    public static final String TIMESLOT_MOSTRECENTWINDOW = "MOST_RECENT_WINDOW";
    public static final String TIMESLOT_MOSTRECENTRESULT = "MOST_RECENT_RESULT";
    public static final String TIMESLOT_HIGHESTEVER = "HIGHEST_EVER";

    /**
     * Constructor
     */
    public AssessmentResolver(List<GenericEntity> a, List<AssessmentMetaData> md) {
        studentIdToAssessments = new HashMap<String, List<GenericEntity>>();
        for (GenericEntity ass : a) {
            studentIdToAssessments.put(ass.get(Constants.ATTR_STUDENT_ID).toString(), new ArrayList<GenericEntity>());
        }
        for (GenericEntity ass : a) {
            studentIdToAssessments.get(ass.get(Constants.ATTR_STUDENT_ID).toString()).add(ass);
        }
        metaDataResolver = new AssessmentMetaDataResolver(md);

    }

    /**
     * Looks up a representation for the result of the assessment, taken by the student
     * Returns the string representation of the result, identified by the Field
     */
    public String get(Field field, Map student) {
        // look up the assessment. 
        GenericEntity chosenAssessment = resolveAssessment(field, student);

        // get the data point
        String dataPointName = extractDataPointName(field.getValue());
        if (chosenAssessment == null) { return ""; }
        if (dataPointName == null) { return ""; }
        if (dataPointName.equals(DATA_POINT_NAME_SCALESCORE)) { return (String) (chosenAssessment.get(DATA_POINT_NAME_SCALESCORE)); }
        if (dataPointName.equals(DATA_POINT_NAME_PERCENTILE)) { return (String) (chosenAssessment.get(DATA_POINT_NAME_PERCENTILE)); }
        if (dataPointName.equals(DATA_POINT_NAME_LEXILESCORE)) { return (String) (chosenAssessment.get(DATA_POINT_NAME_LEXILESCORE)); }

        // return shortname for perf levels?? 
        if (dataPointName.equals(DATA_POINT_NAME_PERFLEVEL)) { 
            String perfLevel = (String) (chosenAssessment.get(DATA_POINT_NAME_PERFLEVEL)); 
            List<PerfLevel> perfLevels = metaDataResolver.findPerfLevelsForFamily((String) (chosenAssessment.get(Constants.ATTR_ASSESSMENT_NAME)));

            if (perfLevels == null) { return ""; }
            for (PerfLevel pl : perfLevels) {
                if (perfLevel.equals(pl.getName())) {
                    return pl.getShortName();
                }
            }
        }

        return "";
    }

    /**
     * Looks up the cutpoints for the result returned by get(field, student);
     * (used by fuel gauge visualization widget)
     */
    public List<Integer> getCutpoints(Field field, Map student) {
        // look up the assessment. 
        GenericEntity chosenAssessment = resolveAssessment(field, student);

        if (chosenAssessment == null) { return null; }
        // get the cutpoints
        return metaDataResolver.findCutpointsForFamily((String) (chosenAssessment.get(Constants.ATTR_ASSESSMENT_NAME)));
    }

    public AssessmentMetaDataResolver getMetaData() {
        return metaDataResolver;
    }

    // ---------------------- Helper functions -------------------------
    /*
     * Looks up a representation for the result of the assessment, taken by the student
     */
    public GenericEntity resolveAssessment(Field field, Map student) {

        // This first implementation is gruelingly inefficient. But, whateves... it's gonna be
        // thrown away.

        // A) filter out students first
        List<GenericEntity> studentFiltered = studentIdToAssessments.get(student.get(Constants.ATTR_ID));
        if (studentFiltered == null || studentFiltered.isEmpty()) { return null; }

        // B) filter out assessments based on dataset path
        String assessmentName = extractAssessmentName(field.getValue());
        List<GenericEntity> studentAssessmentFiltered = new ArrayList<GenericEntity>();
        for (GenericEntity a : studentFiltered) {
            if (metaDataResolver.isAncestor(assessmentName, (String) (a.get(Constants.ATTR_ASSESSMENT_NAME)))) {
                studentAssessmentFiltered.add(a);
            }
        }
        if (studentAssessmentFiltered.isEmpty()) { return null; }

        // C) Apply time logic. 
        GenericEntity chosenAssessment = null;

        String timeSlot = field.getTimeSlot();
        if (TIMESLOT_MOSTRECENTWINDOW.equals(timeSlot)) {
            chosenAssessment = TimedLogic.getMostRecentAssessmentWindow(studentAssessmentFiltered,
                                                                        metaDataResolver,
                                                                        assessmentName);
        } else if (TIMESLOT_MOSTRECENTRESULT.equals(timeSlot)) {
            chosenAssessment = TimedLogic.getMostRecentAssessment(studentAssessmentFiltered);
        } else if (TIMESLOT_HIGHESTEVER.equals(timeSlot)) {
            chosenAssessment = TimedLogic.getHighestEverAssessment(studentAssessmentFiltered);
        } else {
            // Decide whether to throw runtime exception here. Should timed logic default @@@
            chosenAssessment = TimedLogic.getMostRecentAssessment(studentAssessmentFiltered);
        }

        return chosenAssessment;
    }

    // helper functions to extract names from the view config using the datapointid
    private String extractAssessmentName(String dataPointId) {
        String [] strs = dataPointId.split("\\.");
        return strs[0];
    }

    private String extractDataPointName(String dataPointId) {
        String [] strs = dataPointId.split("\\.");
        return strs[1];
    }

}

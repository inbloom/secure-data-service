package org.slc.sli.view;

import org.slc.sli.entity.Assessment;
import org.slc.sli.entity.assessmentmetadata.AssessmentMetaData;
import org.slc.sli.entity.Student;

import org.slc.sli.config.Field;

import java.util.List;
import java.util.ArrayList;


//Hopefully there will be one for each of dataSet types

/**
 * A utility class for views in SLI dashboard. As a wrapper around assessment data passed onto
 *  dashboard views. Contains useful tools look up assessment data
 * 
 * @author syau
 *
 */
public class AssessmentResolver {
    List<Assessment> assessments;
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
    public AssessmentResolver(List<Assessment> a, List<AssessmentMetaData> md) {
        assessments = a;
        metaDataResolver = new AssessmentMetaDataResolver(md);
        
    }
    
    /**
     * Looks up a representation for the result of the assessment, taken by the student 
     * Returns the string representation of the result, identified by the Field
     */
    public String get(Field field, Student student) {
        // look up the assessment. 
        Assessment chosenAssessment = resolveAssessment(field, student);
        // get the data point
        String dataPointName = extractDataPointName(field.getValue());
        if (chosenAssessment == null) { return ""; }
        if (dataPointName == null) { return ""; }
        if (dataPointName.equals(DATA_POINT_NAME_PERFLEVEL)) { return chosenAssessment.getPerfLevelAsString(); }
        if (dataPointName.equals(DATA_POINT_NAME_SCALESCORE)) { return chosenAssessment.getScaleScoreAsString(); }
        if (dataPointName.equals(DATA_POINT_NAME_PERCENTILE)) { return chosenAssessment.getPercentileAsString(); }
        if (dataPointName.equals(DATA_POINT_NAME_LEXILESCORE)) { return chosenAssessment.getLexileScore(); }

        return ""; 
    }

    /**
     * Looks up the cutpoints for the result returned by get(field, student);
     */
    public List<Integer> getCutpoints(Field field, Student student) {
        // look up the assessment. 
        Assessment chosenAssessment = resolveAssessment(field, student);
        if (chosenAssessment == null) { return null; }
        // get the cutpoints
        return metaDataResolver.findCutpointsForFamily(chosenAssessment.getAssessmentName());
    }
    
    public AssessmentMetaDataResolver getMetaData() { 
        return metaDataResolver;
    }
    
    // ---------------------- Helper functions -------------------------
    /*
     * Looks up a representation for the result of the assessment, taken by the student 
     */
    public Assessment resolveAssessment(Field field, Student student) {

        // This first implementation is gruelingly inefficient. But, whateves... it's gonna be 
        // thrown away. 

        // A) filter out students first
        List<Assessment> studentFiltered = new ArrayList<Assessment>();
        for (Assessment a : assessments) {
            if (a.getStudentId().equals(student.getUid())) {
                studentFiltered.add(a);
            }
        }
        if (studentFiltered.isEmpty()) { return null; }

        // B) filter out assessments based on dataset path
        String assessmentName = extractAssessmentName(field.getValue());
        List<Assessment> studentAssessmentFiltered = new ArrayList<Assessment>();
        for (Assessment a : studentFiltered) {
            if (metaDataResolver.isAncestor(assessmentName, a.getAssessmentName())) {
                studentAssessmentFiltered.add(a);
            }
        }
        if (studentAssessmentFiltered.isEmpty()) { return null; }

        // C) Apply time logic. 
        Assessment chosenAssessment = null;
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

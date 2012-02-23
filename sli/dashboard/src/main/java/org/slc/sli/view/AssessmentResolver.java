package org.slc.sli.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slc.sli.config.Field;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;


/**
 * A utility class for views in SLI dashboard. As a wrapper around assessment data passed onto
 *  dashboard views. Contains useful tools look up assessment data
 *
 * @author syau
 *
 */
public class AssessmentResolver {

    AssessmentMetaDataResolver metaDataResolver;

    public static final String DATA_SET_TYPE = "assessment";

    public static final String TIMESLOT_MOSTRECENTWINDOW = "MOST_RECENT_WINDOW";
    public static final String TIMESLOT_MOSTRECENTRESULT = "MOST_RECENT_RESULT";
    public static final String TIMESLOT_HIGHESTEVER = "HIGHEST_EVER";

    /**
     * Constructor
     */
    public AssessmentResolver(List<GenericEntity> studentSummaries, List<GenericEntity> assmts) {
        
        metaDataResolver = new AssessmentMetaDataResolver(assmts);
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

        return getScore(chosenAssessment, dataPointName);
        
        // return shortname for perf levels??
        /*
        if (dataPointName.equals(DATA_POINT_NAME_PERFLEVEL)) { 
            String perfLevel = chosenAssessment.getString(DATA_POINT_NAME_PERFLEVEL); 
            List<PerfLevel> perfLevels = metaDataResolver.findPerfLevelsForFamily(chosenAssessment.getString(Constants.ATTR_ASSESSMENT_NAME));

            if (perfLevels == null) { return ""; }
            for (PerfLevel pl : perfLevels) {
                if (perfLevel.equals(pl.getName())) {
                    return pl.getShortName();
                }
            }
        }
        */
        
        //return "";
    }

    public String getScore(GenericEntity assmt, String dataPointName) {
        
        // find the right score
        List<Map> scoreResults = assmt.getList(Constants.ATTR_SCORE_RESULTS);
        for (Map scoreResult : scoreResults) {
            if (scoreResult.get(Constants.ATTR_ASSESSMENT_REPORTING_METHOD).equals(dataPointName)) {
                return (String) (scoreResult.get(Constants.ATTR_RESULT));
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
        String dataPointId = field.getValue();
        String assmtName = dataPointId.substring(0, dataPointId.indexOf('.'));
        return metaDataResolver.findCutpointsForFamily(assmtName);
    }
    
    public AssessmentMetaDataResolver getMetaData() {
        return metaDataResolver;
    }

    // ---------------------- Helper functions -------------------------
    /*
     * Looks up a representation for the result of the assessment, taken by the student
     */
    public GenericEntity resolveAssessment(Field field, Map student) {


        // filter out assessments based on dataset path
        String assessmentName = extractAssessmentName(field.getValue());
        /*
        List<GenericEntity> studentAssessmentFiltered = new ArrayList<GenericEntity>();
        for (GenericEntity a : studentFiltered) {
            if (metaDataResolver.isAncestor(assessmentName, a.getString(Constants.ATTR_ASSESSMENT_NAME))) {
                studentAssessmentFiltered.add(a);
            }
        }
        if (studentAssessmentFiltered.isEmpty()) { return null; }
        */

        List<Map> studentAssmts = (List<Map>) (student.get(Constants.ATTR_STUDENT_ASSESSMENTS));
        
        List<GenericEntity> studentAssessmentFiltered = new ArrayList<GenericEntity>();
        for (Map studentAssmt : studentAssmts) {
            
            // TODO: i don't think we can depend on the assessment id to contain the name
            if (metaDataResolver.isInAssessmentFamily((String) (studentAssmt.get(Constants.ATTR_ASSESSMENT_ID)), assessmentName)) {
                // TODO: all this converting from Maps to GenericEntity needs to be revisited
                studentAssessmentFiltered.add(new GenericEntity(studentAssmt));
            }
        }
        
        if (studentAssessmentFiltered == null || studentAssessmentFiltered.size() == 0) {
            return null;
        }
        
        // Apply time logic. 
        GenericEntity chosenAssessment = null;

        String timeSlot = field.getTimeSlot();
        
        // TODO: implement most recent window when the assessment period info is available
        /*if (TIMESLOT_MOSTRECENTWINDOW.equals(timeSlot)) {
            chosenAssessment = TimedLogic.getMostRecentAssessmentWindow(studentAssessmentFiltered,
                                                                        metaDataResolver,
                                                                        assessmentName);
        } else */ 
        
        if (TIMESLOT_MOSTRECENTRESULT.equals(timeSlot)) {
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

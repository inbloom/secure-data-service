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
    Map<String, List<GenericEntity>> studentIdToAssessments;
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
    	/*
        studentIdToAssessments = new HashMap<String, List<GenericEntity>>();
        for (GenericEntity ass : a) {
            studentIdToAssessments.put(ass.getString(Constants.ATTR_STUDENT_ID), new ArrayList<GenericEntity>());
        }
        for (GenericEntity ass : a) {
            studentIdToAssessments.get(ass.getString(Constants.ATTR_STUDENT_ID)).add(ass);
        }
        metaDataResolver = new AssessmentMetaDataResolver(md);
        */

    }
    
    public AssessmentResolver(List<GenericEntity> studentSummaries, List<GenericEntity> assmts, String s) {
        
    	//metaDataResolver = new AssessmentMetaDataResolver(assmts);
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
        
        //if (dataPointName.equals(DATA_POINT_NAME_SCALESCORE)) { return chosenAssessment.getString(DATA_POINT_NAME_SCALESCORE); }
        //if (dataPointName.equals(DATA_POINT_NAME_PERCENTILE)) { return chosenAssessment.getString(DATA_POINT_NAME_PERCENTILE); }
        //if (dataPointName.equals(DATA_POINT_NAME_LEXILESCORE)) { return chosenAssessment.getString(DATA_POINT_NAME_LEXILESCORE); }

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

    private String getScore(GenericEntity assmt, String dataPointName) {
    	
    	// find the right score
    	List<Map> scoreResults = assmt.getList("scoreResults");
    	for (Map scoreResult : scoreResults) {
    		if (scoreResult.get("assessmentReportingMethod").equals(dataPointName)) {
    			return (String) (scoreResult.get("result"));
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
        return metaDataResolver.findCutpointsForFamily(chosenAssessment.getString(Constants.ATTR_ASSESSMENT_NAME));
    }

    public AssessmentMetaDataResolver getMetaData() {
        return metaDataResolver;
    }

    // ---------------------- Helper functions -------------------------
    /*
     * Looks up a representation for the result of the assessment, taken by the student
     */
    public GenericEntity resolveAssessment(Field field, Map student) {

        // A) filter out students first
        //List<GenericEntity> studentFiltered = studentIdToAssessments.get(student.get(Constants.ATTR_ID));
        //if (studentFiltered == null || studentFiltered.isEmpty()) { return null; }

        // B) filter out assessments based on dataset path
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

        List<GenericEntity> studentAssmts = (List<GenericEntity>) (student.get(Constants.ATTR_STUDENT_ASSESSMENTS));
        
        List<GenericEntity> studentAssessmentFiltered = studentAssmts;
        
        if (studentAssessmentFiltered == null || studentAssessmentFiltered.size() == 0) {
        	return null;
        }
        
        // C) Apply time logic. 
        GenericEntity chosenAssessment = null;

        String timeSlot = field.getTimeSlot();
        /*if (TIMESLOT_MOSTRECENTWINDOW.equals(timeSlot)) {
            chosenAssessment = TimedLogic.getMostRecentAssessmentWindow(studentAssessmentFiltered,
                                                                        metaDataResolver,
                                                                        assessmentName);
        } else */ if (TIMESLOT_MOSTRECENTRESULT.equals(timeSlot)) {
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

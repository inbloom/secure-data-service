package org.slc.sli.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.config.Field;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;

/**
 * A utility class for views in SLI dashboard. As a wrapper around assessment data passed onto
 * dashboard views. Contains useful tools look up assessment data
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
        String objAssmtCode = extractObjAssmtCode(field.getValue());
        if (chosenAssessment == null) {
            return "";
        }
        if (dataPointName == null) {
            return "";
        }
        
        return getScore(chosenAssessment, dataPointName, objAssmtCode);
        
        // return shortname for perf levels??
        /*
         * if (dataPointName.equals(DATA_POINT_NAME_PERFLEVEL)) {
         * String perfLevel = chosenAssessment.getString(DATA_POINT_NAME_PERFLEVEL);
         * List<PerfLevel> perfLevels =
         * metaDataResolver.findPerfLevelsForFamily(chosenAssessment.getString
         * (Constants.ATTR_ASSESSMENT_NAME));
         * 
         * if (perfLevels == null) { return ""; }
         * for (PerfLevel pl : perfLevels) {
         * if (perfLevel.equals(pl.getName())) {
         * return pl.getShortName();
         * }
         * }
         * }
         */
        
        // return "";
    }
    
    /**
     * @param studentAssmt
     *            the student assessment association entity
     * @param dataPointName
     *            the field or attribute in student assessment association
     * @param objAssmtCode
     *            the objective assessment identification code if student assessment association
     *            include student objective assessment
     * @return the string value of field or attribute specified by dataPointName in student
     *         assessment association
     */
    public String getScore(GenericEntity studentAssmt, String dataPointName, String objAssmtCode) {
        
        List<Map> scoreResults = new ArrayList<Map>();
        
        /*
         * check is student assessment association has student objective assessment, if objective
         * assessment identification code is provided, then the field will be retrieved from student
         * objective assessment
         */
        if (objAssmtCode == null || objAssmtCode.equals("")) {
            scoreResults = studentAssmt.getList(Constants.ATTR_SCORE_RESULTS);
        } else {
            List<Map> studentObjAssmts = studentAssmt.getList(Constants.ATTR_STUDENT_OBJECTIVE_ASSESSMENTS);
            if (studentObjAssmts != null) {
                for (Map studentObjAssmt : studentObjAssmts) {
                    
                    // use objective assessment identification code specified on view configuration
                    // to
                    // find right objective assessment
                    String idCode = (String) (((Map) (studentObjAssmt.get(Constants.ATTR_OBJECTIVE_ASSESSMENT)))
                            .get(Constants.ATTR_IDENTIFICATIONCODE));
                    String[] codes = objAssmtCode.replace("-", " ").split(" ");
                    boolean match = true;
                    for (String code : codes) {
                        if (!idCode.toLowerCase().contains(code.toLowerCase())) {
                            match = false;
                        }
                    }
                    if (match) {
                        scoreResults = (List<Map>) (studentObjAssmt.get(Constants.ATTR_SCORE_RESULTS));
                    }
                }
            }
        }

        // find the right field value by match dataPointName
        List<Map> perfLevelDescriptors = studentAssmt.getList(Constants.ATTR_PERFORMANCE_LEVEL_DESCRIPTOR);
        
        for (Map scoreResult : scoreResults) {
            if (scoreResult.get(Constants.ATTR_ASSESSMENT_REPORTING_METHOD).equals(dataPointName)) {
                return (String) (scoreResult.get(Constants.ATTR_RESULT));
                
            }
        }
        // if performance level is not included in score results as mastery level, then find the
        // performance level from performance level descriptor
        if (dataPointName.equalsIgnoreCase(Constants.ATTR_PERF_LEVEL)
                && !findPerfLevelFromDescriptor(perfLevelDescriptors).equals("")) {
            return findPerfLevelFromDescriptor(perfLevelDescriptors);

            // if performance level not specified in performance level descriptor, then return blank
        } else if (dataPointName.equalsIgnoreCase(Constants.ATTR_PERF_LEVEL) && !hasPerfLevelScoreResults(scoreResults)) {
            /*
             * calculate the performance level if not provided in performance level descriptor
             * 
             * String assmtId = studentAssmt.getString(Constants.ATTR_ASSESSMENT_ID);
             * String scaleScore = getScore(studentAssmt, Constants.ATTR_SCALE_SCORE, null);
             * if (assmtId != null && !assmtId.equals("") && scaleScore != null &&
             * !scaleScore.equals("")) {
             * return metaDataResolver.calculatePerfLevel(assmtId, scaleScore);}
             */
            return "";
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
        
        if (chosenAssessment == null) {
            return null;
        }
        
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
         * List<GenericEntity> studentAssessmentFiltered = new ArrayList<GenericEntity>();
         * for (GenericEntity a : studentFiltered) {
         * if (metaDataResolver.isAncestor(assessmentName,
         * a.getString(Constants.ATTR_ASSESSMENT_NAME))) {
         * studentAssessmentFiltered.add(a);
         * }
         * }
         * if (studentAssessmentFiltered.isEmpty()) { return null; }
         */
        
        List<Map> studentAssmts = (List<Map>) (student.get(Constants.ATTR_STUDENT_ASSESSMENTS));
        
        List<GenericEntity> studentAssessmentFiltered = new ArrayList<GenericEntity>();
        for (Map studentAssmt : studentAssmts) {
            
            // TODO: i don't think we can depend on the assessment id to contain the name
            if (metaDataResolver.isInAssessmentFamily((String) (studentAssmt.get(Constants.ATTR_ASSESSMENT_ID)),
                    assessmentName)) {
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
        String objAssmtCode = extractObjAssmtCode(field.getValue());
        
        // TODO: implement most recent window when the assessment period info is available
        /*
         * if (TIMESLOT_MOSTRECENTWINDOW.equals(timeSlot)) {
         * chosenAssessment = TimedLogic.getMostRecentAssessmentWindow(studentAssessmentFiltered,
         * metaDataResolver,
         * assessmentName);
         * } else
         */
        
        if (TIMESLOT_MOSTRECENTRESULT.equals(timeSlot)) {
            chosenAssessment = TimedLogic.getMostRecentAssessment(studentAssessmentFiltered);
        } else if (TIMESLOT_HIGHESTEVER.equals(timeSlot) && !objAssmtCode.equals("")) {
            chosenAssessment = TimedLogic.getHighestEverObjAssmt(studentAssessmentFiltered, objAssmtCode);
        } else if (TIMESLOT_HIGHESTEVER.equals(timeSlot)) {
            chosenAssessment = TimedLogic.getHighestEverAssessment(studentAssessmentFiltered);
        } else if (TIMESLOT_MOSTRECENTWINDOW.equals(timeSlot)) {
            List<GenericEntity> assessmentMetaData = new ArrayList<GenericEntity>();
            Set<String> assessmentIds = new HashSet<String>();
            for (GenericEntity studentAssessment : studentAssessmentFiltered) {
                String assessmentId = studentAssessment.getString(Constants.ATTR_ASSESSMENT_ID);
                if (!assessmentIds.contains(assessmentId)) {
                    GenericEntity assessment = metaDataResolver.getAssmtById(assessmentId);
                    assessmentMetaData.add(assessment);
                    assessmentIds.add(assessmentId);
                }
            }
            
            chosenAssessment = TimedLogic.getMostRecentAssessmentWindow(studentAssessmentFiltered, assessmentMetaData);
        } else {
            // Decide whether to throw runtime exception here. Should timed logic default @@@
            chosenAssessment = TimedLogic.getMostRecentAssessment(studentAssessmentFiltered);
        }
        
        return chosenAssessment;
    }
    
    // helper functions to extract names from the view config using the datapointid
    private String extractAssessmentName(String dataPointId) {
        String[] strs = dataPointId.split("\\.");
        return strs[0];
    }
    
    private String extractDataPointName(String dataPointId) {
        String[] strs = dataPointId.split("\\.");
        return strs[1];
    }
    
    private String extractObjAssmtCode(String dataPointId) {
        String[] strs = dataPointId.split("\\.");
        if (strs.length >= 3)
            return strs[2];
        return "";
    }

    private boolean hasPerfLevelScoreResults(List<Map> scoreResults) {
        boolean found = false;
        for (Map scoreResult : scoreResults) {
            if (((String) scoreResult.get(Constants.ATTR_ASSESSMENT_REPORTING_METHOD))
                    .equalsIgnoreCase(Constants.ATTR_PERF_LEVEL)) {
                found = true;
            }
        }
        return found;
    }
    
    private String findPerfLevelFromDescriptor(List<Map> descriptors) {
        if (descriptors == null)
            return "";
        for (Map descriptor : descriptors) {
            String perfLevel = (String) (descriptor.get(Constants.ATTR_DESCRIPTION));
            try {
                Integer.parseInt(perfLevel);
            } catch (Exception e) {
                return "";
            }
            return perfLevel;
        }
        return "";
    }
}

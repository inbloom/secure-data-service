package org.slc.sli.view;

import org.slc.sli.entity.Assessment;
import org.slc.sli.entity.Student;

import org.slc.sli.config.ViewConfig;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import org.slc.sli.config.DataPoint;
import org.slc.sli.config.DataSet;

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
    ViewConfig viewConfig;
    
    public static final String DATA_SET_TYPE = "assessment";
    public static final String DATA_POINT_NAME_PERFLEVEL = "perfLevel";
    public static final String DATA_POINT_NAME_SCALESCORE = "scaleScore";
    public static final String DATA_POINT_NAME_PERCENTILE = "percentile";
    public static final String DATA_POINT_NAME_LEXILESCORE = "lexileScore";

    /**
     * Constructor
     */
    public AssessmentResolver(List<Assessment> a, ViewConfig v) {
        assessments = a;
        viewConfig = v;
    }
    
    /**
     * Looks up a representation for the result of the assessment, taken by the student 
     * Returns the string representation of the result, identified by the datapoint ID
     */
    public String get(String dataPointId, Student student) {

        // This first implementation is gruelingly inefficient. But, whateves... it's gonna be 
        // thrown away. 

        // A) filter out students first
        List<Assessment> studentFiltered = new ArrayList();
        for (Assessment a : assessments) {
            if (a.getStudentId().equals(student.getUid())) {
                studentFiltered.add(a);
            }
        }
        if (studentFiltered.isEmpty()) { return ""; }

        // B) filter out assessments based on dataset path
        String assessmentName = extractAssessmentName(dataPointId);
        List<Assessment> studentAssessmentFiltered = new ArrayList();
        for (Assessment a : studentFiltered) {
            if (a.getAssessmentName().equals(assessmentName)) {
                studentAssessmentFiltered.add(a);
            }
        }
        if (studentAssessmentFiltered.isEmpty()) { return ""; }

        // C) Apply time logic. For now, just get the latest... there should be some 
        //    classes that actually implement various timed logics. 
        String timeSlot = extractTimeSlot(dataPointId);
        Collections.sort(studentAssessmentFiltered, new Comparator<Assessment>() {
            public int compare(Assessment o1, Assessment o2) {
                return o1.getYear() - o2.getYear();
            }
        });

        // D) get the data point
        Assessment chosenAssessment = studentAssessmentFiltered.get(0);
        String dataPointName = extractDataPointName(dataPointId);
        if (dataPointName == null) { return ""; }
        if (dataPointName.equals(DATA_POINT_NAME_PERFLEVEL)) { return chosenAssessment.getPerfLevelAsString(); }
        if (dataPointName.equals(DATA_POINT_NAME_SCALESCORE)) { return chosenAssessment.getScaleScoreAsString(); }
        if (dataPointName.equals(DATA_POINT_NAME_PERCENTILE)) { return chosenAssessment.getPercentileAsString(); }
        if (dataPointName.equals(DATA_POINT_NAME_LEXILESCORE)) { return chosenAssessment.getLexileScoreAsString(); }

        return ""; 
    }

    // returns true iff this resolver's view config can resolve the data point 
    public boolean canResolve(String dataPointId) {
        return getDataPoint(dataPointId) != null;
    }

    // helper functions to extract names from the view config using the datapointid 
    private String extractAssessmentName(String dataPointId) {
        DataSet ds = getDataSet(dataPointId);
        return ds == null ? null : ds.getName();
    }
    private String extractTimeSlot(String dataPointId) {
        DataSet ds = getDataSet(dataPointId);
        return ds == null ? null : ds.getTimeSlot();
    }
    private String extractDataPointName(String dataPointId) {
        DataPoint dp = getDataPoint(dataPointId);
        return dp == null ? null : dp.getId(); // Assume data point's name is identical to id
    }
    private DataSet getDataSet(String dataPointId) {
        String [] dataPointPath = dataPointId.split("\\.");
        String dataSetName = dataPointPath[0];
        for (DataSet ds : viewConfig.getDataSet()) {
            if (ds.getType().equals(DATA_SET_TYPE) && ds.getId().equals(dataSetName)) {
                return ds;
            }
        }
        return null;
    }
    private DataPoint getDataPoint(String dataPointId) {
        String [] dataPointPath = dataPointId.split("\\.");
        String dataPointName = dataPointPath[1];
        DataSet ds = getDataSet(dataPointId);
        if (ds == null) { return null; }
        for (DataPoint dp : ds.getDataPoint()) {
            if (dp.getId().equals(dataPointName)) {
                return dp;
            }
        }
        return null;
    }
}

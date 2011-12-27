package org.slc.sli.view;

import org.slc.sli.entity.Student;

import org.slc.sli.config.ViewConfig;

import java.util.List;
import org.slc.sli.config.DataPoint;
import org.slc.sli.config.DataSet;

//Hopefully there will be one for each of dataSet types

/**
 * A utility class for views in SLI dashboard. As a wrapper around student data passed onto
 *  dashboard views. Contains useful tools look up student data
 * 
 * @author syau
 *
 */
public class StudentResolver {
    List<Student> students;
    ViewConfig viewConfig;
    
    public static final String DATA_SET_TYPE = "studentInfo";
    
    /**
     * Constructor
     */
    public StudentResolver(List<Student> s, ViewConfig v) {
        students = s;
        viewConfig = v;
    }
    
    public List<Student> list() {
        return students;
    }
    
    /**
     * Returns the string representation of the student information, identified by the datapoint ID
     */
    public String get(String dataPointId, Student student) {
        String dataPointName = extractDataPointName(dataPointId);
        if (dataPointName == null) { return ""; }
        if (dataPointName.equals("name")) {
            // formatting class and logic should be added here later. Or maybe in the view. Don't know... 
            return student.getFirstName() + " " + student.getLastName(); 
        } 
        return "";
    }

    // returns true iff this resolver's view config can resolve the data point 
    public boolean canResolve(String dataPointId) {
        return getDataPoint(dataPointId) != null;
    }

    // Helper functions. 
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

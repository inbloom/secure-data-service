package org.slc.sli.view;

import org.slc.sli.entity.Student;
import org.slc.sli.config.Field;

import java.util.List;


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
    
    public static final String DATA_SET_TYPE = "studentInfo";
    
    /**
     * Constructor
     */
    public StudentResolver(List<Student> s) {
        students = s;
    }
    
    public List<Student> list() {
        return students;
    }
    
    /**
     * Returns the string representation of the student information, identified by the datapoint ID
     */
    public String get(Field field, Student student) {
        String dataPointName = field.getValue();
        if (dataPointName == null) { return ""; }
        if (dataPointName.equals("name")) {
            // formatting class and logic should be added here later. Or maybe in the view. Don't know... 
            return student.getFirstName() + " " + student.getLastName(); 
        } 
        return "";
    }

}

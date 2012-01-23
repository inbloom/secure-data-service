package org.slc.sli.view;

import org.slc.sli.entity.Student;
import org.slc.sli.entity.StudentProgramAssociation;
import org.slc.sli.config.Field;

import java.util.Arrays;
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
    List<StudentProgramAssociation> programs;
    
    public static final String DATA_SET_TYPE = "studentInfo";
    
    /**
     * Constructor
     */
    public StudentResolver(List<Student> s, List<StudentProgramAssociation> p) {
        students = s;
        programs = p;
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

    /**
     * returns true if the given lozenge code applies to the given student
     */
    public boolean lozengeApplies(Student student, String code) {
        
        String[] studentProgramCodes = Student.getProgramCodesForStudent();
        
        // Check if program in student entity
        if (Arrays.asList(studentProgramCodes).contains(code)) {
            return student.getProgramParticipation(code);
        } 
        
        // Now check program participation
        for (StudentProgramAssociation p : programs) {
            if (p.getStudentId().equals(student.getId())) {
                return Arrays.asList(p.getPrograms()).contains(code);
            }
        }
        return false;
    }
}

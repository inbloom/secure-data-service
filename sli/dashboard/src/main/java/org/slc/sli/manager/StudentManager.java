package org.slc.sli.manager;

import org.slc.sli.entity.Student;
import org.slc.sli.entity.StudentProgramAssociation;
import org.slc.sli.util.SecurityUtil;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.config.Field;
import org.slc.sli.config.ConfigUtil;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * StudentManager supplies student data to the controllers.
 * Given a configuration object, it will source the correct data, apply
 * the necessary business logic, and return the results. 
 * 
 * @author dwu
 *
 */
public class StudentManager extends Manager {
    
    
    public List<Student> getStudentInfo(String username, List<String> studentIds, ViewConfig config) {
        
        // extract the studentInfo data fields
        List<Field> dataFields = ConfigUtil.getDataFields(config, "studentInfo");
        
        // call the api
        // TODO: do we need more logic to grab the correct fields?
        List<Student> studentInfo = new ArrayList<Student>();
        if (dataFields.size() > 0) {
            studentInfo.addAll(Arrays.asList(apiClient.getStudents(SecurityUtil.getToken(), studentIds)));
        }
        
        // return the results
        return studentInfo;
    }

    /**
     * Returns the student program association data for the giving list of students
     */    
    public List<StudentProgramAssociation> getStudentProgramAssociations(String username, List<String> studentIds) {
        List<StudentProgramAssociation> programs = new ArrayList<StudentProgramAssociation>();
        programs.addAll(Arrays.asList(apiClient.getStudentProgramAssociation(SecurityUtil.getToken(), studentIds)));
        return programs;
    }
}

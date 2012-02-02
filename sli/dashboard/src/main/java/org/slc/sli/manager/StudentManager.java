package org.slc.sli.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slc.sli.config.ConfigUtil;
import org.slc.sli.config.Field;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.StudentProgramAssociation;
import org.slc.sli.util.SecurityUtil;

/**
 * StudentManager supplies student data to the controllers.
 * Given a configuration object, it will source the correct data, apply
 * the necessary business logic, and return the results. 
 * 
 * @author dwu
 *
 */
public class StudentManager extends Manager {
    
    
    public List<GenericEntity> getStudentInfo(String username, List<String> studentIds, ViewConfig config) {
        
        // extract the studentInfo data fields
        List<Field> dataFields = ConfigUtil.getDataFields(config, "studentInfo");
        
        // call the api
        List<GenericEntity> studentInfo = new ArrayList<GenericEntity>();
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

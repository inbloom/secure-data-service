package org.slc.sli.view;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.slc.sli.config.Field;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.util.StudentProgramUtil;
import org.slc.sli.util.Constants;



/**
 * A utility class for views in SLI dashboard. As a wrapper around student data passed onto
 *  dashboard views. Contains useful tools look up student data
 *
 * @author syau
 *
 */
public class StudentResolver {

    List<GenericEntity> studentSummaries;

    /**
     * Constructor
     */
    public StudentResolver(List<GenericEntity> studentSummaryList) {
        studentSummaries = studentSummaryList;
    }
    
    public List<GenericEntity> list() {
        return studentSummaries;
    }

    /**
     * Returns the string representation of the student information, identified by the datapoint ID
     * We pass in Map here because freemarker doesn't seem to like GenericEntity
     */
    //public String get(Field field, GenericEntity student) {
    public String get(Field field, Map student) {

        String dataPointName = field.getValue();
        if (dataPointName == null) { return ""; }
        if (dataPointName.equals(Constants.ATTR_NAME)) {
            
            return ((Map) (student.get(Constants.ATTR_NAME))).get(Constants.ATTR_FIRST_NAME) + " " 
                 + ((Map) (student.get(Constants.ATTR_NAME))).get(Constants.ATTR_LAST_SURNAME);
        } 
        return "";
    }

    /**
     * returns true if the given lozenge code applies to the given student
     */
    public boolean lozengeApplies(Map student, String code) {
        
        String[] studentProgramCodes = StudentProgramUtil.getProgramCodesForStudent();
        
        // Check if program in student entity
        if (Arrays.asList(studentProgramCodes).contains(code)) {
            return StudentProgramUtil.hasProgramParticipation(student, code);
        } 
        
        // Now check program participation
        List<String> programs = (List<String>) (student.get(Constants.ATTR_PROGRAMS));
        if (programs != null) {
            return programs.contains(code);
        }

        return false;
    }

    public void filterStudents(String filterName) {

        if (filterName != null && filterName != "") {
            
            List<GenericEntity> filteredStudents = new ArrayList<GenericEntity>();
            for (GenericEntity student : studentSummaries) {
                Map studentMap = (Map) student;
                if (lozengeApplies(studentMap, filterName)) {
                    filteredStudents.add((GenericEntity) studentMap);
                }
            }
            this.studentSummaries = filteredStudents;
        }
    }
}

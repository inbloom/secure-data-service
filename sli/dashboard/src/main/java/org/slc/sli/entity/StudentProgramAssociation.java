package org.slc.sli.entity;

/**
 * Represents the program participation of students
 * Corresponds to the StudentProgramAssociation in Ed-fi schema. 
 *
 */
public class StudentProgramAssociation {

    private String studentId;
    private String[] programs;
    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    public String[] getPrograms() {
        return programs;
    }
    public void setPrograms(String[] programNames) {
        this.programs = programNames;
    }
    
}

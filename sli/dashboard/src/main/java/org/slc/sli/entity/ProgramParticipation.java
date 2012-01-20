package org.slc.sli.entity;

/**
 * Represents the program participation of students
 *
 */
public class ProgramParticipation {

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

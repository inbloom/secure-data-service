package org.slc.sli.entity;


/**
 * 
 * TODO: Write Javadoc
 *
 */
public class Section {

    private String[] studentUIDs;
    private String section;
    public String[] getStudentUIDs() {
        return studentUIDs;
    }
    public void setStudentUIDs(String[] students) {
        this.studentUIDs = students;
    }
    public String getSection() {
        return section;
    }
    public void setSection(String section) {
        this.section = section;
    }
    
}

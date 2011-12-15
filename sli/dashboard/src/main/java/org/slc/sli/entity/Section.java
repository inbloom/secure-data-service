package org.slc.sli.entity;


/**
 * 
 * TODO: Write Javadoc
 *
 */
public class Section {

    private Student[] students;
    private String section;
    public Student[] getStudents() {
        return students;
    }
    public void setStudentList(Student[] students) {
        this.students = students;
    }
    public String getSection() {
        return section;
    }
    public void setSection(String section) {
        this.section = section;
    }
    
}

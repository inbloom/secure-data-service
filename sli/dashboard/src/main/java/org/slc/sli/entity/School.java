package org.slc.sli.entity;

/**
 * 
 * TODO: Write Javadoc
 *
 */
public class School {

    private Course[] courses;
    private String schoolId;
    
    public Course[] getCourses() {
        return courses;
    }
    public void setCourses(Course[] courses) {
        this.courses = courses;
    }
    public String getSchoolId() {
        return schoolId;
    }
    public void setSchoolId(String id) {
        this.schoolId = id;
    }
    
}

package org.slc.sli.entity;

import org.apache.commons.lang.ArrayUtils;

/**
 * 
 * TODO: Write Javadoc
 *
 */
public class School {

    private Course[] courses;
    private String schoolId;
    
    private String id;
    private String nameOfInstitution;
    String[] gradesOffered;
    
    public String[] getGradesOffered() {
        return gradesOffered;
    }
    public void setGradesOffered(String[] gradesOffered) {
        this.gradesOffered = gradesOffered;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getNameOfInstitution() {
        return nameOfInstitution;
    }
    public void setNameOfInstitution(String nameOfInstitution) {
        this.nameOfInstitution = nameOfInstitution;
    }
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
    
    public void addCourses(Course[] courses) {
       this.courses = (Course[]) ArrayUtils.addAll(this.courses, courses);
    }
    
    @Override
    public boolean equals(Object o){
        School sc = (School) o;
        return (sc.getId() == this.id);
    }
    
    
}

package org.slc.sli.entity;

public class Course {

    private Section[] sections;
    private String course;
    
    public Section[] getSections() {
        return sections;
    }
    public void setSections(Section[] sections) {
        this.sections = sections;
    }
    public String getCourse() {
        return course;
    }
    public void setCourse(String course) {
        this.course = course;
    }
    
}

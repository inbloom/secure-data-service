package org.slc.sli.entity;

import org.apache.commons.lang.ArrayUtils;


/**
 * 
 * TODO: Write Javadoc
 *
 */
public class Course {

    private Section[] sections;
    private String course, schoolId;
    
    public String getSchoolId() {
        return schoolId;
    }
    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }
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
    
    public void addSections(Section[] sections) {
        this.sections = (Section[]) ArrayUtils.addAll(this.sections, sections);
    }
    
}

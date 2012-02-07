package org.slc.sli.entity;

import org.apache.commons.lang.ArrayUtils;


/**
 * 
 * TODO: Write Javadoc
 *
 */
public class Course {

    private Section[] sections;
    private String courseTitle;
    private String id;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Section[] getSections() {
        return sections;
    }
    public void setSections(Section[] sections) {
        this.sections = sections;
    }
    public String getCourseTitle() {
        return courseTitle;
    }
    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }
    
    public void addSections(Section[] sections) {
        this.sections = (Section[]) ArrayUtils.addAll(this.sections, sections);
    }
    
}

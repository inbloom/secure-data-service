package org.slc.sli.entity;


/**
 * 
 * TODO: Write Javadoc
 *
 */
public class Section {

    private String[] studentUIDs;
    private String uniqueSectionCode, id;
    private String sectionName;
    
    public String getSectionName() {
        return sectionName;
    }
    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String[] getStudentUIDs() {
        return studentUIDs;
    }
    public void setStudentUIDs(String[] students) {
        this.studentUIDs = students;
    }
    public String getUniqueSectionCode() {
        return uniqueSectionCode;
    }
    public void setUniqueSectionCode(String uniqueSectionCode) {
        this.uniqueSectionCode = uniqueSectionCode;
    }

    
}

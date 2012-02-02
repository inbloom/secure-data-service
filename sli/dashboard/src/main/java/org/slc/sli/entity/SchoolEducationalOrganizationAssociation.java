package org.slc.sli.entity;

/**
 * 
 * Entity to represent the School to Educational Organization Association 
 *
 */
public class SchoolEducationalOrganizationAssociation {

    private String schoolId;
    private String educationOrganizationId;
    
    public String getSchoolId() {
        return schoolId;
    }
    public void setSchoolId(String id) {
        this.schoolId = id;
    }
    public String getEducationOrganizationId() {
        return educationOrganizationId;
    }
    public void setEducationOrganizationId(String id) {
        this.educationOrganizationId = id;
    }

}

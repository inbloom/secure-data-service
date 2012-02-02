package org.slc.sli.entity;

/**
 * 
 * Entity to represent the Educational Organization Association 
 *
 */
public class EducationalOrganizationAssociation {

    private String educationOrganizationParentId;
    private String educationOrganizationChildId;
    
    public String getEducationOrganizationParentId() {
        return educationOrganizationParentId;
    }
    public void setEducationOrganizationParentId(String id) {
        this.educationOrganizationParentId = id;
    }
    public String getEducationOrganizationChildId() {
        return educationOrganizationChildId;
    }
    public void setEducationOrganizationChildId(String id) {
        this.educationOrganizationChildId = id;
    }

}

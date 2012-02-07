package org.slc.sli.entity;

/**
 *
 * Entity to represent the Educational Organization entity
 *
 */
public class EducationalOrganization {

    // These field are defined in the API.
    // Dashboard only cares about two fields
    private String id;
    private String nameOfInstitution;

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
}

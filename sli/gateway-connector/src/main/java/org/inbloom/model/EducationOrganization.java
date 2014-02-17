package org.inbloom.model;

import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * POJO to represent edorg
 * @author ben morgan
 */
public class EducationOrganization {
    @Id
    String id;

    String nameOfInstitution;
    String stateEdOrgId;

    List<String> parentEdOrgReferences;
    List<String> schoolCategories;
    List<String> gradesOffered;

    public String toString()
    {
        return "[\"" + id + "\", \"" + nameOfInstitution + "\", \"" + stateEdOrgId + "\"]";
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

    public String getStateEdOrgId() {
        return stateEdOrgId;
    }

    public void setStateEdOrgId(String stateEdOrgId) {
        this.stateEdOrgId = stateEdOrgId;
    }

    public List<String> getParentEdOrgReferences() {
        return parentEdOrgReferences;
    }

    public void setParentEdOrgReferences(List<String> parentEdOrgReferences) {
        this.parentEdOrgReferences = parentEdOrgReferences;
    }

    public List<String> getSchoolCategories() {
        return schoolCategories;
    }

    public void setSchoolCategories(List<String> schoolCategories) {
        this.schoolCategories = schoolCategories;
    }

    public List<String> getGradesOffered() {
        return gradesOffered;
    }

    public void setGradesOffered(List<String> gradesOffered) {
        this.gradesOffered = gradesOffered;
    }
}

/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.sif.domain.slientity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;

/**
 * An SLI Entity corresponding to an SLI School.
 *
 * @author slee
 *
 */
public class SchoolEntity extends GenericEntity
{
    /**
     * _____mappingg_between_SIF_and_SLI_for_School_______________
     *
     * SLI domain                               SIF domain
     * -----------------------------------------------------------
     * stateOrganizationId                      StateProvinceId
     * nameOfInstitution                        SchoolName
     * organizationCategories [1..*]
     * address [1..*]                           AddressList
     * schoolType [0..1]                        SchoolFocusList
     * charterStatus [0..1]
     * titleIPartASchoolDesignation [0..1]
     * magnetSpecialProgramEmphasisSchool [0..1]
     * administrativeFundingControl [0..1]
     * shortNameOfInstitution [0..1]
     * webSite [0..1]                           SchoolURL
     * operationalStatus [0..1]                 OperationalStatus
     * agencyHierarchyName [0..1]
     * parentEducationAgencyReference [0..1]
     * gradesOffered [0..*]                     GradeLevels
     * schoolCategories [0..*]                  SchoolType
     * educationOrgIdentificationCode [0..*]
     * telephone [0..*]                         PhoneNumberList
     * accountabilityRatings [0..*]
     * programReference [0..*]
     *
     */
    private String stateOrganizationId;
    private String nameOfInstitution;
    private List<String> organizationCategories;
    private List<Address> address;
    private String schoolType;
    private String webSite;
    private String operationalStatus;
    private List<String> gradesOffered;
    private String schoolCategory;
    private List<String> schoolCategories;
    private List<InstitutionTelephone> telephone;

    /**
     * The following fields have no counterparts found in SIF doamin,
     * and hence not mapped:
     * <ol>
     * <li>charterStatus</li>
     * <li>titleIPartASchoolDesignation</li>
     * <li>magnetSpecialProgramEmphasisSchool</li>
     * <li>shortNameOfInstitution</li>
     * <li>agencyHierarchyName</li>
     * <li>parentEducationAgencyReference</li>
     * <li>educationOrgIdentificationCode</li>
     * <li>accountabilityRatings</li>
     * <li>programReference</li>
     * </ol>
     */
    private String charterStatus;
    private String titleIPartASchoolDesignation;
    private String magnetSpecialProgramEmphasisSchool;
    private String shortNameOfInstitution;
    private String agencyHierarchyName;
    private String parentEducationAgencyReference;
    private List<String> educationOrgIdentificationCode;
    private List<String> accountabilityRatings;
    private List<String> programReference;

    /**
     *  Constructor
     */
    public SchoolEntity() {
        super();
        /*
         * organizationCategories is mandatory but not counterpart in SIF SchoolInfo
         * So set it to School
         */
        this.organizationCategories = new ArrayList<String>(1);
        organizationCategories.add("School");
    }

    public List<String> getOrganizationCategories() {
        return this.organizationCategories;
    }

    public void setStateOrganizationId(String stateOrganizationId) {
        this.stateOrganizationId = stateOrganizationId;
    }

    public String getStateOrganizationId() {
        return this.stateOrganizationId;
    }

    public void setNameOfInstitution(String nameOfInstitution) {
        this.nameOfInstitution = nameOfInstitution;
    }

    public String getNameOfInstitution() {
        return this.nameOfInstitution;
    }

    public void setSchoolType(String schoolType) {
        this.schoolType = schoolType;
    }

    public String getSchoolType() {
        return this.schoolType;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getWebSite() {
        return this.webSite;
    }

    public void setOperationalStatus(String operationalStatus) {
        this.operationalStatus = operationalStatus;
    }

    public String getOperationalStatus() {
        return this.operationalStatus;
    }

    public void setGradesOffered(List<String> gradesOffered) {
        this.gradesOffered = gradesOffered;
    }

    public List<String> getGradesOffered() {
        return this.gradesOffered;
    }

    public void setSchoolCategory(String schoolCategory) {
        this.schoolCategory = schoolCategory;
        this.schoolCategories = new ArrayList<String>(1);
        schoolCategories.add(schoolCategory);
    }

    public List<String> getSchoolCategories() {
        return this.schoolCategories;
    }

    public void setTelephone(List<InstitutionTelephone> telephone) {
        this.telephone = telephone;
    }

    public List<InstitutionTelephone> getTelephone() {
        return this.telephone;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public List<Address> getAddress() {
        return this.address;
    }

    /**
     * Output this Entity as a JSON Node
     */
    @Override
    public JsonNode json() {
        try
        {
            return mapper.readTree(jsonString());
        } catch (JsonProcessingException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Output this object as a JSON String
     */
    @Override
    public String jsonString() {
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return super.toString();
        }
    }

}

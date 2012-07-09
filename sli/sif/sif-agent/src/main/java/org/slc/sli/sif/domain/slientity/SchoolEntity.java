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

import java.util.List;

/**
 * An SLI Entity corresponding to an SLI School.
 *
 * @author slee
 *
 */
public class SchoolEntity extends GenericEntity
{
    /*
     * SLI domain                               SIF domain
     * ------------------------------mappingg----------------------
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
    private String address;
    private String schoolType;
    private String charterStatus;
    private String titleIPartASchoolDesignation;
    private String magnetSpecialProgramEmphasisSchool;
    private String shortNameOfInstitution;
    private String webSite;
    private String operationalStatus;
    private String agencyHierarchyName;
    private String parentEducationAgencyReference;
    private List<String> gradesOffered;
    private List<String> schoolCategories;
    private List<String> educationOrgIdentificationCode;
    private List<String> telephone;
    private List<String> accountabilityRatings;
    private List<String> programReference;

    /**
     *  Constructor
     */
    public SchoolEntity() {
        super();
    }


}

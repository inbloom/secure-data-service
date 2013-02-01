/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import java.util.ArrayList;
import java.util.List;

/**
 * An SLI Entity corresponding to an educationOrganization of stateEducationAgency type
 * defined in SLI schema.
 * Each SLI Entity can be converted to a JSON Node ready for SLI operations.
 *
 * Note that some educationOrganization fields defined in SLI schema have no counterparts in SIF SEAInfo,
 * and those fields have no get/setters defined here.
 *
 * @author slee
 *
 */
public class SeaEntity extends SliEntity {
    /**
     * _____mappingg_between_SIF_and_SLI_for_School_______________
     *
     * SLI domain                               SIF domain
     * -----------------------------------------------------------
     * stateOrganizationId                      RefId
     * nameOfInstitution                        SEAName
     * organizationCategories [1..*]
     * address [1..*]                           AddressList
     * shortNameOfInstitution [0..1]
     * webSite [0..1]                           SEAURL
     * operationalStatus [0..1]                 OperationalStatus
     * agencyHierarchyName [0..1]
     * parentEducationAgencyReference [0..1]
     * educationOrgIdentificationCode [0..*]
     * telephone [0..*]                         PhoneNumberList
     *
     */
    private String stateOrganizationId;
    private String nameOfInstitution;
    private List<String> organizationCategories;
    private List<Address> address;
    private String webSite;
    private String operationalStatus;
    private List<InstitutionTelephone> telephone;

    /**
     * The following fields have no counterparts found in SIF doamin,
     * and hence not mapped:
     * <ol>
     * <li>shortNameOfInstitution</li>
     * <li>agencyHierarchyName</li>
     * <li>parentEducationAgencyReference</li>
     * <li>educationOrgIdentificationCode</li>
     * <li>accountabilityRatings</li>
     * <li>programReference</li>
     * </ol>
     */
    private String shortNameOfInstitution;
    private String agencyHierarchyName;
    private String parentEducationAgencyReference;
    private List<String> educationOrgIdentificationCode;
    private List<String> accountabilityRatings;
    private List<String> programReference;

    /**
     *  Constructor
     */
    public SeaEntity() {
        super();
        /*
         * organizationCategories is mandatory but not counterpart in SIF SEAInfo
         * So set it to localEducationAgency
         */
        this.organizationCategories = new ArrayList<String>(1);
        organizationCategories.add("State Education Agency");
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

    @Override
    public String entityType() {
        return "educationOrganization";
    }

}


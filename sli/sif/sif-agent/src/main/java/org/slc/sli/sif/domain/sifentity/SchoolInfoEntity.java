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

package org.slc.sli.sif.domain.sifentity;

import openadk.library.common.AddressList;
import openadk.library.common.GradeLevels;
import openadk.library.common.IdentificationInfoList;
import openadk.library.common.PhoneNumberList;
import openadk.library.common.SchoolContactList;
import openadk.library.student.OtherLEA;
import openadk.library.student.PrincipalInfo;
import openadk.library.student.SchoolFocus;
import openadk.library.student.SchoolFocusList;
import openadk.library.student.SchoolInfo;

/**
 * An SIF Entity corresponding to an SIF SchoolInfo.
 *
 * @author slee
 *
 */
public class SchoolInfoEntity extends GenericEntity
{
    /*
     * SLI domain                       SIF domain
     * --------------------------------------------------
     * stateOrganizationId              StateProvinceId
     * nameOfInstitution                SchoolName
     * organizationCategories           SchoolType ???
     * address                          AddressList
     * schoolType                       SchoolType
     * charterStatus
     * titleIPartASchoolDesignation
     * magnetSpecialProgramEmphasisSchool
     * administrativeFundingControl
     * shortNameOfInstitution
     * webSite                          SchoolURL
     * operationalStatus                OperationalStatus
     * agencyHierarchyName
     * parentEducationAgencyReference
     * gradesOffered                    GradeLevels
     * schoolCategories                 SchoolFocusList ???
     * educationOrgIdentificationCode
     * telephone                        PhoneNumberList
     * accountabilityRatings
     * programReference
     *
     */

    private SchoolInfo schoolInfo;
    private String RefId;
    private String StateProvinceId;
    private String LocalId;
    private String NCESId;
    private String SchoolName;
    private String LEAInfoRefId;
    private OtherLEA OtherLEA;
    private String SchoolType;
    private SchoolFocusList SchoolFocusList;
    private String SchoolFocusType;
    private String SchoolURL;
    private PrincipalInfo PrincipalInfo;
    private SchoolContactList SchoolContactList;
    private AddressList AddressList;
    private PhoneNumberList PhoneNumberList;
    private IdentificationInfoList IdentificationInfoList;
    private String SessionType;
    private GradeLevels GradeLevels;
    private String Title1Status;
    private String OperationalStatus;
    private String CongressionalDistrict;

    /**
     *  Constructor
     */
    public SchoolInfoEntity(SchoolInfo schoolInfo) {
        super( schoolInfo );
        this.schoolInfo = schoolInfo;
        this.RefId = schoolInfo.getRefId();
        this.StateProvinceId = schoolInfo.getStateProvinceId();
        this.LocalId = schoolInfo.getLocalId();
        this.NCESId = schoolInfo.getNCESId();
        this.SchoolName = schoolInfo.getSchoolName();
        this.LEAInfoRefId = schoolInfo.getLEAInfoRefId();
        this.OtherLEA = schoolInfo.getOtherLEA();
        this.SchoolType = schoolInfo.getSchoolType();
        this.SchoolFocusList = schoolInfo.getSchoolFocusList();
        if (this.SchoolFocusList!=null && this.SchoolFocusList.getSchoolFocuses().length>0) {
            SchoolFocus[] schoolFocus = this.SchoolFocusList.getSchoolFocuses();
            this.SchoolFocusType = schoolFocus[0].getValue();
        }
        this.SchoolURL = schoolInfo.getSchoolURL();
        this.PrincipalInfo = schoolInfo.getPrincipalInfo();
        this.SchoolContactList = schoolInfo.getSchoolContactList();
        this.AddressList = schoolInfo.getAddressList();
        this.PhoneNumberList = schoolInfo.getPhoneNumberList();
        this.IdentificationInfoList = schoolInfo.getIdentificationInfoList();
        this.SessionType = schoolInfo.getSessionType();
        this.GradeLevels = schoolInfo.getGradeLevels();
        this.Title1Status = schoolInfo.getTitle1Status();
        this.OperationalStatus = schoolInfo.getOperationalStatus();
        this.CongressionalDistrict = schoolInfo.getCongressionalDistrict();
    }

}

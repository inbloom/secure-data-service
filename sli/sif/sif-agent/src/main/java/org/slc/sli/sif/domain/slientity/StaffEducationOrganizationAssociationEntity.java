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

/**
 * Represents the staffEducationOrganizationAssociation in SLI datamodel
 *
 * @author slee
 *
 */
public class StaffEducationOrganizationAssociationEntity extends SliEntity {
    // mandatory fields
    private String staffReference;
    private String educationOrganizationReference;
    private String staffClassification;
    private String beginDate;

    // optional fields
    private String positionTitle;
    private String endDate;

    /**
     * Constructor
     */
    public StaffEducationOrganizationAssociationEntity() {
        super();
    }

    public String getStaffReference() {
        return this.staffReference;
    }

    public void setStaffReference(String staffReference) {
        this.staffReference = staffReference;
    }

    public String getEducationOrganizationReference() {
        return this.educationOrganizationReference;
    }

    public void setEducationOrganizationReference(String educationOrganizationReference) {
        this.educationOrganizationReference = educationOrganizationReference;
    }

    public String getStaffClassification() {
        return this.staffClassification;
    }

    public void setStaffClassification(String staffClassification) {
        this.staffClassification = staffClassification;
    }

    public String getPositionTitle() {
        return this.positionTitle;
    }

    public void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }

    public String getBeginDate() {
        return this.beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String entityType() {
        return "staffEducationOrganizationAssociation";
    }

}

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

import java.util.List;


/**
 * Represents the staff in SLI datamodel
 *
 * @author slee
 *
 */
public class StaffEntity extends SliEntity {
    // mandatory fields
    private String staffUniqueStateId;
    private Name name;
    private String sex; // Demographics
    private BirthData birthData; // Demographics
    private boolean hispanicLatinoEthnicity; // Demographics

    // The following mandatory field is supposed to be mapped from SIF EmployeeCredential
    private String highestLevelOfEducationCompleted = "No Degree";

    // optional fields
    private List<OtherName> otherName;
    private List<Address> address;
    private List<PersonalTelephone> telephone;
    private List<ElectronicMail> electronicMail;
    private List<String> race; // Demographics
    private List<StaffIdentificationCode> staffIdentificationCode;

    // The following optional fields are supposed to be mapped from SIF EmployeeCredential
//    private List<Credential> credentials;//
//    private int yearsOfPriorProfessionalExperience;
//    private int yearsOfPriorTeachingExperience;


    /**
     * Constructor
     */
    public StaffEntity() {
        super();
    }

    public String getStaffUniqueStateId() {
        return this.staffUniqueStateId;
    }

    public void setStaffUniqueStateId(String staffUniqueStateId) {
        this.staffUniqueStateId = staffUniqueStateId;
    }

    public Name getName() {
        return this.name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public List<OtherName> getOtherName() {
        return this.otherName;
    }

    public void setOtherName(List<OtherName> otherName) {
        this.otherName = otherName;
    }

    public BirthData getBirthData() {
        return this.birthData;
    }

    public void setBirthData(BirthData birthData) {
        this.birthData = birthData;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public List<String> getRace() {
        return this.race;
    }

    public void setRace(List<String> race) {
        this.race = race;
    }

    public List<StaffIdentificationCode> getStaffIdentificationCode() {
        return this.staffIdentificationCode;
    }

    public void setStaffIdentificationCode(List<StaffIdentificationCode> staffIdentificationCode) {
        this.staffIdentificationCode = staffIdentificationCode;
    }

    public boolean getHispanicLatinoEthnicity() {
        return this.hispanicLatinoEthnicity;
    }

    public void setHispanicLatinoEthnicity(boolean hispanicLatinoEthnicity) {
        this.hispanicLatinoEthnicity = hispanicLatinoEthnicity;
    }

    public List<Address> getAddress() {
        return this.address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public List<PersonalTelephone> getTelephone() {
        return this.telephone;
    }

    public void setTelephone(List<PersonalTelephone> telephone) {
        this.telephone = telephone;
    }

    public List<ElectronicMail> getElectronicMail() {
        return this.electronicMail;
    }

    public void setElectronicMail(List<ElectronicMail> electronicMail) {
        this.electronicMail = electronicMail;
    }

    public String getHighestLevelOfEducationCompleted() {
        return this.highestLevelOfEducationCompleted;
    }

    public void setHighestLevelOfEducationCompleted(String highestLevelOfEducationCompleted) {
        this.highestLevelOfEducationCompleted = highestLevelOfEducationCompleted;
    }

    @Override
    public String entityType() {
        return "staff";
    }

}


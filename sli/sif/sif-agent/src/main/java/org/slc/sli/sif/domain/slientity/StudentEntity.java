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
 * Represents the student in SLI datamodel
 *
 * @author slee
 *
 */
public class StudentEntity extends SliEntity {
    // mandatory fields
    private String studentUniqueStateId;
    private Name name;
    private String sex; // Demographics
    private BirthData birthData; // Demographics
    private boolean hispanicLatinoEthnicity; // Demographics
    private boolean economicDisadvantaged;

    // optional fields
    private List<OtherName> otherName;
    private List<Address> address;
    private List<PersonalTelephone> telephone;
    private List<ElectronicMail> electronicMail;
    private List<String> race; // Demographics
    private String limitedEnglishProficiency;
    private List<String> languages; // Demographics
    private List<String> homeLanguages; // Demographics
    private String gradeLevel; // MostRecent
    private String schoolId; // MostRecent
    // not covered yet
    private String section504Disabilities; // section504

    /**
     * Constructor
     */
    public StudentEntity() {
        super();
    }

    public String getStudentUniqueStateId() {
        return this.studentUniqueStateId;
    }

    public void setStudentUniqueStateId(String studentUniqueStateId) {
        this.studentUniqueStateId = studentUniqueStateId;
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

    public String getSchoolId() {
        return this.schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getGradeLevel() {
        return this.gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
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

    public List<String> getLanguages() {
        return this.languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getHomeLanguages() {
        return this.homeLanguages;
    }

    public void setHomeLanguages(List<String> homeLanguages) {
        this.homeLanguages = homeLanguages;
    }

    public String getLimitedEnglishProficiency() {
        return this.limitedEnglishProficiency;
    }

    public void setLimitedEnglishProficiency(String limitedEnglishProficiency) {
        this.limitedEnglishProficiency = limitedEnglishProficiency;
    }

    public boolean getHispanicLatinoEthnicity() {
        return this.hispanicLatinoEthnicity;
    }

    public void setHispanicLatinoEthnicity(boolean hispanicLatinoEthnicity) {
        this.hispanicLatinoEthnicity = hispanicLatinoEthnicity;
    }

    public boolean getEconomicDisadvantaged() {
        return this.economicDisadvantaged;
    }

    public void setEconomicDisadvantaged(boolean economicDisadvantaged) {
        this.economicDisadvantaged = economicDisadvantaged;
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

    @Override
    public String entityType() {
        return "student";
    }

}

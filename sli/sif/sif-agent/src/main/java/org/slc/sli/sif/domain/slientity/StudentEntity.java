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
 * Represents the student in SLI datamodel
 *
 * @author slee
 *
 */
public class StudentEntity extends SliEntity
{
    // mandatory fields
    private String studentUniqueStateId;
    private Name name;
    private String sexType;//Demographics
    private BirthData birthData;//Demographics
    private boolean hispanicLatinoEthnicity;//Demographics
    private boolean economicDisadvantaged;
    
    // optional fields
    private String studentIdentificationCode;
    private Name otherName;
    private List<Address> address;
    private List<InstitutionTelephone> telephone;
    private List<ElectronicMail> electronicMail;
    
    private String race;//Demographics
    private String limitedEnglishProficiency;
    private String languages;//Demographics
    private String homeLanguages;//Demographics
    private String section504Disabilities;//section504
    private String gradeLevel;//MostRecent
    private String schoolId;//MostRecent

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
    
    public Name getOtherName() {
        return this.otherName;
    }

    public void setOtherName(Name otherName) {
        this.otherName = otherName;
    }
    
    public BirthData getBirthData() {
        return this.birthData;
    }

    public void setBirthData(BirthData birthData) {
        this.birthData = birthData;
    }
    
    public String getSexType() {
        return this.sexType;
    }

    public void setSexType(String sexType) {
        this.sexType = sexType;
    }
    
    public List<Address> getAddress() {
        return this.address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public List<InstitutionTelephone> getTelephone() {
        return this.telephone;
    }

    public void setTelephone(List<InstitutionTelephone> telephone) {
        this.telephone = telephone;
    }

    public List<ElectronicMail> getElectronicMail() {
        return this.electronicMail;
    }

    public void setElectronicMail(List<ElectronicMail> electronicMail) {
        this.electronicMail = electronicMail;
    }



    
    
    @Override
    public String entityType()
    {
        return "student";
    }

}

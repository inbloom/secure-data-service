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
    private String name;
    private String sexType;//Demographics
    private String birthData;//Demographics
    private boolean hispanicLatinoEthnicity;//Demographics
    private boolean economicDisadvantaged;
    

    // optional fields
    private String studentIdentificationCode;
    private String otherName;
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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

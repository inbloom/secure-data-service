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
import openadk.library.common.PhoneNumberList;
import openadk.library.student.SchoolFocus;
import openadk.library.student.SchoolInfo;

import org.slc.sli.sif.domain.converter.SchoolConverter;

/**
 * An SIF Entity corresponding to an SIF SchoolInfo.
 *
 * @author slee
 *
 */
public class SchoolInfoEntity extends GenericEntity
{
    private SchoolInfo schoolInfo;

    /**
     *  Constructor
     */
    public SchoolInfoEntity(SchoolInfo schoolInfo) {
        super( schoolInfo );
        this.schoolInfo = schoolInfo;
    }

    public String getStateProvinceId() {
        return this.schoolInfo.getStateProvinceId();
    }

    public String getSchoolName() {
        return this.schoolInfo.getSchoolName();
    }

    public String getSchoolFocusType() {
        SchoolFocus[] schoolFocus = this.schoolInfo.getSchoolFocusList().getSchoolFocuses();
        return SchoolConverter.toSliSchoolType(schoolFocus[0].getValue());
    }

    public String getSchoolURL() {
        return this.schoolInfo.getSchoolURL();
    }

    public String getOperationalStatus() {
        return SchoolConverter.toSliOperationalStatus(this.schoolInfo.getOperationalStatus());
    }

    public String getSchoolType() {
        return SchoolConverter.toSliSchoolCategory(this.schoolInfo.getSchoolType());
    }

    public GradeLevels getGradeLevels() {
        return this.schoolInfo.getGradeLevels();
    }

    public PhoneNumberList getPhoneNumberList() {
        return this.schoolInfo.getPhoneNumberList();
    }

    public AddressList getAddressList() {
        return this.schoolInfo.getAddressList();
    }
}

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
 * SIF SchoolInfo has more getters than listed here. However, those fields without SLI counterparts are not
 * listed in dozer mapping file, and hence not included as getters of SchoolInfoEntity.
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

    /**
     *  Get StateProvinceId in an SIF SchoolInfoEntity.
     */
    public String getStateProvinceId() {
        return this.schoolInfo.getStateProvinceId();
    }

    /**
     *  Get SchoolName in an SIF SchoolInfoEntity.
     */
    public String getSchoolName() {
        return this.schoolInfo.getSchoolName();
    }

    /**
     *  Get SchoolFocusType in an SIF SchoolInfoEntity.
     *  Values of this field need to be mapped to SLI values.
     *
     */
    public String getSchoolFocusType() {
        SchoolFocus[] schoolFocus = this.schoolInfo.getSchoolFocusList().getSchoolFocuses();
        return SchoolConverter.toSliSchoolType(schoolFocus[0].getValue());
    }

    /**
     *  Get SchoolURL in an SIF SchoolInfoEntity.
     */
    public String getSchoolURL() {
        return this.schoolInfo.getSchoolURL();
    }

    /**
     *  Get OperationalStatus in an SIF SchoolInfoEntity.
     *  Values of this field need to be mapped to SLI values.
     *
     */
    public String getOperationalStatus() {
        return SchoolConverter.toSliOperationalStatus(this.schoolInfo.getOperationalStatus());
    }

    /**
     *  Get SchoolType in an SIF SchoolInfoEntity.
     *  Values of this field need to be mapped to SLI values.
     *
     */
    public String getSchoolType() {
        return SchoolConverter.toSliSchoolCategory(this.schoolInfo.getSchoolType());
    }

    /**
     *  Get GradeLevels in an SIF SchoolInfoEntity.
     *
     *  Customized Dozer converter is used by dozer to map this field.
     */
    public GradeLevels getGradeLevels() {
        return this.schoolInfo.getGradeLevels();
    }

    /**
     *  Get PhoneNumberList in an SIF SchoolInfoEntity.
     *
     *  Customized Dozer converter is used by dozer to map this field.
     */
    public PhoneNumberList getPhoneNumberList() {
        return this.schoolInfo.getPhoneNumberList();
    }

    /**
     *  Get AddressList in an SIF SchoolInfoEntity.
     *
     *  Customized Dozer converter is used by dozer to map this field.
     */
    public AddressList getAddressList() {
        return this.schoolInfo.getAddressList();
    }
}

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

import org.slc.sli.sif.domain.converter.SchoolMappings;

/**
 * An SIF Entity corresponding to an SIF SchoolInfo. Used by dozer to map SchoolInfo into an SLI SchoolEntity.
 *
 * Note: Not all fields of SIF SchoolInfo have SLI counterparts. Only those fields which have a valid
 * SLI counterpart have a getter defined here.
 *
 * Three mapping strategies are used when mapping those fields:
 * <ol>
 * <li>Direct value mapping where each SIF field value is mapped to the same SLI value. These include
 * <ol><li><code>StateProvinceId</code></li>
 *     <li><code>SchoolName</code></li>
 *     <li><code>SchoolURL</code></li></ol>
 * </li>
 *
 * <li>Simple value mapping where each SIF field value is mapped to a correnponding SLI value. These include
 * <ol><li><code>SchoolFocusType</code></li>
 *     <li><code>OperationalStatus</code></li>
 *     <li><code>SchoolType</code></li></ol>
 * </li>
 *
 * <li>Complex value mapping where each SIF field value is mapped via a customized Dozer converter. These include
 * <ol><li><code>GradeLevels</code></li>
 *     <li><code>PhoneNumberList</code></li>
 *     <li><code>AddressList</code></li></ol>
 * </li>
 * </ol>
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
     *  Get SchoolFocusType in an SIF SchoolInfoEntity
     *  Using a value mapping method.
     *
     */
    public String getSchoolFocusType() {
        SchoolFocus[] schoolFocus = this.schoolInfo.getSchoolFocusList().getSchoolFocuses();
        return SchoolMappings.toSliSchoolType(schoolFocus[0].getValue());
    }

    /**
     *  Get SchoolURL in an SIF SchoolInfoEntity.
     */
    public String getSchoolURL() {
        return this.schoolInfo.getSchoolURL();
    }

    /**
     *  Get OperationalStatus in an SIF SchoolInfoEntity.
     *  Using a value mapping method.
     *
     */
    public String getOperationalStatus() {
        return SchoolMappings.toSliOperationalStatus(this.schoolInfo.getOperationalStatus());
    }

    /**
     *  Get SchoolType in an SIF SchoolInfoEntity.
     *  Using a value mapping method.
     *
     */
    public String getSchoolType() {
        return SchoolMappings.toSliSchoolCategory(this.schoolInfo.getSchoolType());
    }

    /**
     *  Get GradeLevels in an SIF SchoolInfoEntity.
     *  Using a customized Dozer converter.
     *
     */
    public GradeLevels getGradeLevels() {
        return this.schoolInfo.getGradeLevels();
    }

    /**
     *  Get PhoneNumberList in an SIF SchoolInfoEntity.
     *  Using a customized Dozer converter.
     *
     */
    public PhoneNumberList getPhoneNumberList() {
        return this.schoolInfo.getPhoneNumberList();
    }

    /**
     *  Get AddressList in an SIF SchoolInfoEntity.
     *  Using a customized Dozer converter.
     *
     */
    public AddressList getAddressList() {
        return this.schoolInfo.getAddressList();
    }
}

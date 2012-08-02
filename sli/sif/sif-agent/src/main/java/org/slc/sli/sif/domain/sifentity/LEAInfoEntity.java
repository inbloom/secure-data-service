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
import openadk.library.common.PhoneNumberList;
import openadk.library.student.LEAInfo;

import org.slc.sli.sif.domain.converter.SchoolMappings;

/**
 * An SIF Entity corresponding to an SIF LEAInfo. Used by dozer to map LEAInfo
 * into an SLI LEAEntity.
 *
 * Note: Not all fields of SIF LEAInfo have SLI counterparts. Only those fields
 * which have a valid SLI counterpart have a getter defined here.
 *
 * Three mapping strategies are used when mapping those fields:
 * <ol>
 * <li>Direct value mapping where each SIF field value is mapped to the same SLI
 * value. These include
 * <ol>
 * <li><code>StateProvinceId</code></li>
 * <li><code>LEAName</code></li>
 * <li><code>LEAURL</code></li>
 * </ol>
 * </li>
 *
 * <li>Simple value mapping where each SIF field value is mapped to a
 * correnponding SLI value. These include
 * <ol>
 * <li><code>OperationalStatus</code></li>
 * </ol>
 * </li>
 *
 * <li>Complex value mapping where each SIF field value is mapped via a
 * customized Dozer converter. These include
 * <li><code>PhoneNumberList</code></li>
 * <li><code>AddressList</code></li>
 * </ol>
 * </li> </ol>
 *
 * @author slee
 *
 */
public class LEAInfoEntity extends GenericEntity {
    private LEAInfo leaInfo;

    /**
    * Constructor
    */
    public LEAInfoEntity(LEAInfo leaInfo) {
        super(leaInfo);
        this.leaInfo = leaInfo;
    }

    /**
    * Get StateProvinceId in an SIF LEAInfoEntity.
    */
    public String getStateProvinceId() {
        return this.leaInfo.getStateProvinceId();
    }

    /**
    * Get LEAName in an SIF LEAInfoEntity.
    */
    public String getLEAName() {
        return this.leaInfo.getLEAName();
    }

    /**
    * Get LEAURL in an SIF LEAInfoEntity.
    */
    public String getLEAURL() {
        return this.leaInfo.getLEAURL();
    }

    /**
    * Get OperationalStatus in an SIF LEAInfoEntity. Using a value mapping
    * method.
    *
    */
    public String getOperationalStatus() {
        if (this.leaInfo.getOperationalStatus() == null) {
            return null;
        }
        return SchoolMappings.toSliOperationalStatus(this.leaInfo
                .getOperationalStatus());
    }

    /**
    * Get PhoneNumberList in an SIF LEAInfoEntity. Using a customized Dozer
    * converter.
    *
    */
    public PhoneNumberList getPhoneNumberList() {
        return this.leaInfo.getPhoneNumberList();
    }

    /**
    * Get AddressList in an SIF LEAInfoEntity. Using a customized Dozer
    * converter.
    *
    */
    public AddressList getAddressList() {
        return this.leaInfo.getAddressList();
    }
}

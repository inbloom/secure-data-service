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
import openadk.library.datamodel.SEAInfo;

import org.slc.sli.sif.domain.converter.SchoolMappings;

/**
 * An SIF Entity corresponding to an SIF SEAInfo. Used by dozer to map SEAInfo into an SLI LEAEntity.
 *
 * Note: Not all fields of SIF SEAInfo have SLI counterparts. Only those fields which have a valid
 * SLI counterpart have a getter defined here.
 *
 * Three mapping strategies are used when mapping those fields:
 * <ol>
 * <li>Direct value mapping where each SIF field value is mapped to the same SLI value. These include
 * <ol><li><code>StateProvinceId</code></li>
 *     <li><code>LEAName</code></li>
 *     <li><code>LEAURL</code></li></ol>
 * </li>
 *
 * <li>Simple value mapping where each SIF field value is mapped to a correnponding SLI value. These include
 * <ol><li><code>OperationalStatus</code></li></ol>
 * </li>
 *
 * <li>Complex value mapping where each SIF field value is mapped via a customized Dozer converter. These include
 *     <li><code>PhoneNumberList</code></li>
 *     <li><code>AddressList</code></li></ol>
 * </li>
 * </ol>
 *
 * @author slee
 *
 */
public class SEAInfoEntity extends GenericEntity
{
    private SEAInfo seaInfo;

    /**
     *  Constructor
     */
    public SEAInfoEntity(SEAInfo seaInfo) {
        super( seaInfo );
        this.seaInfo = seaInfo;
    }

    /**
     *  Get StateProvinceId in an SIF LEAInfoEntity.
     */
    public String getRefId() {
        return this.seaInfo.getRefId();
    }

    /**
     *  Get SEAName in an SIF LEAInfoEntity.
     */
    public String getSEAName() {
        return this.seaInfo.getSEAName();
    }

    /**
     *  Get SEAURL in an SIF SEAInfoEntity.
     */
    public String getSEAURL() {
        return this.seaInfo.getSEAURL();
    }

    /**
     *  Get OperationalStatus in an SIF SEAInfoEntity.
     *  Using a value mapping method.
     *
     */
    public String getOperationalStatus() {
        return SchoolMappings.toSliOperationalStatus(this.seaInfo.getOperationalStatus());
    }

    /**
     *  Get PhoneNumberList in an SIF SEAInfoEntity.
     *  Using a customized Dozer converter.
     *
     */
    public PhoneNumberList getPhoneNumberList() {
        return this.seaInfo.getPhoneNumberList();
    }

    /**
     *  Get AddressList in an SIF SEAInfoEntity.
     *  Using a customized Dozer converter.
     *
     */
    public AddressList getAddressList() {
        return this.seaInfo.getAddressList();
    }
}


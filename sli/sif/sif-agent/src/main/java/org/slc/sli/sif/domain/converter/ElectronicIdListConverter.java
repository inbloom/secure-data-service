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

package org.slc.sli.sif.domain.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import openadk.library.common.ElectronicId;
import openadk.library.common.ElectronicIdList;
import openadk.library.common.ElectronicIdType;

import org.springframework.stereotype.Component;

import org.slc.sli.sif.domain.slientity.StaffIdentificationCode;

/**
 * A customized converter to convert SIF ElectronicIdList to SLI StaffIdentificationCode list.
 *
 * SLI values:
 *  Drivers License
 *  Health Record
 *  Medicaid
 *  Professional Certificate
 *  School
 *  District
 *  State
 *  Federal
 *  Other Federal
 *  Selective Service
 *  SSN
 *  US Visa
 *  PIN
 *  Canadian SIN
 *  Other
 *
 * @author vmcglaughlin
 *
 */
@Component
public class ElectronicIdListConverter {

    private static final Map<ElectronicIdType, String> ELECTRONIC_ID_TYPE_MAP = new HashMap<ElectronicIdType, String>();
    static {
        ELECTRONIC_ID_TYPE_MAP.put(ElectronicIdType.BARCODE, "Other");
        ELECTRONIC_ID_TYPE_MAP.put(ElectronicIdType.MAGSTRIPE, "Other");
        ELECTRONIC_ID_TYPE_MAP.put(ElectronicIdType.PIN, "PIN");
        ELECTRONIC_ID_TYPE_MAP.put(ElectronicIdType.RFID, "Other");
     }

    /**
     * Converts the SIF ElectronicIdList into a list of SLI StaffIdentificationCodes
     *
     * @param otherIdList
     * @return
     */
    public List<StaffIdentificationCode> convert(ElectronicIdList electronicIdList) {
        if (electronicIdList == null) {
            return null;
        }

        return toSliStaffIdentificationCodeList(electronicIdList.getElectronicIds());
    }

    private List<StaffIdentificationCode> toSliStaffIdentificationCodeList(ElectronicId[] electronicIds) {
        List<StaffIdentificationCode> list = new ArrayList<StaffIdentificationCode>(electronicIds.length);
        for (ElectronicId electronicId : electronicIds) {
            StaffIdentificationCode staffIdentificationCode = new StaffIdentificationCode();
            staffIdentificationCode.setID(electronicId.getValue());

            String mappedType = toSliIdentificationSystem(ElectronicIdType.wrap(electronicId.getType()));
            staffIdentificationCode.setIdentificationSystem(mappedType);
            list.add(staffIdentificationCode);


        }
        return list;
    }

    private String toSliIdentificationSystem(ElectronicIdType electronicIdType) {
        String mapped = ELECTRONIC_ID_TYPE_MAP.get(electronicIdType);
        return mapped == null ? "Other" : mapped;
    }

}

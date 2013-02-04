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

import openadk.library.common.OtherId;
import openadk.library.common.OtherIdList;
import openadk.library.common.OtherIdType;

import org.springframework.stereotype.Component;

import org.slc.sli.sif.domain.slientity.StaffIdentificationCode;

/**
 * A customized converter to convert SIF OtherIdList to SLI StaffIdentificationCode list.
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
public class OtherIdListConverter {

    private static final Map<OtherIdType, String> OTHER_ID_TYPE_MAP = new HashMap<OtherIdType, String>();
    static {
        OTHER_ID_TYPE_MAP.put(OtherIdType.ACT_INST, "Other");
        OTHER_ID_TYPE_MAP.put(OtherIdType.ACT_PROG, "Other");
        OTHER_ID_TYPE_MAP.put(OtherIdType.ATP, "Other");
        OTHER_ID_TYPE_MAP.put(OtherIdType.CA_SIN, "Canadian SIN");
        OTHER_ID_TYPE_MAP.put(OtherIdType.CERTIFICATE, "Professional Certificate");
        OTHER_ID_TYPE_MAP.put(OtherIdType.CONTRACTOR, "Other");
        OTHER_ID_TYPE_MAP.put(OtherIdType.DISTRICT_ASSIGNED, "District");
        OTHER_ID_TYPE_MAP.put(OtherIdType.DRIVERS_LICENSE, "Drivers License");
        OTHER_ID_TYPE_MAP.put(OtherIdType.DUNS, "Other");
        OTHER_ID_TYPE_MAP.put(OtherIdType.FAMILY_UNIT, "Other");
        OTHER_ID_TYPE_MAP.put(OtherIdType.FEDERAL, "Federal");
        OTHER_ID_TYPE_MAP.put(OtherIdType.HEALTH_RECORD, "Health Record");
        OTHER_ID_TYPE_MAP.put(OtherIdType.IPEDS, "Other");
        OTHER_ID_TYPE_MAP.put(OtherIdType.LEA_SCHOOL, "School");
        OTHER_ID_TYPE_MAP.put(OtherIdType.MEDICAID, "Medicaid");
        OTHER_ID_TYPE_MAP.put(OtherIdType.MIGRANT, "Other");
        OTHER_ID_TYPE_MAP.put(OtherIdType.NCES_LEA, "Other");
        OTHER_ID_TYPE_MAP.put(OtherIdType.NCES_LEA, "Other");
        OTHER_ID_TYPE_MAP.put(OtherIdType.OTHER, "Other");
        OTHER_ID_TYPE_MAP.put(OtherIdType.OTHER_AGENCY, "Other");
        OTHER_ID_TYPE_MAP.put(OtherIdType.OTHER_FEDERAL, "Other Federal");
        OTHER_ID_TYPE_MAP.put(OtherIdType.PERSONAL, "Other");
        OTHER_ID_TYPE_MAP.put(OtherIdType.SCHOOL_ASSIGNED, "School");
        OTHER_ID_TYPE_MAP.put(OtherIdType.SEA_LEA, "State");
        OTHER_ID_TYPE_MAP.put(OtherIdType.SEA_SCHOOL, "State");
        OTHER_ID_TYPE_MAP.put(OtherIdType.SELECTIVE_SERVICE, "Other");
        OTHER_ID_TYPE_MAP.put(OtherIdType.SIF1x_DISTRICT_ASSIGNED_NUM, "District");
        OTHER_ID_TYPE_MAP.put(OtherIdType.SIF1x_DRIVERS_LICENSE, "Drivers License");
        OTHER_ID_TYPE_MAP.put(OtherIdType.SIF1x_FAMILY_UNIT, "Other");
        OTHER_ID_TYPE_MAP.put(OtherIdType.SIF1x_HEATH_RECORD, "Health Record");
        OTHER_ID_TYPE_MAP.put(OtherIdType.SIF1x_MEDICAID, "Medicaid");
        OTHER_ID_TYPE_MAP.put(OtherIdType.SIF1x_MIGRANT, "Other");
        OTHER_ID_TYPE_MAP.put(OtherIdType.SIF1x_OTHER, "Other");
        OTHER_ID_TYPE_MAP.put(OtherIdType.SIF1x_PIN, "PIN");
        OTHER_ID_TYPE_MAP.put(OtherIdType.SIF1x_PROFESSIONAL_LICENSE, "Professional Certificate");
        OTHER_ID_TYPE_MAP.put(OtherIdType.SIF1x_SCHOOL_ASSIGNED_NUM, "School");
        OTHER_ID_TYPE_MAP.put(OtherIdType.SIF1x_SELECTIVE_SERVICE, "Selective Service");
        OTHER_ID_TYPE_MAP.put(OtherIdType.SIF1x_SSN, "SSN");
        OTHER_ID_TYPE_MAP.put(OtherIdType.SIF1x_STATE_ASSIGNED_NUM, "State");
        OTHER_ID_TYPE_MAP.put(OtherIdType.SIF1x_VISA, "US Visa");
    }

    /**
     * Converts the SIF OtherIdList into a list of SLI StaffIdentificationCodes
     *
     * @param otherIdList
     * @return
     */
    public List<StaffIdentificationCode> convert(OtherIdList otherIdList) {
        if (otherIdList == null) {
            return null;
        }

        return toSliStaffIdentificationCodeList(otherIdList.getOtherIds());
    }

    private List<StaffIdentificationCode> toSliStaffIdentificationCodeList(OtherId[] otherIds) {
        List<StaffIdentificationCode> list = new ArrayList<StaffIdentificationCode>(otherIds.length);
        for (OtherId otherId : otherIds) {
            StaffIdentificationCode staffIdentificationCode = new StaffIdentificationCode();
            staffIdentificationCode.setID(otherId.getValue());

            String mappedType = toSliIdentificationSystem(OtherIdType.wrap(otherId.getType()));
            staffIdentificationCode.setIdentificationSystem(mappedType);
            list.add(staffIdentificationCode);
        }
        return list;
    }

    private String toSliIdentificationSystem(OtherIdType otherIdType) {
        String mapped = OTHER_ID_TYPE_MAP.get(otherIdType);
        return mapped == null ? "Other" : mapped;
    }

}

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

package org.slc.sli.sif.domain.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import openadk.library.common.PhoneNumber;
import openadk.library.common.PhoneNumberList;
import openadk.library.common.PhoneNumberType;

import org.springframework.stereotype.Component;

import org.slc.sli.sif.domain.slientity.InstitutionTelephone;

/**
 * A customized converter to convert SIF PhoneNumberList to SLI telephone list.
 *
 * @author slee
 *
 */

@Component
public class PhoneNumberListConverter {

    private static final Map<PhoneNumberType, String> PHONE_NUMBER_TYPE_MAP = new HashMap<PhoneNumberType, String>();
    static {
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.ALT, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.ANSWERING_SERVICE, "Administrative");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.APPOINTMENT, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.BEEPER, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.FAX, "Fax");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.INSTANT_MESSAGING, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.MEDIA_CONFERENCE, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.PRIMARY, "Main");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_ALT, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_ANSWERING_SERVICE, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_APPT_NUMBER, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_BEEPER, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_EXT, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_HOME_FAX, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_HOME_PHONE, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_NIGHT_PHONE, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_OTHER_RES_FAX, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_OTHER_RES_PHONE, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_PERSONAL_CELL, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_PERSONAL_PHONE, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_TELEMAIL, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_TELEX, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_VOICEMAIL, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_WORK_CELL, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_WORK_FAX, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_WORK_PHONE, "");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.TELEMAIL, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.TELEX, "Other");
        PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.VOICE_MAIL, "Attendance");
    }

    /**
     * Converts the SIF PhoneNumberList into an SLI telephone list
     * @param phoneNumberList
     * @return
     */
    public List<InstitutionTelephone> convert(PhoneNumberList phoneNumberList){
        if (phoneNumberList == null) {
            return null;
        }

        return toSliTelephoneList(phoneNumberList.getPhoneNumbers());
    }

    private List<InstitutionTelephone> toSliTelephoneList(PhoneNumber[] phoneNumbers) {
        List<InstitutionTelephone> list = new ArrayList<InstitutionTelephone>(phoneNumbers.length);
        for (PhoneNumber phoneNumber : phoneNumbers) {
            InstitutionTelephone phone = new InstitutionTelephone();
            phone.setTelephoneNumber(phoneNumber.getNumber());

            String mappedType = toSliInstitutionTelephoneNumberType(PhoneNumberType.wrap(phoneNumber.getType()));
            phone.setInstitutionTelephoneNumberType(mappedType);
            list.add(phone);
        }
        return list;
    }

    private String toSliInstitutionTelephoneNumberType(PhoneNumberType phoneNumberType) {
        String mapped = PHONE_NUMBER_TYPE_MAP.get(phoneNumberType);
        return mapped == null ? "Other" : mapped;
    }
}

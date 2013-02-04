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

import openadk.library.common.PhoneNumber;
import openadk.library.common.PhoneNumberList;
import openadk.library.common.PhoneNumberType;

import org.springframework.stereotype.Component;

import org.slc.sli.sif.domain.slientity.InstitutionTelephone;
import org.slc.sli.sif.domain.slientity.PersonalTelephone;

/**
 * A customized converter to convert SIF PhoneNumberList to SLI telephone list.
 *
 * SLI values:
 * Main
 * Administrative
 * HealthClinic
 * Attendance
 * Other
 * Fax
 *
 * @author slee
 *
 */
@Component
public class PhoneNumberListConverter {

    private static final Map<PhoneNumberType, String> INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP = new HashMap<PhoneNumberType, String>();
    private static final Map<PhoneNumberType, String> PERSONAL_PHONE_NUMBER_TYPE_MAP = new HashMap<PhoneNumberType, String>();
    static {
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.ALT, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.ANSWERING_SERVICE, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.APPOINTMENT, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.BEEPER, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.FAX, "Fax");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.INSTANT_MESSAGING, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.MEDIA_CONFERENCE, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.PRIMARY, "Main");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_ALT, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_ANSWERING_SERVICE, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_APPT_NUMBER, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_BEEPER, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_EXT, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_HOME_FAX, "Fax");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_HOME_PHONE, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_NIGHT_PHONE, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_OTHER_RES_FAX, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_OTHER_RES_PHONE, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_PERSONAL_CELL, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_PERSONAL_PHONE, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_TELEMAIL, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_TELEX, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_VOICEMAIL, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_WORK_CELL, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_WORK_FAX, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_WORK_PHONE, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.TELEMAIL, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.TELEX, "Other");
        INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.VOICE_MAIL, "Other");

        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.ALT, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.ANSWERING_SERVICE, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.APPOINTMENT, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.BEEPER, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.FAX, "Fax");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.INSTANT_MESSAGING, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.MEDIA_CONFERENCE, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.PRIMARY, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_ALT, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_ANSWERING_SERVICE, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_APPT_NUMBER, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_BEEPER, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_EXT, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_HOME_FAX, "Fax");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_HOME_PHONE, "Home");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_NIGHT_PHONE, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_OTHER_RES_FAX, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_OTHER_RES_PHONE, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_PERSONAL_CELL, "Mobile");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_PERSONAL_PHONE, "Home");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_TELEMAIL, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_TELEX, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_VOICEMAIL, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_WORK_CELL, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_WORK_FAX, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.SIF1x_WORK_PHONE, "Work");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.TELEMAIL, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.TELEX, "Other");
        PERSONAL_PHONE_NUMBER_TYPE_MAP.put(PhoneNumberType.VOICE_MAIL, "Other");
    }

    /**
     * Converts the SIF PhoneNumberList into an SLI Institution Telephone list
     *
     * @param phoneNumberList
     * @return
     */
    public List<InstitutionTelephone> convertInstitutionTelephone(PhoneNumberList phoneNumberList) {
        if (phoneNumberList == null) {
            return null;
        }

        return toSliInstitutionTelephoneList(phoneNumberList.getPhoneNumbers());
    }

    private List<InstitutionTelephone> toSliInstitutionTelephoneList(PhoneNumber[] phoneNumbers) {
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
        String mapped = INSTITUTIONAL_PHONE_NUMBER_TYPE_MAP.get(phoneNumberType);
        return mapped == null ? "Other" : mapped;
    }

    /**
     * Converts the SIF PhoneNumberList into an SLI Personal Telephone list
     *
     * @param phoneNumberList
     * @return
     */
    public List<PersonalTelephone> convertPersonalTelephone(PhoneNumberList phoneNumberList) {
        if (phoneNumberList == null) {
            return null;
        }

        return toSliPersonalTelephoneList(phoneNumberList.getPhoneNumbers());
    }

    private List<PersonalTelephone> toSliPersonalTelephoneList(PhoneNumber[] phoneNumbers) {
        List<PersonalTelephone> list = new ArrayList<PersonalTelephone>(phoneNumbers.length);
        for (PhoneNumber phoneNumber : phoneNumbers) {
            PersonalTelephone phone = new PersonalTelephone();
            phone.setTelephoneNumber(phoneNumber.getNumber());

            String mappedType = toSliPersonalTelephoneNumberType(PhoneNumberType.wrap(phoneNumber.getType()));
            phone.setTelephoneNumberType(mappedType);
            list.add(phone);
        }
        return list;
    }

    private String toSliPersonalTelephoneNumberType(PhoneNumberType phoneNumberType) {
        String mapped = PERSONAL_PHONE_NUMBER_TYPE_MAP.get(phoneNumberType);
        return mapped == null ? "Other" : mapped;
    }
}

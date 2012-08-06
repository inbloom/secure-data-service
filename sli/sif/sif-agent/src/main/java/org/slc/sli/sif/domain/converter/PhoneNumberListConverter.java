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
import java.util.List;

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

            String mappedType = toSliInstitutionTelephoneNumberType(phoneNumber.getType());
            phone.setInstitutionTelephoneNumberType(mappedType);
            list.add(phone);
        }
        return list;
    }

    private String toSliInstitutionTelephoneNumberType(String phoneNumberType) {
        if (PhoneNumberType.ANSWERING_SERVICE.getValue()
                .equals(phoneNumberType)) {
            return "Administrative";
        }
        if (PhoneNumberType.FAX.getValue().equals(phoneNumberType)) {
            return "Fax";
        }
        if (PhoneNumberType.PRIMARY.getValue().equals(phoneNumberType)) {
            return "Main";
        }
        if (PhoneNumberType.VOICE_MAIL.getValue().equals(phoneNumberType)) {
            return "Attendance";
        }
        return "Other";
    }
}

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

import org.dozer.DozerConverter;

import org.slc.sli.sif.domain.slientity.InstitutionTelephone;

/**
 * A customized Dozer converter to convert SIF PhoneNumberList to SLI telephone list.
 *
 * @author slee
 *
 */
public class PhoneNumberListConverter extends DozerConverter<PhoneNumberList, List<InstitutionTelephone>>
{
    public PhoneNumberListConverter() {
        super(PhoneNumberList.class, (Class<List<InstitutionTelephone>>)new ArrayList<InstitutionTelephone>().getClass());
    }

    @Override
    public List<InstitutionTelephone> convertTo(PhoneNumberList source,
            List<InstitutionTelephone> destination)
    {
        if (source==null) {
            return null;
        }
        PhoneNumber[] phoneNumbers = source.getPhoneNumbers();
        List<InstitutionTelephone> list = new ArrayList<InstitutionTelephone>(phoneNumbers.length);
        for (PhoneNumber phoneNumber : phoneNumbers) {
            InstitutionTelephone phone = new InstitutionTelephone();
            phone.setTelephoneNumber(phoneNumber.getNumber());
            phone.setInstitutionTelephoneNumberType(SchoolMappings.toSliInstitutionTelephoneNumberType(phoneNumber.getType()));
            list.add(phone);
        }
        return list;
    }

    @Override
    public PhoneNumberList convertFrom(List<InstitutionTelephone> source,
            PhoneNumberList destination)
    {
        return null;
    }

}

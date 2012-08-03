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

package org.slc.sli.sif.translation;

import java.util.ArrayList;
import java.util.List;

import openadk.library.SIFDataObject;
import openadk.library.student.SchoolInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.sif.domain.converter.AddressListConverter;
import org.slc.sli.sif.domain.converter.GradeLevelsConverter;
import org.slc.sli.sif.domain.converter.PhoneNumberListConverter;
import org.slc.sli.sif.domain.converter.SchoolFocusConverter;
import org.slc.sli.sif.domain.converter.SchoolTypeConverter;
import org.slc.sli.sif.domain.slientity.Address;
import org.slc.sli.sif.domain.slientity.SchoolEntity;

@Component
public class SchoolInfoTranslationTask implements TranslationTask<SchoolEntity>
{

    @Autowired
    AddressListConverter addressListConverter;

    @Autowired
    SchoolFocusConverter schoolFocusConverter;

    @Autowired
    GradeLevelsConverter gradeLevelsConverter;

    @Autowired
    SchoolTypeConverter schoolTypeConverter;

    @Autowired
    PhoneNumberListConverter phoneNumberListConverter;

    @Override
    public List<SchoolEntity> translate(SIFDataObject sifData)
    {
        //Hey, I translate only SchoolInfo
        if (!(sifData instanceof SchoolInfo)) {
            return new ArrayList<SchoolEntity>();
        }

        SchoolInfo schoolInfo = (SchoolInfo)sifData;

        SchoolEntity result = new SchoolEntity();

        // organizationCategories is mandatory but not counterpart in SIF SchoolInfo
        List<String> organizationCategories = new ArrayList<String>();
        organizationCategories.add("School");
        result.setOrganizationCategories(organizationCategories);

        result.setStateOrganizationId(schoolInfo.getStateProvinceId());
        result.setNameOfInstitution(schoolInfo.getSchoolName());
        result.setAddress(addressListConverter.convertTo(schoolInfo.getAddressList(), new ArrayList<Address>()));
        result.setSchoolType(schoolFocusConverter.convert(schoolInfo.getSchoolFocusList()));
        result.setWebSite(schoolInfo.getSchoolURL());
        result.setGradesOffered(gradeLevelsConverter.convertTo(schoolInfo.getGradeLevels()));
        result.setSchoolCategories(schoolTypeConverter.convert(schoolInfo.getSchoolType()));
        result.setTelephone(phoneNumberListConverter.convertTo(schoolInfo.getPhoneNumberList()));

        List<SchoolEntity> list = new ArrayList<SchoolEntity>(1);
        list.add(result);
        return list;
    }

}

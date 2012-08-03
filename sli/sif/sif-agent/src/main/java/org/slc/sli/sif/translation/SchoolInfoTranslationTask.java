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

import openadk.library.student.SchoolInfo;

import org.slc.sli.sif.domain.converter.AddressListConverter;
import org.slc.sli.sif.domain.converter.GradeLevelsConverter;
import org.slc.sli.sif.domain.converter.PhoneNumberListConverter;
import org.slc.sli.sif.domain.converter.SchoolFocusConverter;
import org.slc.sli.sif.domain.converter.SchoolTypeConverter;
import org.slc.sli.sif.domain.slientity.Address;
import org.slc.sli.sif.domain.slientity.SchoolEntity;
import org.slc.sli.sif.domain.slientity.SliEntity;
import org.springframework.beans.factory.annotation.Autowired;

public class SchoolInfoTranslationTask<A extends SchoolInfo, B extends SchoolEntity> 
                        extends AbstractTranslationTask<SchoolInfo>
{

//    @Autowired
//    AddressListConverter addressListConverter;
//
//    @Autowired
//    SchoolFocusConverter schoolFocusConverter;

    @Autowired
    
    GradeLevelsConverter gradeLevelsConverter;

    @Autowired
    SchoolTypeConverter schoolTypeConverter;

    @Autowired
    PhoneNumberListConverter phoneNumberListConverter;

    public SchoolInfoTranslationTask()
{
        super(SchoolInfo.class);
    }

    @Override
    public List<SliEntity> doTranslate(SchoolInfo sifData)
    {
        SchoolInfo schoolInfo = (SchoolInfo)sifData;
        SchoolEntity result = new SchoolEntity();

        // organizationCategories is mandatory but not counterpart in SIF SchoolInfo
        List<String> organizationCategories = new ArrayList<String>();
        organizationCategories.add("School");
        result.setOrganizationCategories(organizationCategories);

        result.setWebSite(schoolInfo.getSchoolURL());
        result.setGradesOffered(gradeLevelsConverter.convertTo(schoolInfo.getGradeLevels()));
        result.setSchoolCategories(schoolTypeConverter.convert(schoolInfo.getSchoolType()));
        result.setTelephone(phoneNumberListConverter.convertTo(schoolInfo.getPhoneNumberList()));

        List<SliEntity> list = new ArrayList<SliEntity>(1);
        list.add(result);
        return list;
    }

}

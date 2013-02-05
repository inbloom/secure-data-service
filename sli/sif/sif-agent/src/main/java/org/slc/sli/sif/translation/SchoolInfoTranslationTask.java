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

package org.slc.sli.sif.translation;

import java.util.ArrayList;
import java.util.List;

import openadk.library.student.OperationalStatus;
import openadk.library.student.SchoolInfo;
import openadk.library.student.SchoolLevelType;
import openadk.library.student.Title1Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.sif.domain.converter.AddressListConverter;
import org.slc.sli.sif.domain.converter.GradeLevelsConverter;
import org.slc.sli.sif.domain.converter.OperationalStatusConverter;
import org.slc.sli.sif.domain.converter.PhoneNumberListConverter;
import org.slc.sli.sif.domain.converter.SchoolFocusConverter;
import org.slc.sli.sif.domain.converter.SchoolLevelTypeConverter;
import org.slc.sli.sif.domain.converter.TitleIPartASchoolDesignationConverter;
import org.slc.sli.sif.domain.slientity.SchoolEntity;
import org.slc.sli.sif.domain.slientity.TitleIPartASchoolDesignation;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * Translates a SIF schoolInfo into a SLI school entity
 */
@Component
public class SchoolInfoTranslationTask extends AbstractTranslationTask<SchoolInfo, SchoolEntity> {

    @Autowired
    SifIdResolver sifIdResolver;

    @Autowired
    AddressListConverter addressListConverter;

    @Autowired
    SchoolFocusConverter schoolFocusConverter;

    @Autowired
    GradeLevelsConverter gradeLevelsConverter;

    @Autowired
    OperationalStatusConverter operationalStatusConverter;

    @Autowired
    SchoolLevelTypeConverter schoolTypeConverter;

    @Autowired
    TitleIPartASchoolDesignationConverter titleIPartASchoolDesignationConverter;

    @Autowired
    PhoneNumberListConverter phoneNumberListConverter;

    public SchoolInfoTranslationTask() {
        super(SchoolInfo.class);
    }

    @Override
    public List<SchoolEntity> doTranslate(SchoolInfo sifData, String zoneId) {
        SchoolInfo schoolInfo = sifData;
        SchoolEntity result = new SchoolEntity();

        // organizationCategories is mandatory but not counterpart in SIF SchoolInfo
        List<String> organizationCategories = new ArrayList<String>();
        organizationCategories.add("School");
        result.setOrganizationCategories(organizationCategories);

        result.setStateOrganizationId(schoolInfo.getStateProvinceId());
        result.setNameOfInstitution(schoolInfo.getSchoolName());
        result.setOperationalStatus(operationalStatusConverter.convert(OperationalStatus.wrap(schoolInfo
                .getOperationalStatus())));
        result.setAddress(addressListConverter.convert(schoolInfo.getAddressList()));
        result.setSchoolType(schoolFocusConverter.convert(schoolInfo.getSchoolFocusList()));
        result.setWebSite(schoolInfo.getSchoolURL());
        result.setGradesOffered(gradeLevelsConverter.convert(schoolInfo.getGradeLevels()));
        result.setSchoolCategories(schoolTypeConverter.convertAsList(SchoolLevelType.wrap(schoolInfo.getSchoolType())));
        result.setTelephone(phoneNumberListConverter.convertInstitutionTelephone(schoolInfo.getPhoneNumberList()));

        TitleIPartASchoolDesignation schoolType = titleIPartASchoolDesignationConverter.convert(Title1Status
                .wrap(schoolInfo.getTitle1Status()));
        if (schoolType != null) {
            result.setSchoolType(schoolType.getText());
        }

        String leaGuid = sifIdResolver.getSliGuid(schoolInfo.getLEAInfoRefId(), zoneId);
        if (leaGuid != null) {
            result.setParentEducationAgencyReference(leaGuid);
        }

        List<SchoolEntity> list = new ArrayList<SchoolEntity>(1);
        list.add(result);
        return list;
    }
}

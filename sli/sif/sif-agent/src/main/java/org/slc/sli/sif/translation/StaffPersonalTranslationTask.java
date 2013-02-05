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

import java.util.Arrays;
import java.util.List;

import openadk.library.common.Demographics;
import openadk.library.student.StaffPersonal;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.sif.domain.converter.AddressListConverter;
import org.slc.sli.sif.domain.converter.DemographicsToBirthDataConverter;
import org.slc.sli.sif.domain.converter.EmailListConverter;
import org.slc.sli.sif.domain.converter.GenderConverter;
import org.slc.sli.sif.domain.converter.HrOtherIdListConverter;
import org.slc.sli.sif.domain.converter.NameConverter;
import org.slc.sli.sif.domain.converter.OtherNamesConverter;
import org.slc.sli.sif.domain.converter.PhoneNumberListConverter;
import org.slc.sli.sif.domain.converter.RaceListConverter;
import org.slc.sli.sif.domain.converter.YesNoUnknownConverter;
import org.slc.sli.sif.domain.slientity.StaffEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * Translation task for translating StaffPersonal SIF data objects to staff SLI entities.
 *
 * @author slee
 *
 */
public class StaffPersonalTranslationTask extends AbstractTranslationTask<StaffPersonal, StaffEntity> {

    @Autowired
    SifIdResolver sifIdResolver;

    @Autowired
    YesNoUnknownConverter yesNoUnknownConverter;

    @Autowired
    GenderConverter genderConverter;

    @Autowired
    RaceListConverter raceListConverter;

    @Autowired
    DemographicsToBirthDataConverter birthDataConverter;

    @Autowired
    NameConverter nameConverter;

    @Autowired
    OtherNamesConverter otherNameConverter;

    @Autowired
    HrOtherIdListConverter hrOtherIdListConverter;

    @Autowired
    PhoneNumberListConverter phoneNumberListConverter;

    @Autowired
    EmailListConverter emailListConverter;

    @Autowired
    AddressListConverter addressListConverter;

    public StaffPersonalTranslationTask() {
        super(StaffPersonal.class);
    }

    @Override
    public List<StaffEntity> doTranslate(StaffPersonal sifData, String zoneId) {
        StaffPersonal sp = sifData;
        Demographics demographics = sp.getDemographics();
        StaffEntity e = new StaffEntity();
        //convert properties
        e.setStaffUniqueStateId(sp.getStateProvinceId());
        e.setName(nameConverter.convert(sp.getName()));
        e.setOtherName(otherNameConverter.convert(sp.getOtherNames()));
        e.setStaffIdentificationCode(hrOtherIdListConverter.convert(sp.getOtherIdList()));

        if (demographics != null) {
            e.setBirthData(birthDataConverter.convert(demographics));
            e.setRace(raceListConverter.convert(demographics.getRaceList()));
            e.setSex(genderConverter.convert(demographics.getGender()));
            Boolean hispanicLatinoEthnicity = yesNoUnknownConverter.convert(demographics.getHispanicLatino());
            if (hispanicLatinoEthnicity != null) {
                e.setHispanicLatinoEthnicity(hispanicLatinoEthnicity);
            }
        }

        e.setElectronicMail(emailListConverter.convert(sp.getEmailList()));
        e.setAddress(addressListConverter.convert(sp.getAddressList()));
        e.setTelephone(phoneNumberListConverter.convertPersonalTelephone(sp.getPhoneNumberList()));

        String staffGuid = sifIdResolver.getSliGuid(sp.getEmployeePersonalRefId(), zoneId);
        if (staffGuid != null) {
            e.setCreatorRefId(sp.getEmployeePersonalRefId());
        }

        return Arrays.asList(e);
    }

}


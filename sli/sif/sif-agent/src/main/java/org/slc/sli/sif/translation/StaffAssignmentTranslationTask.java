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

import openadk.library.student.StaffAssignment;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.sif.domain.slientity.SliEntity;
import org.slc.sli.sif.domain.slientity.StaffEducationOrganizationAssociationEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * Translation task for translating StaffAssignment SIF data objects to staffEducationOrganizationAssociation SLI entities.
 *
 * @author slee
 *
 */
public class StaffAssignmentTranslationTask extends AbstractTranslationTask<StaffAssignment, SliEntity> {

    @Autowired
    SifIdResolver sifIdResolver;

    public StaffAssignmentTranslationTask() {
        super(StaffAssignment.class);
    }

    @Override
    public List<SliEntity> doTranslate(StaffAssignment sifData, String zoneId) {
        StaffAssignment sa = sifData;
        StaffEducationOrganizationAssociationEntity e = new StaffEducationOrganizationAssociationEntity();
        //convert properties
        sa.getEmployeePersonalRefId();
        sa.getGradeClassification();
        sa.getGradeLevels();
        sa.getInstructionalLevel();
        sa.getJobEndDate();
        sa.getJobStartDate();
        sa.getJobFunction();
        sa.getSchoolInfoRefId();
        sa.getStaffPersonalRefId();
        sa.getTeachingAssignment();


//        e.setStaffUniqueStateId(sa.getStateProvinceId());
//        e.setName(nameConverter.convert(sa.getName()));
//        e.setOtherName(otherNameConverter.convert(sa.getOtherNames()));
//        e.setStaffIdentificationCode(hrOtherIdListConverter.convert(sa.getOtherIdList()));
//
//
//        e.setElectronicMail(emailListConverter.convert(sa.getEmailList()));
//        e.setAddress(addressListConverter.convert(sa.getAddressList()));
//        e.setTelephone(phoneNumberListConverter.convertPersonalTelephone(sa.getPhoneNumberList()));
//
//        String staffGuid = sifIdResolver.getSliGuid(sa.getEmployeePersonalRefId(), zoneId);
//        if (staffGuid != null) {
//            e.setCreatorRefId(sa.getEmployeePersonalRefId());
//        }

        List<SliEntity> list = new ArrayList<SliEntity>(1);
        list.add(e);
        return list;
    }

}



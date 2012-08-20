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

import openadk.library.hrfin.EmployeeAssignment;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.sif.domain.slientity.SliEntity;
import org.slc.sli.sif.domain.slientity.StaffEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * Translation task for translating EmployeeAssignment SIF data objects to
 * StaffEducationOrganizationAssociation SLI entities.
 *
 * @author slee
 *
 */
public class EmployeeAssignmentTranslationTask extends AbstractTranslationTask<EmployeeAssignment, SliEntity> {

    @Autowired
    SifIdResolver sifIdResolver;


    public EmployeeAssignmentTranslationTask() {
        super(EmployeeAssignment.class);
    }

    @Override
    public List<SliEntity> doTranslate(EmployeeAssignment sifData, String zoneId) {
        EmployeeAssignment ea = sifData;
        StaffEntity se = new StaffEntity();
        // convert properties
        // We need to check if a staff record exists by using ea.getEmployeePersonalRefId()
        // The EmployeePersonal must arrive first and a staff record be created
        // Otherwise, EmployeeAssignment cannot be handled
        String staffGuid = sifIdResolver.getSliGuid(ea.getEmployeePersonalRefId(), zoneId);
        if (staffGuid != null) {
            se.setCreatorRefId(ea.getEmployeePersonalRefId());
        }




        ea.getJobClassification();
        ea.getJobEndDate();
        ea.getJobStartDate();
        ea.getProgramType();


//        e.setStaffUniqueStateId(ea.getStateProvinceId());
//        e.setName(nameConverter.convert(ea.getName()));
//        e.setOtherName(otherNameConverter.convert(ea.getOtherNames()));
//        e.setStaffIdentificationCode(hrOtherIdListConverter.convert(ea.getOtherIdList()));
//
//
//        e.setElectronicMail(emailListConverter.convert(ea.getEmailList()));
//        e.setAddress(addressListConverter.convert(ea.getAddressList()));
//        e.setTelephone(phoneNumberListConverter.convertPersonalTelephone(ea.getPhoneNumberList()));
//
//        String staffGuid = sifIdResolver.getSliGuid(ea.getEmployeePersonalRefId(), zoneId);
//        if (staffGuid != null) {
//            e.setCreatorRefId(ea.getEmployeePersonalRefId());
//        }

        List<SliEntity> list = new ArrayList<SliEntity>(1);
        list.add(se);
        return list;
    }

}



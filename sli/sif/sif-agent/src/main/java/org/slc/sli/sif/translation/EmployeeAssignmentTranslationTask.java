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

import org.slc.sli.sif.domain.converter.DateConverter;
import org.slc.sli.sif.domain.converter.JobClassificationConverter;
import org.slc.sli.sif.domain.slientity.SliEntity;
import org.slc.sli.sif.domain.slientity.StaffEducationOrganizationAssociationEntity;
import org.slc.sli.sif.domain.slientity.TeacherEntity;
import org.slc.sli.sif.domain.slientity.TeacherSchoolAssociationEntity;
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

    @Autowired
    JobClassificationConverter jobClassificationConverter;

    @Autowired
    DateConverter dateConverter;

    public EmployeeAssignmentTranslationTask() {
        super(EmployeeAssignment.class);
    }

    @Override
    public List<SliEntity> doTranslate(EmployeeAssignment sifData, String zoneId) {
        EmployeeAssignment ea = sifData;
        // convert properties
        // We need to check if a staff record exists by using ea.getEmployeePersonalRefId()
        // Normally, an EmployeePersonal should arrive first and a staff record is already created
        // Otherwise, EmployeeAssignment cannot be handled without entity life cycle support
        String staffGuid = sifIdResolver.getSliGuid(ea.getEmployeePersonalRefId(), zoneId);

        // Now we check if a JobClassification is set in the EmployeeAssignment
        // If yes, then a StaffEducationOrganizationAssociationEntity should be created
        // to catch the JobClassification
        StaffEducationOrganizationAssociationEntity seoae = new StaffEducationOrganizationAssociationEntity();
        if (ea.getJobClassification() != null) {

            if (staffGuid != null) {
                seoae.setStaffReference(staffGuid);
                // there is no school info attached in EmployeeAssignment
                // but educationOrganizationReference in StaffEducationOrganizationAssociationEntity must be set
                // Let's set it to the SEA corresponding to the zone
                // It is expected that educationOrganizationReference will be set correctly
                // by StaffAssignment that will be received later
                seoae.setEducationOrganizationReference(sifIdResolver.getZoneSea(zoneId));
                // We need to allow the to be created StaffEducationOrganizationAssociationEntity
                // serachable using ea.getEmployeePersonalRefId()
                // so that it can be correlated by a corresponding StaffAssignment later
                seoae.setZoneId(zoneId);
                seoae.setOtherSifRefId(ea.getEmployeePersonalRefId());
            }
            if (ea.getJobStartDate() != null) {
                seoae.setBeginDate(dateConverter.convert(ea.getJobStartDate()));

            }
            if (ea.getJobEndDate() != null) {
                seoae.setEndDate(dateConverter.convert(ea.getJobEndDate()));

            }
            seoae.setStaffClassification(jobClassificationConverter.convert(ea.getJobClassification()));

        }

        // Now we check if the JobClassification is "Teacher" and
        // if  a HrProgramType is set in the EmployeeAssignment
        // If yes, then a TeacherSchoolAssociationEntity should be created
        // to catch the HrProgramType
        TeacherEntity te = new TeacherEntity();
        TeacherSchoolAssociationEntity tsae = new TeacherSchoolAssociationEntity();
        if (seoae.getStaffClassification() != null
                && seoae.getStaffClassification().equals("Teacher")
                && ea.getProgramType() != null) {

            if (staffGuid != null) {
                // A staff entity is previously created
                // Now we knoe this staff is a 'Teacher' from the StaffClassification
                // So we need to create TeacherEntity to catch it
                // By setting TeacherEntity's setCreatorRefId
                // The previous StaffEntity will be merged into the new TeacherEntity
                te.setCreatorRefId(ea.getEmployeePersonalRefId());
                // now we need a way to set mandatory TeacherId for TeacherSchoolAssociationEntity
                // and allow the created TeacherSchoolAssociationEntity
                // serachable using ea.getEmployeePersonalRefId()
                // so that it can be correlated by a corresponding StaffAssignment later
                tsae.setZoneId(zoneId);
                tsae.setOtherSifRefId(ea.getEmployeePersonalRefId());
                // again, since there is no school info attached in EmployeeAssignment
                // but schoolId in TeacherSchoolAssociationEntity must be set
                // Let's set it to the SEA corresponding to the zone
                // It is expected that schoolId will be set correctly
                // by StaffAssignment that will be received later
                tsae.setSchoolId(sifIdResolver.getZoneSea(zoneId));
            }
            //convert ea.getProgramType()

        }

        List<SliEntity> list = new ArrayList<SliEntity>(3);
        list.add(seoae);
        list.add(te);
        list.add(tsae);
        return list;
    }

}



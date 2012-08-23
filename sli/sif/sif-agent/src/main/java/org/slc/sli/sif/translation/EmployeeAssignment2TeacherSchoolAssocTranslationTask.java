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
import java.util.Arrays;
import java.util.List;

import openadk.library.hrfin.EmployeeAssignment;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.sif.domain.converter.DateConverter;
import org.slc.sli.sif.domain.converter.HRProgramTypeConverter;
import org.slc.sli.sif.domain.converter.JobClassificationConverter;
import org.slc.sli.sif.domain.slientity.TeacherSchoolAssociationEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * Translation task for translating EmployeeAssignment SIF data objects to
 * TeacherSchoolAssociationEntity SLI entities.
 *
 * @author slee
 *
 */
public class EmployeeAssignment2TeacherSchoolAssocTranslationTask extends AbstractTranslationTask<EmployeeAssignment, TeacherSchoolAssociationEntity> {

    @Autowired
    SifIdResolver sifIdResolver;

    @Autowired
    JobClassificationConverter jobClassificationConverter;

    @Autowired
    HRProgramTypeConverter hrProgramTypeConverter;

    @Autowired
    DateConverter dateConverter;

    public EmployeeAssignment2TeacherSchoolAssocTranslationTask() {
        super(EmployeeAssignment.class);
    }

    @Override
    public List<TeacherSchoolAssociationEntity> doTranslate(EmployeeAssignment sifData, String zoneId) {
        EmployeeAssignment ea = sifData;
        // convert properties
        TeacherSchoolAssociationEntity tsae = new TeacherSchoolAssociationEntity();
        tsae.setProgramAssignment(hrProgramTypeConverter.convert(ea.getProgramType()));

        // We need to check if a staff record exists by using ea.getEmployeePersonalRefId()
        // Normally, an EmployeePersonal should arrive first and a staff record is already created
        // Otherwise, EmployeeAssignment cannot be handled without entity life cycle support
        String staffGuid = sifIdResolver.getSliGuid(ea.getEmployeePersonalRefId(), zoneId);

        if (staffGuid != null) {
            // A staff entity is previously created
            // we need a way to set mandatory TeacherId for TeacherSchoolAssociationEntity
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

        // If a staff entity is not previously created
        // and there is no ProgramAssignment
        // and jobClassification cannot be converted to "Teacher"
        // then there is no need to translate the EmployeeAssignment to a TeacherSchoolAssociationEntity
        String staffClassificationType = jobClassificationConverter.convert(ea.getJobClassification());
        if (staffGuid == null && tsae.getProgramAssignment() == null
                && (staffClassificationType == null || !staffClassificationType.equals("Teacher"))) {
            return new ArrayList<TeacherSchoolAssociationEntity>();
        }

        return Arrays.asList(tsae);
    }

}


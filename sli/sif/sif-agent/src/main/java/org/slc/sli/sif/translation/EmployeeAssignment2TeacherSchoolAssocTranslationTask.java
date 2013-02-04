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

import openadk.library.hrfin.EmployeeAssignment;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.api.client.Entity;
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
public class EmployeeAssignment2TeacherSchoolAssocTranslationTask extends
        AbstractTranslationTask<EmployeeAssignment, TeacherSchoolAssociationEntity> {

    @Autowired
    SifIdResolver sifIdResolver;

    @Autowired
    JobClassificationConverter jobClassificationConverter;

    @Autowired
    HRProgramTypeConverter hrProgramTypeConverter;

    public EmployeeAssignment2TeacherSchoolAssocTranslationTask() {
        super(EmployeeAssignment.class);
    }

    private static final String TEACHER_TYPE = "teacher";

    @Override
    public List<TeacherSchoolAssociationEntity> doTranslate(EmployeeAssignment sifData, String zoneId) {
        List<TeacherSchoolAssociationEntity> result = new ArrayList<TeacherSchoolAssociationEntity>();

        if (sifData == null) {
            return result;
        }

        Entity staff = sifIdResolver.getSliEntity(sifData.getEmployeePersonalRefId(), zoneId);

        if (staff == null) {
            return result;
        }

        String staffClassificationType = jobClassificationConverter.convert(sifData.getJobClassification());

        //Only create a TeacherSchoolAssociation for employeeAssignments that have a staffClassificationType
        //of Teacher and reference a teacher entity.

        //TODO determine whether we should promote an SLI staff to a student if the receive an employeeAssignment
        //specifying a teacher staffClassification but referencing a staff.
        if (staffClassificationType == null || !staffClassificationType.equals("Teacher")
                || !TEACHER_TYPE.equals(staff.getEntityType())) {
            // not handled by this translator
            return result;
        }

        TeacherSchoolAssociationEntity tsae = new TeacherSchoolAssociationEntity();

        tsae.setTeacherId(staff.getId());

        tsae.setProgramAssignment(hrProgramTypeConverter.convert(sifData.getProgramType()));

        tsae.setZoneId(zoneId);
        tsae.setOtherSifRefId(sifData.getEmployeePersonalRefId());

        result.add(tsae);

        //TODO default to be removed once partial entities are supported
        tsae.setSchoolId(sifIdResolver.getZoneSea(zoneId));

        return result;
    }
}

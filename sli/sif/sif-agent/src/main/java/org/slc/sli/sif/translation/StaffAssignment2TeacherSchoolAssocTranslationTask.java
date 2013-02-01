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
import java.util.Arrays;
import java.util.List;

import openadk.library.student.StaffAssignment;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.sif.domain.converter.GradeLevelsConverter;
import org.slc.sli.sif.domain.converter.TeachingAssignmentConverter;
import org.slc.sli.sif.domain.slientity.TeacherSchoolAssociationEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * Translation task for translating StaffAssignment SIF data objects to staffEducationOrganizationAssociation SLI entities.
 *
 * @author slee
 *
 */
public class StaffAssignment2TeacherSchoolAssocTranslationTask extends AbstractTranslationTask<StaffAssignment, TeacherSchoolAssociationEntity> {

    @Autowired
    SifIdResolver sifIdResolver;

    @Autowired
    GradeLevelsConverter gradeLevelsConverter;

    @Autowired
    TeachingAssignmentConverter teachingAssignmentConverter;

    public StaffAssignment2TeacherSchoolAssocTranslationTask() {
        super(StaffAssignment.class);
    }

    @Override
    public List<TeacherSchoolAssociationEntity> doTranslate(StaffAssignment sifData, String zoneId) {
        StaffAssignment sa = sifData;
        // convert properties
        // Check if TeacherSchoolAssociationEntity should be added or updated
        // by looking at the propertes belonging to TeacherSchoolAssociation
        if (sa.getTeachingAssignment() != null || sa.getGradeLevels() != null) {
            TeacherSchoolAssociationEntity e = new TeacherSchoolAssociationEntity();
            // We need to check if a staff record exists by using sa.getStaffPersonalRefId()
            // Normally, an StaffPersonal should arrive first and a staff record is already created
            // Otherwise, StaffAssignment cannot be handled without entity life cycle support
            String staffGuid = sifIdResolver.getSliGuid(sa.getStaffPersonalRefId(), zoneId);
            String schoolGuid = sifIdResolver.getSliGuid(sa.getSchoolInfoRefId(), zoneId);

            if (staffGuid != null) {
                e.setTeacherId(staffGuid);
            }
            if (schoolGuid != null) {
                e.setSchoolId(schoolGuid);
            }
            if (sa.getTeachingAssignment() != null) {
                e.setAcademicSubjects(teachingAssignmentConverter.convert(sa.getTeachingAssignment()));
            }
            if (sa.getGradeLevels() != null) {
                e.setInstructionalGradeLevels(gradeLevelsConverter.convert(sa.getGradeLevels()));
            }

            //set the other Sif Ids for matching
            e.setZoneId(zoneId);
            e.setOtherSifRefId(sa.getStaffPersonalRefId());

            //TODO default to be removed once partial entities are supported
            e.setProgramAssignment("Regular Education");

            return Arrays.asList(e);

        } else {
            return new ArrayList<TeacherSchoolAssociationEntity>();
        }
    }

}


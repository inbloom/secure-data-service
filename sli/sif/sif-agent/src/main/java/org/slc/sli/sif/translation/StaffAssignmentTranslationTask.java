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

import org.slc.sli.api.client.Entity;
import org.slc.sli.sif.domain.converter.DateConverter;
import org.slc.sli.sif.domain.converter.GradeLevelsConverter;
import org.slc.sli.sif.domain.converter.TeachingAssignmentConverter;
import org.slc.sli.sif.domain.slientity.SliEntity;
import org.slc.sli.sif.domain.slientity.StaffEducationOrganizationAssociationEntity;
import org.slc.sli.sif.domain.slientity.TeacherSchoolAssociationEntity;
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

    @Autowired
    DateConverter dateConverter;

    @Autowired
    GradeLevelsConverter gradeLevelsConverter;

    @Autowired
    TeachingAssignmentConverter teachingAssignmentConverter;

    public StaffAssignmentTranslationTask() {
        super(StaffAssignment.class);
    }

    @Override
    public List<SliEntity> doTranslate(StaffAssignment sifData, String zoneId) {
        StaffAssignment sa = sifData;
        // convert properties
        // We need to check if a staff record exists by using sa.getStaffPersonalRefId()
        // Normally, an StaffPersonal should arrive first and a staff record is already created
        // Otherwise, StaffAssignment cannot be handled without entity life cycle support
        String staffGuid = sifIdResolver.getSliGuid(sa.getStaffPersonalRefId(), zoneId);
        String schoolGuid = sifIdResolver.getSliGuid(sa.getSchoolInfoRefId(), zoneId);
        StaffEducationOrganizationAssociationEntity seoae = new StaffEducationOrganizationAssociationEntity();
        if (staffGuid != null) {
            seoae.setStaffReference(staffGuid);
        }
        if (schoolGuid != null) {
            seoae.setEducationOrganizationReference(schoolGuid);
        }
        if (sa.getJobStartDate() != null) {
            seoae.setBeginDate(dateConverter.convert(sa.getJobStartDate()));

        }
        if (sa.getJobEndDate() != null) {
            seoae.setEndDate(dateConverter.convert(sa.getJobEndDate()));
        }
        // If there is a previous EmployeeAssignment of the same EmployeePersonalRefId
        // We need to check if a Matched Entity can be found
        if (sa.getEmployeePersonalRefId() != null && sa.getEmployeePersonalRefId().length() > 0) {
            Entity staffEdOrgAssocEntity = sifIdResolver.getSliEntityByType(sa.getEmployeePersonalRefId(), seoae.entityType(), zoneId);
            if (staffEdOrgAssocEntity != null) {
                seoae.setMatchedEntity(staffEdOrgAssocEntity);
            }
        }
        // If there is no Matched Entity, we need to set a default for the mandatory StaffClassification
        if (seoae.getMatchedEntity() == null) {
            seoae.setStaffClassification("Other");
        }

        // Now we check if TeacherSchoolAssociationEntity should be added or updated
        TeacherSchoolAssociationEntity tsae = new TeacherSchoolAssociationEntity();
        if (sa.getTeachingAssignment() != null || sa.getGradeLevels() != null) {

            if (staffGuid != null) {
                tsae.setTeacherId(staffGuid);
            }
            if (schoolGuid != null) {
                tsae.setSchoolId(schoolGuid);
            }
            if (sa.getTeachingAssignment() != null) {
                tsae.setAcademicSubjects(teachingAssignmentConverter.convert(sa.getTeachingAssignment()));
            }
            if (sa.getGradeLevels() != null) {
                tsae.setInstructionalGradeLevels(gradeLevelsConverter.convert(sa.getGradeLevels()));
            }
            // If there is a previous EmployeeAssignment of the same EmployeePersonalRefId
            // We need to check if a Matched Entity can be found
            if (sa.getEmployeePersonalRefId() != null && sa.getEmployeePersonalRefId().length() > 0) {
                Entity teacherSchoolAssocEntity = sifIdResolver.getSliEntityByType(sa.getEmployeePersonalRefId(), tsae.entityType(), zoneId);
                if (teacherSchoolAssocEntity != null) {
                    tsae.setMatchedEntity(teacherSchoolAssocEntity);
                }
            }
            // If there is no Matched Entity, we need to set a default for the mandatory StaffClassification
            if (tsae.getMatchedEntity() == null) {
                tsae.setProgramAssignment("Regular Education");
            }

        }

        List<SliEntity> list = new ArrayList<SliEntity>(2);
        list.add(seoae);
        list.add(tsae);
        return list;
    }

}



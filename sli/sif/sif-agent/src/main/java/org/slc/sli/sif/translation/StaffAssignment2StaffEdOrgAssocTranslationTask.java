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

import java.util.Arrays;
import java.util.List;

import openadk.library.student.StaffAssignment;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.api.client.Entity;
import org.slc.sli.sif.domain.converter.DateConverter;
import org.slc.sli.sif.domain.slientity.StaffEducationOrganizationAssociationEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * Translation task for translating StaffAssignment SIF data objects to
 * staffEducationOrganizationAssociation SLI entities.
 *
 * @author slee
 *
 */
public class StaffAssignment2StaffEdOrgAssocTranslationTask extends AbstractTranslationTask<StaffAssignment, StaffEducationOrganizationAssociationEntity> {

    @Autowired
    SifIdResolver sifIdResolver;

    @Autowired
    DateConverter dateConverter;

    public StaffAssignment2StaffEdOrgAssocTranslationTask() {
        super(StaffAssignment.class);
    }

    @Override
    public List<StaffEducationOrganizationAssociationEntity> doTranslate(StaffAssignment sifData, String zoneId) {
        StaffAssignment sa = sifData;
        // convert properties
        // We need to check if a staff record exists by using sa.getStaffPersonalRefId()
        // Normally, an StaffPersonal should arrive first and a staff record is already created
        // Otherwise, StaffAssignment cannot be handled without entity life cycle support
        String staffGuid = sifIdResolver.getSliGuid(sa.getStaffPersonalRefId(), zoneId);
        String schoolGuid = sifIdResolver.getSliGuid(sa.getSchoolInfoRefId(), zoneId);
        StaffEducationOrganizationAssociationEntity e = new StaffEducationOrganizationAssociationEntity();
        if (staffGuid != null) {
            e.setStaffReference(staffGuid);
        }
        if (schoolGuid != null) {
            e.setEducationOrganizationReference(schoolGuid);
        }
        if (sa.getJobStartDate() != null) {
            e.setBeginDate(dateConverter.convert(sa.getJobStartDate()));

        }
        if (sa.getJobEndDate() != null) {
            e.setEndDate(dateConverter.convert(sa.getJobEndDate()));
        }
        // If there is a previous EmployeeAssignment of the same EmployeePersonalRefId
        // We need to check if a Matched Entity can be found
        if (sa.getEmployeePersonalRefId() != null && sa.getEmployeePersonalRefId().length() > 0) {
            Entity staffEdOrgAssocEntity = sifIdResolver.getSliEntityByType(sa.getEmployeePersonalRefId(), e.entityType(), zoneId);
            if (staffEdOrgAssocEntity != null) {
                e.setMatchedEntity(staffEdOrgAssocEntity);
            }
        }
        // If there is no Matched Entity, we need to set a default for the mandatory StaffClassification
        if (e.getMatchedEntity() == null) {
            e.setStaffClassification("Other");
        }

        return Arrays.asList(e);
    }

}


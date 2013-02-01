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

import openadk.library.hrfin.EmployeeAssignment;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.sif.domain.converter.DateConverter;
import org.slc.sli.sif.domain.converter.JobClassificationConverter;
import org.slc.sli.sif.domain.slientity.StaffEducationOrganizationAssociationEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * Translation task for translating EmployeeAssignment SIF data objects to
 * StaffEducationOrganizationAssociation SLI entities.
 *
 * @author slee
 *
 */
public class EmployeeAssignment2StaffEdOrgAssocTranslationTask extends AbstractTranslationTask<EmployeeAssignment, StaffEducationOrganizationAssociationEntity> {

    @Autowired
    SifIdResolver sifIdResolver;

    @Autowired
    JobClassificationConverter jobClassificationConverter;

    @Autowired
    DateConverter dateConverter;

    public EmployeeAssignment2StaffEdOrgAssocTranslationTask() {
        super(EmployeeAssignment.class);
    }

    @Override
    public List<StaffEducationOrganizationAssociationEntity> doTranslate(EmployeeAssignment sifData, String zoneId) {
        EmployeeAssignment ea = sifData;
        // convert properties
        StaffEducationOrganizationAssociationEntity seoae = new StaffEducationOrganizationAssociationEntity();
        seoae.setStaffClassification(jobClassificationConverter.convert(ea.getJobClassification()));
        if (ea.getJobStartDate() != null) {
            seoae.setBeginDate(dateConverter.convert(ea.getJobStartDate()));
        }
        if (ea.getJobEndDate() != null) {
            seoae.setEndDate(dateConverter.convert(ea.getJobEndDate()));
        }
        // We need to check if a staff record exists by using ea.getEmployeePersonalRefId()
        // Normally, an EmployeePersonal should arrive first and a staff record is already created
        // Otherwise, EmployeeAssignment cannot be handled without entity life cycle support
        String staffGuid = sifIdResolver.getSliGuid(ea.getEmployeePersonalRefId(), zoneId);

        if (staffGuid != null) {
            seoae.setStaffReference(staffGuid);
            // there is no school info attached in EmployeeAssignment
            // but educationOrganizationReference in StaffEducationOrganizationAssociationEntity must be set
            // Let's set it to the SEA corresponding to the zone
            // It is expected that educationOrganizationReference will be set correctly
            // by StaffAssignment that will be received later
            seoae.setEducationOrganizationReference(sifIdResolver.getZoneSea(zoneId));
            // We need to allow the newly created StaffEducationOrganizationAssociationEntity
            // be serachable by EmployeePersonalRefId
            // so that it can be found by a later StaffAssignment
            seoae.setZoneId(zoneId);
            seoae.setOtherSifRefId(ea.getEmployeePersonalRefId());
        }

        return Arrays.asList(seoae);
    }

}



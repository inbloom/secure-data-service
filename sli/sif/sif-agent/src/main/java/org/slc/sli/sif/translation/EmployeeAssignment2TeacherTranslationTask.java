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

import openadk.library.hrfin.EmployeeAssignment;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.sif.domain.converter.JobClassificationConverter;
import org.slc.sli.sif.domain.slientity.TeacherEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * Translation task for translating EmployeeAssignment SIF data objects to
 * TeacherEntity SLI entities.
 *
 * @author slee
 *
 * TODO determine when and how a staff entity should be promoted to a teacher entity
 *
 */
public class EmployeeAssignment2TeacherTranslationTask extends AbstractTranslationTask<EmployeeAssignment, TeacherEntity> {

    @Autowired
    SifIdResolver sifIdResolver;

    @Autowired
    JobClassificationConverter jobClassificationConverter;

    public EmployeeAssignment2TeacherTranslationTask() {
        super(EmployeeAssignment.class);
    }

    @Override
    public List<TeacherEntity> doTranslate(EmployeeAssignment sifData, String zoneId) {
        EmployeeAssignment ea = sifData;
        // convert properties
        // We need to check if a staff record exists by using ea.getEmployeePersonalRefId()
        // Normally, an EmployeePersonal should arrive first and a staff record is already created
        // Otherwise, EmployeeAssignment cannot be handled without entity life cycle support
        String staffGuid = sifIdResolver.getSliGuid(ea.getEmployeePersonalRefId(), zoneId);
        String staffClassificationType = jobClassificationConverter.convert(ea.getJobClassification());
        // If a staff entity is not previously created
        // and jobClassification cannot be converted to "Teacher"
        // then there is no need to translate the EmployeeAssignment to a TeacherEntity
        if (staffGuid == null && (staffClassificationType == null || !staffClassificationType.equals("Teacher"))) {
            return new ArrayList<TeacherEntity>();
        }

        TeacherEntity te = new TeacherEntity();
        if (staffGuid != null) {
            // A staff entity is previously created
            // Now we know this staff is a 'Teacher' from the StaffClassification
            te.setCreatorRefId(ea.getEmployeePersonalRefId());
        }

        return Arrays.asList(te);
    }

}


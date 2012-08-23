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

import openadk.library.hrfin.EmploymentRecord;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.api.client.Entity;
import org.slc.sli.sif.domain.converter.DateConverter;
import org.slc.sli.sif.domain.slientity.TeacherSchoolAssociationEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * Translation task to map SIF EmploymentRecord to SLI teacherSchoolAssociation
 *
 */
public class EmploymentRecordToTeacherSchoolTranslationTask extends
        AbstractTranslationTask<EmploymentRecord, TeacherSchoolAssociationEntity> {

    @Autowired
    SifIdResolver sifIdResolver;

    @Autowired
    DateConverter dateConverter;

    private static final String TEACHER_TYPE = "teacher";
    private static final String SCHOOL_TYPE = "school";

    public EmploymentRecordToTeacherSchoolTranslationTask() {
        super(EmploymentRecord.class);
    }

    @Override
    public List<TeacherSchoolAssociationEntity> doTranslate(EmploymentRecord sifData, String zoneId) {
        List<TeacherSchoolAssociationEntity> result = new ArrayList<TeacherSchoolAssociationEntity>();

        if (sifData == null) {
            return result;
        }

        sifData.setRefId(sifData.getSIF_RefId() + ":" + sifData.getSIF_RefObject());

        Entity staff = sifIdResolver.getSliEntity(sifData.getSIF_RefId(), zoneId);
        Entity edOrg = sifIdResolver.getSliEntity(sifData.getLEAInfoRefId(), zoneId);

        if (staff == null || edOrg == null) {
            return result;
        }

        if (!TEACHER_TYPE.equals(staff.getEntityType()) || !SCHOOL_TYPE.equals(edOrg.getEntityType())) {
            // not handled by this translator
            return result;
        }

        TeacherSchoolAssociationEntity e = new TeacherSchoolAssociationEntity();

        e.setSchoolId(edOrg.getId());
        e.setTeacherId(staff.getId());

        // This is a default value, missing field in SIF
        e.setProgramAssignment("Regular Education");

        result.add(e);
        return result;
    }

}

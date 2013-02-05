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
import java.util.Calendar;
import java.util.List;

import openadk.library.common.EntryType;
import openadk.library.common.ExitType;
import openadk.library.common.GradeLevel;
import openadk.library.common.StudentLEARelationship;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.sif.domain.converter.DateConverter;
import org.slc.sli.sif.domain.converter.EntryTypeConverter;
import org.slc.sli.sif.domain.converter.ExitTypeConverter;
import org.slc.sli.sif.domain.converter.GradeLevelsConverter;
import org.slc.sli.sif.domain.converter.SchoolYearConverter;
import org.slc.sli.sif.domain.slientity.StudentSchoolAssociationEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * Translation task for translating StudentLEARelationship SIF data objects
 * to StudentSchoolAssociation SLI entities.
 */
public class StudentLeaRelationshipTranslationTask extends
        AbstractTranslationTask<StudentLEARelationship, StudentSchoolAssociationEntity> {
    @Autowired
    SifIdResolver sifIdResolver;

    @Autowired
    SchoolYearConverter schoolYearConverter;

    @Autowired
    GradeLevelsConverter gradeLevelsConverter;

    @Autowired
    EntryTypeConverter entryTypeConverter;

    @Autowired
    ExitTypeConverter exitTypeConverter;

    @Autowired
    DateConverter dateConverter;


    public StudentLeaRelationshipTranslationTask() {
        super(StudentLEARelationship.class);
    }

    @Override
    public List<StudentSchoolAssociationEntity> doTranslate(StudentLEARelationship sifData, String zoneId) {
        StudentLEARelationship slr = sifData;
        StudentSchoolAssociationEntity result = new StudentSchoolAssociationEntity();

        String sifStudentRefId = slr.getStudentPersonalRefId();
        String sliStudentGuid = sifIdResolver.getSliGuid(sifStudentRefId, zoneId);
        result.setStudentId(sliStudentGuid);
        String sifLEAInfoRefId = slr.getLEAInfoRefId();
        String sliSchoolGuid = sifIdResolver.getSliGuid(sifLEAInfoRefId, zoneId);
        result.setSchoolId(sliSchoolGuid);

        Integer schoolYear = slr.getSchoolYear();
        result.setSchoolYear(schoolYearConverter.convert(schoolYear));
        Calendar entryDate = slr.getEntryDate();
        result.setEntryDate(dateConverter.convert(entryDate));

        GradeLevel gradeLevel = slr.getGradeLevel();
        result.setEntryGradeLevel(gradeLevelsConverter.convert(gradeLevel));

        EntryType entryType = slr.getEntryType();
        result.setEntryType(entryTypeConverter.convert(entryType));

        Calendar exitDate = slr.getExitDate();
        result.setExitWithdrawDate(dateConverter.convert(exitDate));

        ExitType exitType = slr.getExitType();
        result.setExitWithdrawType(exitTypeConverter.convert(exitType));

        return Arrays.asList(result);
    }

}

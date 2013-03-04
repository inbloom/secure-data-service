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


package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.AssessmentCategoryType;
import org.slc.sli.test.edfi.entities.AssessmentFamily;
import org.slc.sli.test.edfi.entities.AssessmentFamilyIdentityType;
import org.slc.sli.test.edfi.entities.AssessmentFamilyReferenceType;
import org.slc.sli.test.edfi.entities.AssessmentIdentificationCode;
import org.slc.sli.test.edfi.entities.AssessmentIdentificationSystemType;
import org.slc.sli.test.edfi.entities.ContentStandardType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.meta.AssessmentFamilyMeta;

public class AssessmentFamilyGenerator {

    public static AssessmentFamily generateLowFi(AssessmentFamilyMeta assessFamilyMeta) {
        AssessmentFamily assessFamily = new AssessmentFamily();
        assessFamily.setAssessmentFamilyTitle(assessFamilyMeta.id + "Title");

        AssessmentIdentificationCode idCode = new AssessmentIdentificationCode();
        idCode.setID(assessFamilyMeta.id);
        idCode.setIdentificationSystem(AssessmentIdentificationSystemType.FEDERAL);
        idCode.setAssigningOrganizationCode("AssigningOrganizationCode");
        assessFamily.getAssessmentFamilyIdentificationCode().add(idCode);

        assessFamily.setAssessmentCategory(AssessmentCategoryType.ACHIEVEMENT_TEST);
        assessFamily.setAcademicSubject(AcademicSubjectType.ENGLISH);
        assessFamily.setGradeLevelAssessed(GradeLevelType.EIGHTH_GRADE);
        assessFamily.setLowestGradeLevelAssessed(GradeLevelType.EIGHTH_GRADE);
        assessFamily.setContentStandard(ContentStandardType.ADVANCED_PLACEMENT);
        assessFamily.setVersion(1);
        assessFamily.setRevisionDate("2011-01-01");
        assessFamily.setNomenclature("Nomenclature");

        for (String assessPeriodDescIdString : assessFamilyMeta.assessmentPeriodDescriptorIds) {
            assessFamily.getAssessmentPeriods().add(
                    AssessmentPeriodDescriptorGenerator.getAssessmentPeriodDescriptorType(assessPeriodDescIdString));
        }
        

        if (assessFamilyMeta.relatedAssessmentFamilyId != null) {
            assessFamily
                    .setAssessmentFamilyReference(getAssessmentFamilyReferenceType(assessFamilyMeta.relatedAssessmentFamilyId));
        }

        return assessFamily;
    }

    public static AssessmentFamilyReferenceType getAssessmentFamilyReferenceType(final String assessmentFamilyId) {
        AssessmentFamilyReferenceType familyRef = new AssessmentFamilyReferenceType();
        AssessmentFamilyIdentityType identity = new AssessmentFamilyIdentityType();
        identity.setAssessmentFamilyTitle(assessmentFamilyId + "Title");

        familyRef.setAssessmentFamilyIdentity(identity);
        return familyRef;
    }

    public static AssessmentFamilyReferenceType getAssessmentFamilyReferenceType(final AssessmentFamily family) {
        AssessmentFamilyReferenceType familyRef = new AssessmentFamilyReferenceType();
        AssessmentFamilyIdentityType identity = new AssessmentFamilyIdentityType();
        familyRef.setAssessmentFamilyIdentity(identity);
        identity.setAssessmentFamilyTitle(family.getAssessmentFamilyTitle());
        return familyRef;
    }
}

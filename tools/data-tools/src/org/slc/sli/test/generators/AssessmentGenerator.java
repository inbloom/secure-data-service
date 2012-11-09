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


package org.slc.sli.test.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.Assessment;
import org.slc.sli.test.edfi.entities.AssessmentCategoryType;
import org.slc.sli.test.edfi.entities.AssessmentIdentificationCode;
import org.slc.sli.test.edfi.entities.AssessmentIdentificationSystemType;
import org.slc.sli.test.edfi.entities.AssessmentIdentityType;
import org.slc.sli.test.edfi.entities.AssessmentItemIdentityType;
import org.slc.sli.test.edfi.entities.AssessmentItemReferenceType;
import org.slc.sli.test.edfi.entities.AssessmentPerformanceLevel;
import org.slc.sli.test.edfi.entities.AssessmentReferenceType;
import org.slc.sli.test.edfi.entities.AssessmentReportingMethodType;
import org.slc.sli.test.edfi.entities.ContentStandardType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.ObjectiveAssessment;
import org.slc.sli.test.edfi.entities.PerformanceLevelDescriptorType;
import org.slc.sli.test.edfi.entities.ReferenceType;
import org.slc.sli.test.edfi.entities.SectionReferenceType;
import org.slc.sli.test.edfi.entities.meta.AssessmentMeta;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

public class AssessmentGenerator {

    public static Assessment generate(AssessmentMeta assessmentMeta, Map<String, ObjectiveAssessment> objAssessMap) {
        return generate(StateEdFiXmlGenerator.fidelityOfData, assessmentMeta, objAssessMap);
    }
    
    public static Assessment generate(String fidelity, AssessmentMeta assessmentMeta, Map<String, ObjectiveAssessment> objAssessMap) {
        Assessment assessment = new Assessment();

        populateRequiredFields(fidelity, assessmentMeta, objAssessMap, assessment);

        return assessment;
    }

    private static void populateRequiredFields(String fidelity, AssessmentMeta assessmentMeta,
            Map<String, ObjectiveAssessment> objAssessMap, Assessment assessment) {
        assessment.setAssessmentTitle(assessmentMeta.id + "Title");

        AssessmentIdentificationCode aidCode = new AssessmentIdentificationCode();
        aidCode.setID(assessmentMeta.id);
        aidCode.setIdentificationSystem(AssessmentIdentificationSystemType.SCHOOL);
        assessment.getAssessmentIdentificationCode().add(aidCode);

        assessment.setAssessmentCategory(AssessmentCategoryType.ACHIEVEMENT_TEST);
        assessment.setAcademicSubject(AcademicSubjectType.AGRICULTURE_FOOD_AND_NATURAL_RESOURCES);
        assessment.setGradeLevelAssessed(GradeLevelType.ADULT_EDUCATION);
        assessment.setLowestGradeLevelAssessed(GradeLevelType.ADULT_EDUCATION);

        // AssessmentPerformanceLevel
        for (String assessmentPerformanceLevelId : assessmentMeta.performanceLevelDescriptorIds) {
            AssessmentPerformanceLevel apLevel = new AssessmentPerformanceLevel();

//            apLevel.setPerformanceLevel(PerformanceLevelDescriptorGenerator
//                    .getPerformanceLevelDescriptorType(assessmentPerformanceLevelId));
            PerformanceLevelDescriptorType pldt = new PerformanceLevelDescriptorType();
            pldt.setPerformanceLevelMet(true);
            pldt.setCodeValue(assessmentPerformanceLevelId);
            apLevel.setPerformanceLevel(pldt);

            apLevel.setAssessmentReportingMethod(AssessmentReportingMethodType.SCALE_SCORE);
            apLevel.setMinimumScore(0);
            apLevel.setMaximumScore(100);

            assessment.getAssessmentPerformanceLevel().add(apLevel);
        }

        assessment.setContentStandard(ContentStandardType.ACT);
        assessment.setAssessmentForm("ThisAssessmentForm");
        assessment.setVersion(1);
        assessment.setRevisionDate("2012-03-24");
        assessment.setMaxRawScore(100);
        assessment.setNomenclature("Nomenclature");

        // AssessmentPeriod
        if (assessmentMeta.assessmentPeriodDescriptorId != null) {
            assessment.setAssessmentPeriod(AssessmentPeriodDescriptorGenerator
                    .getAssessmentPeriodDescriptorType(assessmentMeta.assessmentPeriodDescriptorId));
        }

        // AssessmentItemReference
        List<AssessmentItemReferenceType> value = new ArrayList<AssessmentItemReferenceType>();
        for (@SuppressWarnings("unused") String assessItemIdString : assessmentMeta.assessmentItemIds) {
            AssessmentItemIdentityType identity = new AssessmentItemIdentityType();
            identity.setAssessmentItemIdentificationCode(assessItemIdString);
            AssessmentItemReferenceType refType = new AssessmentItemReferenceType();
            refType.setAssessmentItemIdentity(identity);
            value.add(refType);
        }
        if (value.size() != 0) {
            assessment.setAssessmentItemReference(value);
        }
        
        // ObjectiveAssessmentReference
        for (String objAssessmentIdString : assessmentMeta.objectiveAssessmentIds) {
            if (objAssessMap.get(objAssessmentIdString) != null) {
                ReferenceType objAssessRef = new ReferenceType();
                objAssessRef
                .setRef(objAssessMap.get(objAssessmentIdString));
                assessment.getObjectiveAssessmentReference().add(
                        objAssessRef);
            }
        }
        

        // AssessmentFamilyReference
        if (assessmentMeta.assessmentFamilyId != null) {
            assessment.setAssessmentFamilyReference(AssessmentFamilyGenerator
                    .getAssessmentFamilyReferenceType(assessmentMeta.assessmentFamilyId));
        }

        // SectionReference
        for (String sectionIdString : assessmentMeta.sectionIds) {
            // TODO: need to populate this type further in order to validate. need either
            // StateOrganizationId or an EducationOrgIdentificationCode object
            SectionReferenceType srType = new SectionReferenceType();
            srType.getSectionIdentity().setUniqueSectionCode(sectionIdString);
            assessment.getSectionReference().add(srType);
        }
    }

    public static AssessmentReferenceType getAssessmentReference(final String assessmentId) {
        AssessmentIdentificationCode aic = new AssessmentIdentificationCode();
        aic.setID(assessmentId);
        aic.setIdentificationSystem(AssessmentIdentificationSystemType.SCHOOL);

        AssessmentIdentityType ait = new AssessmentIdentityType();
        ait.setAssessmentTitle(assessmentId+"Title");
        ait.setAssessmentCategory(AssessmentCategoryType.ACHIEVEMENT_TEST);
        ait.setAcademicSubject(AcademicSubjectType.AGRICULTURE_FOOD_AND_NATURAL_RESOURCES);
        ait.setGradeLevelAssessed(GradeLevelType.ADULT_EDUCATION);
        ait.setVersion(1);

        AssessmentReferenceType art = new AssessmentReferenceType();
        art.setAssessmentIdentity(ait);
        return art;
    }

}

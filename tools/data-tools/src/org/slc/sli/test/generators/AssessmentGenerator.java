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

import java.util.Map;

import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.AssessmentIdentityType;
import org.slc.sli.test.edfi.entities.AssessmentReferenceType;
import org.slc.sli.test.edfi.entities.SLCAssessment;
import org.slc.sli.test.edfi.entities.AssessmentCategoryType;
import org.slc.sli.test.edfi.entities.AssessmentIdentificationCode;
import org.slc.sli.test.edfi.entities.AssessmentIdentificationSystemType;
import org.slc.sli.test.edfi.entities.SLCAssessmentIdentityType;
import org.slc.sli.test.edfi.entities.AssessmentItemIdentityType;
import org.slc.sli.test.edfi.entities.AssessmentItemReferenceType;
import org.slc.sli.test.edfi.entities.AssessmentPerformanceLevel;
import org.slc.sli.test.edfi.entities.SLCAssessmentReferenceType;
import org.slc.sli.test.edfi.entities.AssessmentReportingMethodType;
import org.slc.sli.test.edfi.entities.ContentStandardType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.SLCObjectiveAssessment;
import org.slc.sli.test.edfi.entities.PerformanceLevelDescriptorType;
import org.slc.sli.test.edfi.entities.SLCObjectiveAssessmentIdentityType;
import org.slc.sli.test.edfi.entities.SLCObjectiveAssessmentReferenceType;
import org.slc.sli.test.edfi.entities.SLCSectionReferenceType;
import org.slc.sli.test.edfi.entities.meta.AssessmentMeta;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

public class AssessmentGenerator {

    public static SLCAssessment generate(AssessmentMeta assessmentMeta, Map<String, SLCObjectiveAssessment> objAssessMap) {
        return generate(StateEdFiXmlGenerator.fidelityOfData, assessmentMeta, objAssessMap);
    }
    
    public static SLCAssessment generate(String fidelity, AssessmentMeta assessmentMeta, Map<String, SLCObjectiveAssessment> objAssessMap) {
        SLCAssessment assessment = new SLCAssessment();

        populateRequiredFields(fidelity, assessmentMeta, objAssessMap, assessment);

        return assessment;
    }

    private static void populateRequiredFields(String fidelity, AssessmentMeta assessmentMeta,
            Map<String, SLCObjectiveAssessment> objAssessMap, SLCAssessment assessment) {
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
        for (@SuppressWarnings("unused") String assessItemIdString : assessmentMeta.assessmentItemIds) {
            AssessmentItemIdentityType identity = new AssessmentItemIdentityType();
            identity.setAssessmentItemIdentificationCode(assessItemIdString);
            AssessmentItemReferenceType refType = new AssessmentItemReferenceType();
            refType.setAssessmentItemIdentity(identity);
            assessment.getAssessmentItemReference().add(refType);
        }
        
        // ObjectiveAssessmentReference
        for (String objAssessmentIdString : assessmentMeta.objectiveAssessmentIds) {
            if (objAssessMap.get(objAssessmentIdString) != null) {
                SLCObjectiveAssessmentReferenceType objAssessRef = new SLCObjectiveAssessmentReferenceType();
                SLCObjectiveAssessmentIdentityType objAssessId = new SLCObjectiveAssessmentIdentityType();
                objAssessId.setObjectiveAssessmentIdentificationCode(objAssessmentIdString);
                objAssessRef.setObjectiveAssessmentIdentity(objAssessId);
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
            SLCSectionReferenceType srType = new SLCSectionReferenceType();
            srType.getSectionIdentity().setUniqueSectionCode(sectionIdString);
            assessment.getSectionReference().add(srType);
        }
    }

    public static SLCAssessmentReferenceType getAssessmentReference(final String assessmentId) {
        AssessmentIdentificationCode aic = new AssessmentIdentificationCode();
        aic.setID(assessmentId);
        aic.setIdentificationSystem(AssessmentIdentificationSystemType.SCHOOL);

        SLCAssessmentIdentityType ait = new SLCAssessmentIdentityType();
        ait.setAssessmentTitle(assessmentId+"Title");
//        ait.setAssessmentCategory(AssessmentCategoryType.ACHIEVEMENT_TEST);
        ait.setAcademicSubject(AcademicSubjectType.AGRICULTURE_FOOD_AND_NATURAL_RESOURCES);
        ait.setGradeLevelAssessed(GradeLevelType.ADULT_EDUCATION);
        ait.setVersion(1);

        SLCAssessmentReferenceType art = new SLCAssessmentReferenceType();
        art.setAssessmentIdentity(ait);
        return art;
    }

    public static AssessmentReferenceType getEdFiAssessmentReference(final String assessmentId) {
        AssessmentIdentificationCode aic = new AssessmentIdentificationCode();
        aic.setID(assessmentId);
        aic.setIdentificationSystem(AssessmentIdentificationSystemType.SCHOOL);

        AssessmentIdentityType ait = new AssessmentIdentityType();
        ait.setAssessmentTitle(assessmentId+"Title");
//        ait.setAssessmentCategory(AssessmentCategoryType.ACHIEVEMENT_TEST);
        ait.setAcademicSubject(AcademicSubjectType.AGRICULTURE_FOOD_AND_NATURAL_RESOURCES);
        ait.setGradeLevelAssessed(GradeLevelType.ADULT_EDUCATION);
        ait.setVersion(1);

        AssessmentReferenceType art = new AssessmentReferenceType();
        art.setAssessmentIdentity(ait);
        return art;
    }

}

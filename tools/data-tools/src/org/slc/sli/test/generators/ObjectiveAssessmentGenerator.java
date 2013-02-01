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

import org.slc.sli.test.edfi.entities.AssessmentPerformanceLevel;
import org.slc.sli.test.edfi.entities.AssessmentReportingMethodType;
import org.slc.sli.test.edfi.entities.SLCObjectiveAssessment;
import org.slc.sli.test.edfi.entities.SLCObjectiveAssessmentIdentityType;
import org.slc.sli.test.edfi.entities.SLCObjectiveAssessmentReferenceType;
import org.slc.sli.test.edfi.entities.PerformanceLevelDescriptorType;
import org.slc.sli.test.edfi.entities.meta.ObjectiveAssessmentMeta;

public class ObjectiveAssessmentGenerator {

    public static SLCObjectiveAssessment generateLowFi(final ObjectiveAssessmentMeta objAssessMeta) {

        SLCObjectiveAssessment objectiveAssessment = new SLCObjectiveAssessment();
        objectiveAssessment.setId(objAssessMeta.id);
        objectiveAssessment.setIdentificationCode(objAssessMeta.id);
        objectiveAssessment.setMaxRawScore(100);

        for (String plDescString : objAssessMeta.performanceLevelDescriptorIds) {
            AssessmentPerformanceLevel apLevel = new AssessmentPerformanceLevel();

//            apLevel.setPerformanceLevel(PerformanceLevelDescriptorGenerator
//                    .getPerformanceLevelDescriptorType(plDescString));
            PerformanceLevelDescriptorType pldt = new PerformanceLevelDescriptorType();
            pldt.setPerformanceLevelMet(true);
            pldt.setCodeValue(plDescString);
            apLevel.setPerformanceLevel(pldt);

            apLevel.setAssessmentReportingMethod(AssessmentReportingMethodType.ADAPTIVE_SCALE_SCORE);
            apLevel.setMinimumScore(0);
            apLevel.setMaximumScore(100);

            objectiveAssessment.getAssessmentPerformanceLevel().add(apLevel);
        }
        objectiveAssessment.setPercentOfAssessment(50);
        objectiveAssessment.setNomenclature("Nomenclature");

        for (String assessItemIdString : objAssessMeta.assessmentItemIds) {
            // TODO: this is only modeled as XML ReferenceType...
        }

//        for (String learningObjectiveIdString : objAssessMeta.learningObjectiveIds) {
//            objectiveAssessment.getLearningObjectiveReference().add(
//                    LearningObjectiveGenerator.getLearningObjectiveReferenceType(learningObjectiveIdString));
//        }

        for (String learningStandardIdString : objAssessMeta.learningStandardIds) {
            objectiveAssessment.getLearningStandardReference().add(
                    LearningStandardGenerator.getLearningStandardReferenceType(learningStandardIdString));
        }

        for (String relatedObjAssessmentIdString : objAssessMeta.relatedObjectiveAssessmentIds) {
            // TODO: this is only modeled as XML ReferenceType...
        }

        return objectiveAssessment;
    }

    public static SLCObjectiveAssessmentReferenceType getObjectiveAssessmentReferenceType(final String assessmentCode) {
        SLCObjectiveAssessmentReferenceType objectiveAssessmentRef = new SLCObjectiveAssessmentReferenceType();
        SLCObjectiveAssessmentIdentityType identity = new SLCObjectiveAssessmentIdentityType();
        objectiveAssessmentRef.setObjectiveAssessmentIdentity(identity);
        identity.setObjectiveAssessmentIdentificationCode(assessmentCode);
        return objectiveAssessmentRef;
    }

    public static SLCObjectiveAssessmentReferenceType getObjectiveAssessmentReferenceType(
            final SLCObjectiveAssessment objectiveAssessment) {
        SLCObjectiveAssessmentReferenceType objectiveAssessmentRef = new SLCObjectiveAssessmentReferenceType();
        SLCObjectiveAssessmentIdentityType identity = new SLCObjectiveAssessmentIdentityType();
        objectiveAssessmentRef.setObjectiveAssessmentIdentity(identity);
        identity.setObjectiveAssessmentIdentificationCode(objectiveAssessment.getIdentificationCode());
        return objectiveAssessmentRef;
    }
}

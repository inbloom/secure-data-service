package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.AssessmentPerformanceLevel;
import org.slc.sli.test.edfi.entities.AssessmentReportingMethodType;
import org.slc.sli.test.edfi.entities.ObjectiveAssessment;
import org.slc.sli.test.edfi.entities.ObjectiveAssessmentIdentityType;
import org.slc.sli.test.edfi.entities.ObjectiveAssessmentReferenceType;
import org.slc.sli.test.edfi.entities.meta.ObjectiveAssessmentMeta;

public class ObjectiveAssessmentGenerator {

    public static ObjectiveAssessment generateLowFi(final ObjectiveAssessmentMeta objAssessMeta) {

        ObjectiveAssessment objectiveAssessment = new ObjectiveAssessment();
        objectiveAssessment.setId(objAssessMeta.id);
        objectiveAssessment.setIdentificationCode(objAssessMeta.id);
        objectiveAssessment.setMaxRawScore(100);

        for (String plDescString : objAssessMeta.performanceLevelDescriptorIds) {
            AssessmentPerformanceLevel apLevel = new AssessmentPerformanceLevel();

            apLevel.setPerformanceLevel(PerformanceLevelDescriptorGenerator
                    .getPerformanceLevelDescriptorType(plDescString));

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

        for (String learningObjectiveIdString : objAssessMeta.learningObjectiveIds) {
            objectiveAssessment.getLearningObjectiveReference().add(
                    LearningObjectiveGenerator.getLearningObjectiveReferenceType(learningObjectiveIdString));
        }

        for (String learningStandardIdString : objAssessMeta.learningStandardIds) {
            objectiveAssessment.getLearningStandardReference().add(
                    LearningStandardGenerator.getLearningStandardReferenceType(learningStandardIdString));
        }

        for (String relatedObjAssessmentIdString : objAssessMeta.relatedObjectiveAssessmentIds) {
            // TODO: this is only modeled as XML ReferenceType...
        }

        return objectiveAssessment;
    }

    public static ObjectiveAssessmentReferenceType getObjectiveAssessmentReferenceType(final String assessmentCode) {
        ObjectiveAssessmentReferenceType objectiveAssessmentRef = new ObjectiveAssessmentReferenceType();
        ObjectiveAssessmentIdentityType identity = new ObjectiveAssessmentIdentityType();
        objectiveAssessmentRef.setObjectiveAssessmentIdentity(identity);
        identity.setObjectiveAssessmentIdentificationCode(assessmentCode);
        return objectiveAssessmentRef;
    }

    public static ObjectiveAssessmentReferenceType getObjectiveAssessmentReferenceType(
            final ObjectiveAssessment objectiveAssessment) {
        ObjectiveAssessmentReferenceType objectiveAssessmentRef = new ObjectiveAssessmentReferenceType();
        ObjectiveAssessmentIdentityType identity = new ObjectiveAssessmentIdentityType();
        objectiveAssessmentRef.setObjectiveAssessmentIdentity(identity);
        identity.setObjectiveAssessmentIdentificationCode(objectiveAssessment.getIdentificationCode());
        return objectiveAssessmentRef;
    }
}

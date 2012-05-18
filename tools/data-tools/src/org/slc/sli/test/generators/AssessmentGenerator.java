package org.slc.sli.test.generators;

import java.util.Map;

import org.slc.sli.test.DataFidelityType;
import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.Assessment;
import org.slc.sli.test.edfi.entities.AssessmentCategoryType;
import org.slc.sli.test.edfi.entities.AssessmentIdentificationCode;
import org.slc.sli.test.edfi.entities.AssessmentIdentificationSystemType;
import org.slc.sli.test.edfi.entities.AssessmentIdentityType;
import org.slc.sli.test.edfi.entities.AssessmentPerformanceLevel;
import org.slc.sli.test.edfi.entities.AssessmentReferenceType;
import org.slc.sli.test.edfi.entities.AssessmentReportingMethodType;
import org.slc.sli.test.edfi.entities.ContentStandardType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.ObjectiveAssessment;
import org.slc.sli.test.edfi.entities.ReferenceType;
import org.slc.sli.test.edfi.entities.SectionReferenceType;
import org.slc.sli.test.edfi.entities.meta.AssessmentMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;

public class AssessmentGenerator {

    public static Assessment generate(AssessmentMeta assessmentMeta, Map<String, ObjectiveAssessment> objAssessMap) {
        return generate(MetaRelations.DEFAULT_DATA_FIDELITY_TYPE, assessmentMeta, objAssessMap);
    }
    
    public static Assessment generate(DataFidelityType fidelity, AssessmentMeta assessmentMeta, Map<String, ObjectiveAssessment> objAssessMap) {
        Assessment assessment = new Assessment();

        populateRequiredFields(fidelity, assessmentMeta, objAssessMap, assessment);

        return assessment;
    }

    private static void populateRequiredFields(DataFidelityType fidelity, AssessmentMeta assessmentMeta,
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

            apLevel.setPerformanceLevel(PerformanceLevelDescriptorGenerator
                    .getPerformanceLevelDescriptorType(assessmentPerformanceLevelId));

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
            // TODO: this is only modeled as XML ReferenceType... (AssessmentItem is post-alpha?)
        }

        // ObjectiveAssessmentReference
        for (String objAssessmentIdString : assessmentMeta.objectiveAssessmentIds) {
            if (objAssessMap.get(objAssessmentIdString) != null) {
                ReferenceType objAssessRef = new ReferenceType();
                objAssessRef.setRef(objAssessMap.get(objAssessmentIdString));
                assessment.getObjectiveAssessmentReference().add(objAssessRef);
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
        ait.getAssessmentIdentificationCode().add(aic);

        AssessmentReferenceType art = new AssessmentReferenceType();
        art.setAssessmentIdentity(ait);
        return art;
    }

}

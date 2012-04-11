package org.slc.sli.test.generators;

import javax.xml.bind.JAXBElement;

import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.Assessment;
import org.slc.sli.test.edfi.entities.AssessmentCategoryType;
import org.slc.sli.test.edfi.entities.AssessmentFamily;
import org.slc.sli.test.edfi.entities.AssessmentFamilyIdentityType;
import org.slc.sli.test.edfi.entities.AssessmentFamilyReferenceType;
import org.slc.sli.test.edfi.entities.AssessmentIdentificationCode;
import org.slc.sli.test.edfi.entities.AssessmentIdentificationSystemType;
import org.slc.sli.test.edfi.entities.AssessmentIdentityType;
import org.slc.sli.test.edfi.entities.AssessmentItem;
import org.slc.sli.test.edfi.entities.AssessmentItemIdentityType;
import org.slc.sli.test.edfi.entities.AssessmentItemReferenceType;
import org.slc.sli.test.edfi.entities.AssessmentPerformanceLevel;
import org.slc.sli.test.edfi.entities.AssessmentPeriodDescriptor;
import org.slc.sli.test.edfi.entities.AssessmentPeriodDescriptorType;
import org.slc.sli.test.edfi.entities.AssessmentReferenceType;
import org.slc.sli.test.edfi.entities.AssessmentReportingMethodType;
import org.slc.sli.test.edfi.entities.ContentStandardType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.ItemCategoryType;
import org.slc.sli.test.edfi.entities.LearningObjectiveReferenceType;
import org.slc.sli.test.edfi.entities.LearningStandardReferenceType;
import org.slc.sli.test.edfi.entities.ObjectFactory;
import org.slc.sli.test.edfi.entities.ObjectiveAssessment;
import org.slc.sli.test.edfi.entities.ObjectiveAssessmentIdentityType;
import org.slc.sli.test.edfi.entities.ObjectiveAssessmentReferenceType;
import org.slc.sli.test.edfi.entities.PerformanceBaseType;
import org.slc.sli.test.edfi.entities.PerformanceLevelDescriptor;
import org.slc.sli.test.edfi.entities.PerformanceLevelDescriptorType;
import org.slc.sli.test.edfi.entities.SectionReferenceType;


public class AssessmentGenerator {

    private static ObjectFactory factory = new ObjectFactory();
    private static final int RAND_INT_100 = 100;

    public final Assessment generate(final String assessmentTitle,
            final String assessmentId, final boolean includeOptional) {
        Assessment assessment = new Assessment();

        assessment.setAssessmentTitle(assessmentTitle);
        AssessmentIdentificationCode aidCode =
                new AssessmentIdentificationCode();

        aidCode.setID(assessmentId);
        aidCode.setIdentificationSystem(
                AssessmentIdentificationSystemType.SCHOOL);
        assessment.getAssessmentIdentificationCode().add(aidCode);
        if (includeOptional) {
            assessment.setAssessmentCategory(
                    AssessmentCategoryType.ACHIEVEMENT_TEST);
            assessment.setAcademicSubject(
                AcademicSubjectType.
                AGRICULTURE_FOOD_AND_NATURAL_RESOURCES);
            assessment.setGradeLevelAssessed(
                    GradeLevelType.ADULT_EDUCATION);
            assessment.setLowestGradeLevelAssessed(
                    GradeLevelType.ADULT_EDUCATION);
//            assessment.getAssessmentPerformanceLevel().add(apl);
            assessment.setContentStandard(ContentStandardType.ACT);
            assessment.setAssessmentForm("ThisAssessmentForm");
            assessment.setVersion(1);
            assessment.setRevisionDate("03-24-2012");
            assessment.setMaxRawScore(RAND_INT_100);
            assessment.setNomenclature("StandardNomenclature");
            AssessmentPeriodDescriptorType apdType
            = new AssessmentPeriodDescriptorType();
            JAXBElement<String> str = factory.
            createAssessmentPeriodDescriptorTypeShortDescription(
                            "AssessmentPeriod1");
              apdType.getCodeValueOrShortDescriptionOrDescription().add(str);
            assessment.setAssessmentPeriod(apdType);
//            assessment.getAssessmentItemReference()
//            assessment.getObjectiveAssessmentReference()
            AssessmentFamilyReferenceType afrType =
                    new AssessmentFamilyReferenceType();
            AssessmentFamilyIdentityType afiType
            = new AssessmentFamilyIdentityType();
            afiType.setAssessmentFamilyTitle("AssessmentFamilyTitle1");
            afrType.setAssessmentFamilyIdentity(afiType);
            assessment.setAssessmentFamilyReference(afrType);
            SectionReferenceType srType = new SectionReferenceType();
              srType.getSectionIdentity().setUniqueSectionCode("Section 12a");
            assessment.getSectionReference().add(srType);
        }
        return assessment;
    }

    public static AssessmentReferenceType getAssessmentReference(
            final String assessmentTitle, final String assessmentId) {
        AssessmentIdentificationCode aic = new AssessmentIdentificationCode();
        aic.setID(assessmentId);
        aic.setIdentificationSystem(AssessmentIdentificationSystemType.SCHOOL);
        AssessmentIdentityType ait = new AssessmentIdentityType();
        ait.getAssessmentIdentificationCode().add(aic);
        if(assessmentTitle != null) ait.setAssessmentTitle(assessmentTitle);
        AssessmentReferenceType art = new AssessmentReferenceType();
        art.setAssessmentIdentity(ait);
        return art;
    }

   /////////////////////////////////////////////////////////////////////////
    public static AssessmentFamily getAssessmentFamily() {
        AssessmentFamily family = new AssessmentFamily();
        family.setAssessmentFamilyTitle("AssessmentFamilyTitle");
        AssessmentIdentificationCode idCode =
                new AssessmentIdentificationCode();
        family.getAssessmentFamilyIdentificationCode().add(idCode);
        idCode.setID("AssessmentFamilyIdentificationCode");
          idCode.setIdentificationSystem(
                  AssessmentIdentificationSystemType.FEDERAL);
          idCode.setAssigningOrganizationCode("AssigningOrganizationCode");
        family.setAssessmentCategory(AssessmentCategoryType.ACHIEVEMENT_TEST);
        family.setAcademicSubject(AcademicSubjectType.ENGLISH);
        family.setGradeLevelAssessed(GradeLevelType.EIGHTH_GRADE);
        family.setLowestGradeLevelAssessed(GradeLevelType.EIGHTH_GRADE);
        family.setContentStandard(ContentStandardType.ADVANCED_PLACEMENT);
        family.setVersion(new Integer(RAND_INT_100));
        family.setRevisionDate("20121112");
        family.setNomenclature("Nomenclature");
        AssessmentPeriodDescriptorType periods =
                new AssessmentPeriodDescriptorType();
        family.getAssessmentPeriods().add(periods);
        JAXBElement<String> opt1 = factory.
                createAssessmentPeriodDescriptorTypeCodeValue(
                        "CodeVale");
        //JAXBElement<String> opt2 = factory.
        //createAssessmentPeriodDescriptorTypeDescription("Description");
        //JAXBElement<String> opt3 = factory.
        //createAssessmentPeriodDescriptorTypeShortDescription
        //("Short Description");
        periods.getCodeValueOrShortDescriptionOrDescription().add(opt1);
        //AssessmentPeriods.getCodeValueOrShortDescriptionOrDescription().
        //add(opt2);
        //AssessmentPeriods.getCodeValueOrShortDescriptionOrDescription().
        //add(opt3);
        return family;
    }

    public static AssessmentFamilyReferenceType
    getAssessmentFamilyReferenceType(final String assessmentId) {
        AssessmentFamilyReferenceType familyRef
        = new AssessmentFamilyReferenceType();
        AssessmentFamilyIdentityType identity
        = new AssessmentFamilyIdentityType();
        familyRef.setAssessmentFamilyIdentity(identity);
        identity.setAssessmentFamilyTitle("AssessmentFamilyTitle");
        identity.setVersion(new Integer(1));
        AssessmentIdentificationCode idCode
        = new AssessmentIdentificationCode();
        identity.getAssessmentFamilyIdentificationCode().add(idCode);
        idCode.setID(assessmentId);
        idCode.setIdentificationSystem(
                AssessmentIdentificationSystemType.FEDERAL);
        idCode.setAssigningOrganizationCode("AssigningOrganizationCode");
        return familyRef;
    }

    public static AssessmentFamilyReferenceType
    getAssessmentFamilyReferenceType(final AssessmentFamily family) {
        AssessmentFamilyReferenceType familyRef =
                new AssessmentFamilyReferenceType();
        AssessmentFamilyIdentityType identity =
                new AssessmentFamilyIdentityType();
        familyRef.setAssessmentFamilyIdentity(identity);
        identity.setAssessmentFamilyTitle(family.getAssessmentFamilyTitle());
        identity.setVersion(family.getVersion());
        AssessmentIdentificationCode idCode =
                new AssessmentIdentificationCode();
        identity.getAssessmentFamilyIdentificationCode().add(idCode);
        idCode.setID(family.getAssessmentFamilyIdentificationCode().
                get(0).getID());
        idCode.setIdentificationSystem(family.
                getAssessmentFamilyIdentificationCode().get(0).
                getIdentificationSystem());
        idCode.setAssigningOrganizationCode(family.
                getAssessmentFamilyIdentificationCode().
                get(0).getAssigningOrganizationCode());
        return familyRef;
    }

   /////////////////////////////////////////////////////////////////////////
    public static AssessmentPeriodDescriptor getAssessmentPeriodDescriptor() {
        AssessmentPeriodDescriptor assessmentPeriodDescriptor =
                new AssessmentPeriodDescriptor();
        assessmentPeriodDescriptor.setCodeValue("CodeValue");
        assessmentPeriodDescriptor.setDescription("Description");
        assessmentPeriodDescriptor.setShortDescription("ShortDescription");
        assessmentPeriodDescriptor.setBeginDate("20121112");
        assessmentPeriodDescriptor.setEndDate("20121112");
        return assessmentPeriodDescriptor;
    }

   /////////////////////////////////////////////////////////////////////////
    public static PerformanceLevelDescriptor getPerfomanceLevelDescriptor() {
        PerformanceLevelDescriptor pld = new PerformanceLevelDescriptor();
        pld.setCodeValue("CodeValue");
        pld.setDescription("Description");
        pld.setPerformanceBaseConversion(PerformanceBaseType.BASIC);
        return pld;
    }

   /////////////////////////////////////////////////////////////////////////
    public static ObjectiveAssessment getObjectiveAssessment(
            final AssessmentItemReferenceType assessmentItemRef,
            final LearningObjectiveReferenceType learningObjectiveRef,
            final LearningStandardReferenceType learningStandardRef) {
        ObjectiveAssessment objectiveAssessment = new ObjectiveAssessment();
        objectiveAssessment.setIdentificationCode("IdentificationCode");
        objectiveAssessment.setMaxRawScore(1);
        AssessmentPerformanceLevel apLevel = new AssessmentPerformanceLevel();
        objectiveAssessment.getAssessmentPerformanceLevel().add(apLevel);
        apLevel.setAssessmentReportingMethod(
                AssessmentReportingMethodType.ADAPTIVE_SCALE_SCORE);
        apLevel.setMaximumScore(new Integer(1));
        apLevel.setMinimumScore(new Integer(1));
        PerformanceLevelDescriptorType performanceLevel =
                new PerformanceLevelDescriptorType();
        apLevel.setPerformanceLevel(performanceLevel);
        JAXBElement<String> opt1 = factory.
                createPerformanceLevelDescriptorTypeCodeValue(
                        "Code Value");
        //JAXBElement<String> opt2 = factory.
        //createPerformanceLevelDescriptorTypeDescription("Description");
        performanceLevel.getCodeValueOrDescription().add(opt1);
        //PerformanceLevel.getCodeValueOrDescription().add(opt2);
        apLevel.setAssessmentReportingMethod(
                AssessmentReportingMethodType.
                ACHIEVEMENT_PROFICIENCY_LEVEL);
        objectiveAssessment.setPercentOfAssessment(RAND_INT_100);
        objectiveAssessment.setNomenclature("Nomenclature");
        if (assessmentItemRef != null) {
            objectiveAssessment.getAssessmentItemReference().
            add(assessmentItemRef);
        }
        if (learningObjectiveRef != null) {
            objectiveAssessment.getLearningObjectiveReference().
            add(learningObjectiveRef);
        }
        if (learningStandardRef != null) {
            objectiveAssessment.getLearningStandardReference().
            add(learningStandardRef);
        }
        return objectiveAssessment;
    }

    public static ObjectiveAssessmentReferenceType
    getObjectiveAssessmentReferenceType(final String assessmentCode) {
        ObjectiveAssessmentReferenceType objectiveAssessmentRef
        = new ObjectiveAssessmentReferenceType();
        ObjectiveAssessmentIdentityType identity
        = new ObjectiveAssessmentIdentityType();
        objectiveAssessmentRef.setObjectiveAssessmentIdentity(identity);
        identity.setObjectiveAssessmentIdentificationCode(assessmentCode);
        return objectiveAssessmentRef;
    }

    public static ObjectiveAssessmentReferenceType
    getObjectiveAssessmentReferenceType(
            final ObjectiveAssessment objectiveAssessment) {
        ObjectiveAssessmentReferenceType objectiveAssessmentRef =
                new ObjectiveAssessmentReferenceType();
        ObjectiveAssessmentIdentityType identity =
                new ObjectiveAssessmentIdentityType();
        objectiveAssessmentRef.setObjectiveAssessmentIdentity(identity);
        identity.setObjectiveAssessmentIdentificationCode(
                objectiveAssessment.getIdentificationCode());
        return objectiveAssessmentRef;
    }
}

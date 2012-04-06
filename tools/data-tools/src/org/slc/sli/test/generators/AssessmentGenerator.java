package org.slc.sli.test.generators;

import javax.xml.bind.JAXBElement;

import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.Assessment;
import org.slc.sli.test.edfi.entities.AssessmentCategoryType;
import org.slc.sli.test.edfi.entities.AssessmentFamilyIdentityType;
import org.slc.sli.test.edfi.entities.AssessmentFamilyReferenceType;
import org.slc.sli.test.edfi.entities.AssessmentIdentificationCode;
import org.slc.sli.test.edfi.entities.AssessmentIdentificationSystemType;
import org.slc.sli.test.edfi.entities.AssessmentIdentityType;
import org.slc.sli.test.edfi.entities.AssessmentPerformanceLevel;
import org.slc.sli.test.edfi.entities.AssessmentPeriodDescriptorType;
import org.slc.sli.test.edfi.entities.AssessmentReferenceType;
import org.slc.sli.test.edfi.entities.ContentStandardType;
import org.slc.sli.test.edfi.entities.DisciplineDescriptorType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.ObjectFactory;
import org.slc.sli.test.edfi.entities.SectionReferenceType;


public class AssessmentGenerator {

    public Assessment generate(String assessmentTitle, String assessmentId, boolean includeOptional) {
    	Assessment assessment = new Assessment();

    	assessment.setAssessmentTitle(assessmentTitle);
    	AssessmentIdentificationCode aidCode = new AssessmentIdentificationCode();

    	aidCode.setID(assessmentId);
    	aidCode.setIdentificationSystem(AssessmentIdentificationSystemType.SCHOOL);
    	assessment.getAssessmentIdentificationCode().add(aidCode);

    	if (includeOptional) {

        	assessment.setAssessmentCategory(AssessmentCategoryType.ACHIEVEMENT_TEST);

        	assessment.setAcademicSubject(AcademicSubjectType.AGRICULTURE_FOOD_AND_NATURAL_RESOURCES);

        	assessment.setGradeLevelAssessed(GradeLevelType.ADULT_EDUCATION);

        	assessment.setLowestGradeLevelAssessed(GradeLevelType.ADULT_EDUCATION);

//        	assessment.getAssessmentPerformanceLevel().add(apl);

        	assessment.setContentStandard(ContentStandardType.ACT);

        	assessment.setAssessmentForm("ThisAssessmentForm");

        	assessment.setVersion(1);

        	assessment.setRevisionDate("03-24-2012");

        	assessment.setMaxRawScore(800);

        	assessment.setNomenclature("StandardNomenclature");

        	AssessmentPeriodDescriptorType apdType = new AssessmentPeriodDescriptorType();
          	ObjectFactory fact = new ObjectFactory();
        	JAXBElement<String> str = fact.createAssessmentPeriodDescriptorTypeShortDescription("AssessmentPeriod1");
          	apdType.getCodeValueOrShortDescriptionOrDescription().add(str);
        	assessment.setAssessmentPeriod(apdType);

//        	assessment.getAssessmentItemReference()

//        	assessment.getObjectiveAssessmentReference()

        	AssessmentFamilyReferenceType afrType = new AssessmentFamilyReferenceType();
        	AssessmentFamilyIdentityType afiType = new AssessmentFamilyIdentityType();
        	afiType.setAssessmentFamilyTitle("AssessmentFamilyTitle1");
        	afrType.setAssessmentFamilyIdentity(afiType);
        	assessment.setAssessmentFamilyReference(afrType);

        	SectionReferenceType srType = new SectionReferenceType();
          	srType.getSectionIdentity().setUniqueSectionCode("Section 12a");
        	assessment.getSectionReference().add(srType);
    	}

    	return assessment;
    }

    public static AssessmentReferenceType getAssessmentReference(String assessmentTitle, String assessmentId, AssessmentIdentificationSystemType assessmentIdentificationSystem) {
        AssessmentReferenceType art = new AssessmentReferenceType();

        AssessmentIdentityType ait = new AssessmentIdentityType();

        AssessmentIdentificationCode aic = new AssessmentIdentificationCode();
    	aic.setID(assessmentId);
    	aic.setIdentificationSystem(AssessmentIdentificationSystemType.SCHOOL);
    	aic.setIdentificationSystem(assessmentIdentificationSystem);

    	ait.getAssessmentIdentificationCode().add(aic);
//        ait.setAssessmentTitle(assessmentTitle);


        art.setAssessmentIdentity(ait);
        return art;
    }

}

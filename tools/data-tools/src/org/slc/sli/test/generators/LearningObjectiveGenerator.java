package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.LearningObjective;
import org.slc.sli.test.edfi.entities.LearningObjectiveIdentityType;
import org.slc.sli.test.edfi.entities.LearningObjectiveReferenceType;
import org.slc.sli.test.edfi.entities.LearningStandardId;
import org.slc.sli.test.edfi.entities.LearningStandardIdentityType;
import org.slc.sli.test.edfi.entities.LearningStandardReferenceType;

public class LearningObjectiveGenerator {

	private int loId = 0;
	private LearningStandardReferenceType learningStandardRef;
	private LearningStandardId learningStandardId;
	
	public LearningObjectiveGenerator() {
		LearningStandardIdentityType lsIdentity = new LearningStandardIdentityType();
		lsIdentity.setLearningStandardId(learningStandardId);
		learningStandardId.setContentStandardName("Learning Standard Content Standard");
		learningStandardId.setIdentificationCode( "Learning Standard Content Standard G1");
		learningStandardRef.setLearningStandardIdentity(lsIdentity);
	}

	public LearningObjective getLearningObjective(String learningObjectiveId)
	{
		loId++;
		LearningObjective lo = new LearningObjective();
		String id = learningObjectiveId == null?("LOID" + loId ):learningObjectiveId;
		lo.setId(id);
		lo.setLearningObjectiveId(learningStandardId) ;
		lo.setObjective( "Learning Objective " + loId) ;
		lo.setDescription("Learning Objective Desciption " + loId) ;
		lo.setAcademicSubject(AcademicSubjectType.AGRICULTURE_FOOD_AND_NATURAL_RESOURCES) ;
		lo.setObjectiveGradeLevel(GradeLevelType.OTHER) ;
		lo.getLearningStandardReference().add(learningStandardRef) ;
		return lo;
	}
	 
	public LearningObjectiveReferenceType getLearningObjectiveReferenceType(LearningObjective lo)
	{
		LearningObjectiveIdentityType loId = new LearningObjectiveIdentityType();
		loId.getLearningObjectiveIdOrObjective().add(lo.getObjective());
		LearningObjectiveReferenceType lor = new LearningObjectiveReferenceType();
		lor.setLearningObjectiveIdentity(loId);
		return lor;
	}
}

package org.slc.sli.test.generators;

import javax.xml.bind.JAXBElement;

import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.ObjectFactory;
import org.slc.sli.test.edfi.entities.StudentCompetencyObjective;
import org.slc.sli.test.edfi.entities.StudentCompetencyObjectiveIdentityType;
import org.slc.sli.test.edfi.entities.StudentCompetencyObjectiveReferenceType;

public class StudentCompetancyObjectiveGenerator {

	private int scId = 0;
	private ObjectFactory factory = new ObjectFactory();
	public StudentCompetancyObjectiveGenerator() {
	}

	public StudentCompetencyObjective getStudentCompetencyObjective(String scoId)
	{
		scId++;
		StudentCompetencyObjective sco = new StudentCompetencyObjective();
		String id = scoId==null?"SCO Id" + scId:scoId;
		sco.setId(id);
		sco.setStudentCompetencyObjectiveId(id);
		sco.setDescription("Student Competancy Description " + scId);
		sco.setObjective("Student Competency Objective " +  scId);
		sco.setObjectiveGradeLevel(GradeLevelType.OTHER);
		//sco.setEducationOrganizationReference(null);
		return sco;
	}
	
	public StudentCompetencyObjectiveReferenceType getStudentCompetencyObjectiveReferenceType(StudentCompetencyObjective sco)
	{
		StudentCompetencyObjectiveReferenceType ref = new StudentCompetencyObjectiveReferenceType();
		StudentCompetencyObjectiveIdentityType scoIdentity = new StudentCompetencyObjectiveIdentityType();
		JAXBElement<String> oid = factory.createStudentCompetencyObjectiveIdentityTypeStudentCompetencyObjectiveId(sco.getStudentCompetencyObjectiveId());
		scoIdentity.getStudentCompetencyObjectiveIdOrObjective().add(oid);
		return ref;
	}
}

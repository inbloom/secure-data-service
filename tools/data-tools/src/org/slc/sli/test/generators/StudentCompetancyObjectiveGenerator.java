package org.slc.sli.test.generators;

import javax.xml.bind.JAXBElement;

import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.ObjectFactory;
import org.slc.sli.test.edfi.entities.StudentCompetencyObjective;
import org.slc.sli.test.edfi.entities.StudentCompetencyObjectiveIdentityType;
import org.slc.sli.test.edfi.entities.StudentCompetencyObjectiveReferenceType;

public class StudentCompetancyObjectiveGenerator {

	private static int scId = 0;
	private static ObjectFactory factory = new ObjectFactory();

	public static StudentCompetencyObjective getStudentCompetencyObjective(String scoId, EducationalOrgReferenceType edOrgRef)
	{
		scId++;
		StudentCompetencyObjective sco = new StudentCompetencyObjective();
		String id = scoId==null?"SCO Id" + scId:scoId;
		sco.setId(id);
		sco.setStudentCompetencyObjectiveId(id);
		sco.setDescription("Student Competancy Description " + scId);
		sco.setObjective("Student Competency Objective " +  scId);
		sco.setObjectiveGradeLevel(GradeLevelType.OTHER);
		if(edOrgRef != null)sco.setEducationOrganizationReference(edOrgRef);
		return sco;
	}
	
	public static StudentCompetencyObjectiveReferenceType getStudentCompetencyObjectiveReferenceType(StudentCompetencyObjective sco)
	{
		StudentCompetencyObjectiveReferenceType ref = new StudentCompetencyObjectiveReferenceType();
		StudentCompetencyObjectiveIdentityType scoIdentity = new StudentCompetencyObjectiveIdentityType();
		JAXBElement<String> oid = factory.
				createStudentCompetencyObjectiveIdentityTypeStudentCompetencyObjectiveId(
						sco.getStudentCompetencyObjectiveId());
		scoIdentity.getStudentCompetencyObjectiveIdOrObjective().add(oid);
		JAXBElement<String> objective = factory.
				createStudentCompetencyObjectiveIdentityTypeObjective(sco.getObjective());
		scoIdentity.getStudentCompetencyObjectiveIdOrObjective().add(objective);
		return ref;
	}
}

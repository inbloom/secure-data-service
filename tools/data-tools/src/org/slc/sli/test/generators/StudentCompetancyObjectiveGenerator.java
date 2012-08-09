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

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

import org.slc.sli.test.edfi.entities.LearningObjectiveReferenceType;
import org.slc.sli.test.edfi.entities.StudentCompetencyIdentityType;
import org.slc.sli.test.edfi.entities.StudentCompetencyObjectiveReferenceType;
import org.slc.sli.test.edfi.entities.StudentCompetencyReferenceType;
import org.slc.sli.test.edfi.entities.StudentSectionAssociationReferenceType;

public class StudentCompetancyGenerator {


    public static StudentCompetencyReferenceType getStudentCompetencyReference(LearningObjectiveReferenceType lor,
            String codeValue, StudentSectionAssociationReferenceType studentSectionAssociationReference)
	{
        StudentCompetencyReferenceType scRef = new StudentCompetencyReferenceType();
        StudentCompetencyIdentityType scIdent = new StudentCompetencyIdentityType();
        scRef.setStudentCompetencyIdentity(scIdent);

        scIdent.setCodeValue(codeValue);
        scIdent.setStudentSectionAssociationReference(studentSectionAssociationReference);
        scIdent.setLearningObjectiveReference(lor);

        return scRef;
	}

    public static StudentCompetencyReferenceType getStudentCompetencyReference(
            StudentCompetencyObjectiveReferenceType scor, String codeValue,
            StudentSectionAssociationReferenceType studentSectionAssociationReference) {
        StudentCompetencyReferenceType scRef = new StudentCompetencyReferenceType();
        StudentCompetencyIdentityType scIdent = new StudentCompetencyIdentityType();
        scRef.setStudentCompetencyIdentity(scIdent);

        scIdent.setCodeValue(codeValue);
        scIdent.setStudentSectionAssociationReference(studentSectionAssociationReference);
        scIdent.setStudentCompetencyObjectiveReference(scor);

        return scRef;
    }
	
}

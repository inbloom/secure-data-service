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

import java.util.Random;

import org.slc.sli.test.edfi.entities.ParentReferenceType;
import org.slc.sli.test.edfi.entities.Ref;
import org.slc.sli.test.edfi.entities.RelationType;
import org.slc.sli.test.edfi.entities.StudentParentAssociation;
import org.slc.sli.test.edfi.entities.StudentReferenceType;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;

public class StudentParentAssociationGenerator {
   public static Random random = new Random(31);

    public StudentParentAssociation generate(String studentParentId, String studentId) {

    	StudentParentAssociation studentParentAssociation = new StudentParentAssociation();

    	try {

            StudentReferenceType srt = StudentGenerator.getStudentReferenceType(studentId);

            studentParentAssociation.setStudentReference(srt);

            studentParentAssociation.setRelation(random.nextBoolean() ? RelationType.MOTHER : RelationType.FATHER);

            boolean bool = random.nextBoolean();

            studentParentAssociation.setPrimaryContactStatus(bool);

            studentParentAssociation.setLivesWith(bool);

            studentParentAssociation.setEmergencyContactStatus(bool);

            studentParentAssociation.setContactPriority(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return studentParentAssociation;
    }



      public static StudentParentAssociation generateLowFi(String parentId, boolean isMale, String studentId) {
        StudentParentAssociation studentParentAssociation = new StudentParentAssociation();

        try {
        	
    
			if (MetaRelations.StudentParentAssociation_Ref) {
				Ref parentRef = new Ref(parentId);
				ParentReferenceType prt = new ParentReferenceType();
				prt.setRef(parentRef);
				studentParentAssociation.setParentReference(prt);

				Ref studentRef = new Ref("REF-" + studentId);
				StudentReferenceType srt = new StudentReferenceType();
				srt.setRef(studentRef);
				studentParentAssociation.setStudentReference(srt);
			} else {
				StudentReferenceType srt = StudentGenerator
						.getStudentReferenceType(studentId);
				studentParentAssociation.setStudentReference(srt);
				ParentReferenceType prt = ParentGenerator
						.getParentReferenceType(parentId);
				studentParentAssociation.setParentReference(prt);
			}
        	
            if(isMale)
                studentParentAssociation.setRelation(RelationType.FATHER);
            else
                studentParentAssociation.setRelation(RelationType.MOTHER);

            boolean bool = random.nextBoolean();

            studentParentAssociation.setPrimaryContactStatus(bool);

            studentParentAssociation.setLivesWith(bool);

            studentParentAssociation.setEmergencyContactStatus(bool);

            studentParentAssociation.setContactPriority(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return studentParentAssociation;
    }
}

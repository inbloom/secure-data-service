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

import org.slc.sli.test.edfi.entities.*;
import org.slc.sli.test.edfi.entities.meta.ProgramMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;

public class StudentAssessmentItemGenerator {
    private boolean optional;
    private Random random = new Random(31);

    private AssessmentItemResultType[] airts = AssessmentItemResultType.values();

    public StudentAssessmentItemGenerator(boolean optional) {
        this.optional = optional;
    }

    public StudentAssessmentItem generate(String id, AssessmentItemReferenceType assessmentItemReference, ReferenceType studentTestAssessmentReference, ReferenceType studentObjectiveAssessmentReference) {
        StudentAssessmentItem sai = new StudentAssessmentItem();

        sai.setId(id);

        sai.setAssessmentItemResult(airts[random.nextInt(airts.length)]);

        sai.setAssessmentItemReference(assessmentItemReference);
        
       
        if (optional) {
           
            sai.setRawScoreResult(random.nextInt(100));
	
			// TODO: StudentTestAssessmentReference
			if (studentTestAssessmentReference != null) {
			    
				sai.setStudentAssessmentReference(studentTestAssessmentReference);
				
			}

			// TODO: StudentObjectiveAssessmentReference
			if (studentObjectiveAssessmentReference != null) {
				sai.setStudentObjectiveAssessmentReference(studentObjectiveAssessmentReference);
			}
		
        }

        return sai;
    }

//    public StudentAssessmentItem generate(String id, AssessmentItemReferenceType assessmentItemReference) {
//        return generate(id, assessmentItemReference, null, null);
//    }



    
    public static StudentAssessmentItem generateLowFi(String id, String studentAssessmentId, AssessmentItemReferenceType assessmentItemReference) {
        StudentAssessmentItemGenerator saig = new StudentAssessmentItemGenerator(true);
     
        if(MetaRelations.StudentAssessment_Ref){
            ReferenceType star = new ReferenceType();
            StudentAssessmentIdentificationCode saic = new StudentAssessmentIdentificationCode();
            saic.setID(studentAssessmentId);
            star.setRef(saic);
            return saig.generate(id, assessmentItemReference,star, null);
        } else {
           // System.out.println("The complex type does not define in Schema, so it cannot be gennerated!");
            //the following code to use to generate complex object
//            StudentAssessmentIdentityType sait =  new StudentAssessmentIdentityType();
//            sait.getStudentAssessmentIdentificationCode().add(studentAssessmentId);
//            StudentAssessmentReferenceType sart = new StudentAssessmentReferenceType ();
//            sart.setStudentAssessmentIdentity(sait); 
            return saig.generate(id, assessmentItemReference,null, null);
        }
    }
    
     
}

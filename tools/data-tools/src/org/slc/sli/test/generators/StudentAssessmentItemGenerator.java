/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import org.slc.sli.test.edfi.entities.AssessmentItemResultType;
import org.slc.sli.test.edfi.entities.SLCAssessmentItemReferenceType;
import org.slc.sli.test.edfi.entities.SLCStudentAssessmentItem;
import org.slc.sli.test.edfi.entities.SLCStudentAssessmentReferenceType;
import org.slc.sli.test.edfi.entities.SLCStudentObjectiveAssessmentReferenceType;

public class StudentAssessmentItemGenerator {
    private boolean optional;
    private Random random = new Random(31);

    private AssessmentItemResultType[] airts = AssessmentItemResultType.values();

    public StudentAssessmentItemGenerator(boolean optional) {
        this.optional = optional;
    }

    public SLCStudentAssessmentItem generate(String id, SLCAssessmentItemReferenceType assessmentItemReference, SLCStudentAssessmentReferenceType studentTestAssessmentReference, 
            SLCStudentObjectiveAssessmentReferenceType studentObjectiveAssessmentReference) {
        SLCStudentAssessmentItem sai = new SLCStudentAssessmentItem();

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

    public SLCStudentAssessmentItem generate(String id, SLCAssessmentItemReferenceType assessmentItemReference) {
        return generate(id, assessmentItemReference, null, null);
    }



    
    public static SLCStudentAssessmentItem generateLowFi(String id, String studentAssessmentId, SLCAssessmentItemReferenceType assessmentItemReference) {
        StudentAssessmentItemGenerator saig = new StudentAssessmentItemGenerator(true);
     
//        if(MetaRelations.StudentAssessment_Ref){
//            // id refs are no longer supported
//            ReferenceType star = new ReferenceType();
//            StudentAssessmentIdentificationCode saic = new StudentAssessmentIdentificationCode();
//            saic.setID(studentAssessmentId);
//            star.setRef(saic);
//            return saig.generate(id, assessmentItemReference,star, null);
//        } else {
           // System.out.println("The complex type does not define in Schema, so it cannot be gennerated!");
            //the following code to use to generate complex object
//            StudentAssessmentIdentityType sait =  new StudentAssessmentIdentityType();
//            sait.getStudentAssessmentIdentificationCode().add(studentAssessmentId);
//            StudentAssessmentReferenceType sart = new StudentAssessmentReferenceType ();
//            sart.setStudentAssessmentIdentity(sait); 
            return saig.generate(id, assessmentItemReference);
//        }
    }
    
     
}
